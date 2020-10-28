package stellaris.content;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.draw.DrawAnimation;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.DrawWeave;
import stellaris.type.blocks.crafting.EnergySmelter;
import stellaris.type.draw.DrawHeatAnimation;



public class AsBlocks implements ContentList {

	public static GenericCrafter MatterEnergyTransformator, energyPackager, neutronMetalForge, civilianFactory, autoFarm;


	@Override
	public void load() {
		MatterEnergyTransformator = new EnergySmelter("matter-energy-transformator") {
			{
				health = 800;
				size = 3;
				outputItem = new ItemStack(AsItems.energyUnit, 1);
				craftTime = 60f;
				requirements(Category.crafting, ItemStack.with(Items.silicon, 150, Items.titanium, 200, Items.plastanium, 120, Items.phaseFabric, 50));
				itemCapacity = 500;
				craftEffect = Fx.smeltsmoke;
			}
		};

		energyPackager = new GenericCrafter("energy-packager") {
			{
				size = 4;
				health = 800;
				drawer = new DrawHeatAnimation(false, true).heatc(Color.valueOf("#D64821")).frame(4, 3).base(true);
				hasLiquids = true;
				liquidCapacity = 2;
				craftTime = 60f;
				requirements(Category.crafting,  ItemStack.with(Items.silicon, 250, Items.lead, 300, Items.metaglass, 200, Items.surgeAlloy, 50));
				consumes.power(85f);
				consumes.item(Items.metaglass, 1);
				outputItem = new ItemStack(AsItems.energyUnit, 1);
				hasPower = true;
				itemCapacity = 20;
				consumes.liquid(Liquids.water, 0.1f).boost();
				craftEffect = Fx.smokeCloud;
			}

		};

		neutronMetalForge = new GenericSmelter("neutron-metal-Forge") {
			{
				size = 4;
				health = 800;
				consumes.items(ItemStack.with(Items.surgeAlloy, 1, AsItems.mineral, 10));
				craftEffect = Fx.blastsmoke;
				craftTime = 90f;
				consumes.power(7.5f);
				outputItem = new ItemStack(AsItems.neutronMaterial, 1);
				requirements(Category.crafting, ItemStack.with(Items.titanium, 250, Items.phaseFabric, 100, Items.thorium, 400, Items.surgeAlloy, 150, Items.lead, 300));
				hasPower = true;
				itemCapacity = 20;
			}
		};


		civilianFactory = new GenericCrafter("civilian-factory") {
			{
				size = 3;
				health = 750;
				consumes.items(ItemStack.with(Items.silicon, 3, AsItems.mineral, 5, Items.copper, 4));
				consumes.power(5.0f);
				outputItem = new ItemStack(AsItems.industryProduction, 1);
				requirements(Category.crafting, ItemStack.with(Items.titanium, 150, Items.lead, 150, Items.copper, 200, Items.plastanium, 50));
				craftEffect = AsEffects.energyMedium;
				hasPower = true;
				itemCapacity = 70;
			}
		};

		autoFarm = new Cultivator("autoFarm") {
			{
				size = 3;
				health = 750;
				consumes.item(Items.sporePod, 2);
				consumes.power(2.0f);
				consumes.liquid(Liquids.water, 0.16f);
				outputItem = new ItemStack(AsItems.food, 3);
				craftTime = 90f;
				requirements(Category.crafting, ItemStack.with(Items.titanium, 150, Items.lead, 150, Items.copper, 200, Items.plastanium, 50));
				hasPower = true;
				itemCapacity = 50;
			}
		};
	}
}