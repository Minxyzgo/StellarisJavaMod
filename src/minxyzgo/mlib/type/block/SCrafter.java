package minxyzgo.mlib.type.block;

import arc.*;
import arc.struct.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import minxyzgo.mlib.type.*;

public class SCrafter extends GenericCrafter {
	public Seq<Recipe> recipes = new Seq<>();
	public SCrafter(String name) {
		super(name);
		hasLiquids = true;
		liquidCapacity = 20;
		Events.on(EventType.WorldLoadEvent.class, e -> {
			clearUnlock();
		});
	}
	@Override
	public boolean isVisible() {
		return !locked();
	}
	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.speed, craftTime, StatUnit.seconds);
		if (recipes != null) {
			for (Recipe recipe : recipes) {
				stats.add(Stat.output, recipe.name);
			}
		}
	}


	public class SCrafterBuild extends GenericCrafterBuild {
	    public RecipeModule conRecipe;
		public Recipe recipe;
		public float progress;

		@Override
		public boolean consValid() {
			return this.cons.valid() && conRecipe.valid();
		}
		
		/*
		@Override
		public void display(Table table) {
			super.display(table);
			table.pane( table1 -> {
				table1.row();
				table1.top();
				if (recipe != null) {
					for (int t = 0; t < recipe.itemInput.size(); t++) {
						table1.add(new ItemDisplay(recipe.itemInput.get(t).item, recipe.itemInput.get(t).amount));
					}
					table1.row();
					if (recipe.liquidInput != null) {
						table1.add(new LiquidDisplay(recipe.liquidInput.liquid, recipe.liquidInput.amount, false));
					}
					table1.row();
					table1.image(Icon.craftingSmall);
					table1.row();
					for (int t = 0; t < recipe.itemOutput.size(); t++) {
						table1.add(new ItemDisplay(recipe.itemOutput.get(t).item, recipe.itemOutput.get(t).amount));
					}
					table1.row();
					if (recipe.liquidOutput != null) {
						table1.add(new LiquidDisplay(recipe.liquidOutput.liquid, recipe.liquidOutput.amount, false));
					}
				}
			}).size(150, 150).top();
		}
		*/
		
		/*
		@Override
		public void updateTile() {
			if (recipe != null) {
				if (recipe.liquidInput == null) {
					if (recipe.itemInput.isEmpty() == false && items.has(recipe.itemInput)) {
						progress++;
					}
				}
				if (recipe.liquidInput != null) {
					if (recipe.itemInput.isEmpty() == true && liquids.current() == recipe.liquidInput.liquid) {
						if (liquids.get(recipe.liquidInput.liquid) > recipe.liquidInput.amount) {
							progress++;
						}
					}
					if (recipe.itemInput.isEmpty() == false && items.has(recipe.itemInput) && liquids.current() == recipe.liquidInput.liquid) {
						if (liquids.get(recipe.liquidInput.liquid) > recipe.liquidInput.amount) {
							progress++;
						}
					}
				}
				if (progress >= recipe.time) {
					if (recipe.itemInput.isEmpty() == false) {
						items.remove(recipe.itemInput);
					}
					if (recipe.liquidInput != null) {
						liquids.remove(recipe.liquidInput.liquid, recipe.liquidInput.amount);
					}
					if (recipe.itemOutput.isEmpty() == false) {
						int amount = 0;
						for (int i = 0; i < recipe.itemOutput.size(); i++) {
							amount = amount + recipe.itemOutput.get(i).amount;
						}
						if (amount < items.total()) {
							for (int b = 0; b < recipe.itemOutput.size(); b++) {
								this.items.add(recipe.itemOutput.get(b).item, recipe.itemOutput.get(b).amount);
							}
						}
					}
					if (recipe.liquidOutput != null) {
						this.handleLiquid(this, recipe.liquidOutput.liquid, recipe.liquidOutput.amount);
					}
					progress = 0;
				}
				if (this.timer(SCrafter.this.timerDump, 1.0F)) {
					for (int i = 0; i < recipe.itemOutput.size(); i++) {
						this.dump(recipe.itemOutput.get(i).item);
					}
				}
				if (recipe.liquidOutput != null) {
					this.dumpLiquid(recipe.liquidOutput.liquid);
				}
			}
		}


		@Override
		public void buildConfiguration(Table table) {
			table.button(Icon.add, ()-> {
				BaseDialog dialog = new BaseDialog("Choose Recipe") {
					{
						addCloseButton();
						for (int i = 0; i < recipes.size(); i++) {
							int b = i;
							cont.button(recipes.get(i).name, new TextureRegionDrawable(Core.atlas.find("age-" + recipes.get(i).drawable)), ()-> {
								recipe = recipes.get(b);
								visible = false;
								items.clear();
								liquids.clear();
							}).size(500, 50);
							cont.row();
						}
						cont.row();
						if (recipe != null) {
							cont.label(() -> "Recipe-" + recipe.name);
						}
					}
				};
				dialog.show();
			});
		}
		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(progress);
		}
		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.i();
		}
		
		*/
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
