package minxyzgo.mlib.type.block;

import arc.*;
import arc.util.*;
import arc.struct.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.Image;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import minxyzgo.mlib.type.*;

public class SCrafter extends Block {
	public Seq<Recipe> recipes = new Seq<>();
	public SCrafter(String name) {
		super(name);
		hasLiquids = true;
		liquidCapacity = 20;
		update = true;
		solid = true;
		hasItems = true;
		ambientSound = Sounds.machine;
		sync = true;
		ambientSoundVolume = 0.03f;
		flags = EnumSet.of(BlockFlag.factory);
		configurable = true;
		saveConfig = true;

		config(Recipe.class, (ent, recipe) -> {
			((SCrafterBuild)ent).recipe = recipe;
		});

		configClear(ent -> ((SCrafterBuild)ent).recipe = null);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (recipes != null) {
			for (Recipe recipe : recipes) {
				stats.add(Stat.output, recipe.name);
				stats.add(Stat.speed, recipe.name + ": " + recipe.time / Time.toSeconds + "s", StatUnit.seconds);
			}
		}
	}


	public class SCrafterBuild extends Building {
		public RecipeModule conRecipe = new RecipeModule(this);
		public Recipe recipe;
		public float progress;
		public float totalProgress;
		public float warmup;
		
		@Override
		public boolean acceptItem(Building source, Item item) {
            return super.acceptItem(source, item) || (recipe != null && (recipe.consumes.itemFilters.get(item.id) && items.get(item) < getMaximumAccepted(item)));
        }
        
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return super.acceptLiquid(source, liquid) || block.hasLiquids && recipe != null && recipe.consumes.liquidfilters.get(liquid.id);
        }
        
		@Override
		public boolean consValid() {
			return this.cons.valid() && recipe != null && conRecipe.valid();
		}

		@Override
		public boolean shouldConsume() {
			if (recipe != null && !recipe.itemOutput.isEmpty()) {
				for (ItemStack stack : recipe.itemOutput) {
					if (items.get(stack.item) < itemCapacity) return true;
				}
			}
			return recipe != null && (recipe.liquidOutput == null || !(liquids.get(recipe.liquidOutput.liquid) >= liquidCapacity - 0.001f)) && enabled;
		}

		@Override
		public void display(Table table) {
			super.display(table);
			table.pane(table1 -> {
				table1.row();
				table1.top();
				if (recipe != null) {

					for (Consume cons : recipe.consumes.all()) {
						if (cons.isOptional() && cons.isBoost()) continue;
						cons.build(this, table);
					}

					if (recipe.consumes.has(ConsumeType.power)) {
						table1.row();
						table1.table(o -> {
							o.left();
							o.add(new Image(Icon.power.getRegion())).size(32f);
						});
						table1.row();
						table1.table(t -> {
							float amount = recipe.consumes.getPower().usage;
							t.left().bottom();
							t.add(amount + "");
							t.pack();
						});
					}

					table1.row();
					table1.image(Icon.craftingSmall);
					table1.row();
					for (ItemStack stack : recipe.itemOutput) {
						table1.add(new ItemDisplay(stack.item, stack.amount));
					}
					table1.row();
					if (recipe.liquidOutput != null) {
						table1.add(new LiquidDisplay(recipe.liquidOutput.liquid, recipe.liquidOutput.amount, false));
					}
				}
			}).size(150, 150).top();
		}

		@Override
		public Recipe config() {
			return recipe;
		}

		@Override
		public byte version() {
			return 2;
		}

		@Override
		public void updateTile() {
			if (recipe != null) {
			    conRecipe.update();
			    
				if (consValid()) {

					progress += getProgressIncrease(recipe.time);
					totalProgress += delta();
					warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);

					if (Mathf.chanceDelta(recipe.updateEffectChance)) {
						recipe.updateEffect.at(getX() + Mathf.range(size * 4f), getY() + Mathf.range(size * 4));
					}
				} else {
					warmup = Mathf.lerp(warmup, 0f, 0.02f);
				}

				if (progress >= 1f) {
					consume();

					if (!recipe.itemOutput.isEmpty()) {
						for (ItemStack stack : recipe.itemOutput) {
							for (int i = 0; i < stack.amount; i++) {
								offload(stack.item);
							}
						}
					}

					if (recipe.liquidOutput != null) {
						handleLiquid(this, recipe.liquidOutput.liquid, recipe.liquidOutput.amount);
					}

					recipe.craftEffect.at(x, y);
					progress = 0f;
				}

				if (!recipe.itemOutput.isEmpty() && timer(timerDump, dumpTime)) {
					for (ItemStack stack : recipe.itemOutput) {
						dump(stack.item);
					}
				}

				if (recipe.liquidOutput != null) {
					dumpLiquid(recipe.liquidOutput.liquid);
				}
			}
		}


		@Override
		public void buildConfiguration(Table table) {
			table.button(Icon.add, ()-> {
				BaseDialog dialog = new BaseDialog("Choose Recipe") {
					{
						addCloseButton();
						for (Recipe r : recipes) {
						    if(!r.unlockedNow()) continue;
							cont.button(r.name, new TextureRegionDrawable(Core.atlas.find("age-" + r.name)), () -> {
								configure(r);
								visible = false;
								items.clear();
								liquids.clear();
							}).size(500, 50);
							cont.row();
						}
						cont.row();
						if (recipe != null) {
							cont.label(() -> "Recipe$" + recipe.name);
						}
					}
				};
				dialog.show();
			});
		}

		@Override
		public void consume() {
			super.consume();
			conRecipe.trigger();
		}

		@Override
		public void configure(Object obj) {
			super.configure(obj);
			recipe = (Recipe)obj;
		}

		@Override
		public int getMaximumAccepted(Item item) {
			return itemCapacity;
		}

		@Override
		public boolean shouldAmbientSound() {
			return consValid();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(progress);
			write.f(warmup);
			write.i(recipe == null ? -1 : recipes.indexOf(recipe));
			conRecipe.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.f();
			warmup = read.f();
			int id = read.i();
			if (id != -1) recipe = recipes.get(id);
			conRecipe.read(read);
		}
	}

	public static class RecipeModule extends BlockModule {
		private boolean valid, optionalValid;
		private final SCrafterBuild entity;

		public RecipeModule(SCrafterBuild entity) {
			this.entity = entity;
		}

		public BlockStatus status() {
			if (!entity.shouldConsume()) {
				return BlockStatus.noOutput;
			}

			if (!valid || !entity.productionValid()) {
				return BlockStatus.noInput;
			}

			return BlockStatus.active;
		}

		public void update() {
			//everything is valid when cheating
			if (entity.cheating()) {
				valid = optionalValid = true;
				return;
			}

			boolean prevValid = valid();
			valid = true;
			optionalValid = true;
			boolean docons = entity.shouldConsume() && entity.productionValid();

			for (Consume cons : entity.recipe.consumes.all()) {
				if (cons.isOptional()) continue;

				if (docons && cons.isUpdate() && prevValid && cons.valid(entity)) {
					cons.update(entity);
				}

				valid &= cons.valid(entity);
			}

			for (Consume cons : entity.recipe.consumes.optionals()) {
				if (docons && cons.isUpdate() && prevValid && cons.valid(entity)) {
					cons.update(entity);
				}

				optionalValid &= cons.valid(entity);
			}
		}

		public void trigger() {
			for (Consume cons : entity.recipe.consumes.all()) {
				cons.trigger(entity);
			}
		}

		public boolean valid() {
			return valid && entity.shouldConsume() && entity.enabled;
		}

		public boolean optionalValid() {
			return valid() && optionalValid && entity.enabled;
		}

		@Override
		public void write(Writes write) {
			write.bool(valid);
		}

		@Override
		public void read(Reads read) {
			valid = read.bool();
		}
	}
}
