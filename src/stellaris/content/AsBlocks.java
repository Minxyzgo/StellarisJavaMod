package stellaris.content;

import mindustry.content.Liquids;
import mindustry.content.Items;
import mindustry.ctype.ContentList;
import mindustry.ctype.ContentType;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawAnimation;
import mindustry.world.draw.DrawWeave;
import stellaris.type.blocks.crafting.EnergySmelter;


public class AsBlocks implements ContentList {
	
    public static GenericCrafter MatterEnergyTransformator, energyPackager, neutronMetalForge, civilianFactory, autoFarm;

            
    @Override
    public void load() {
        MatterEnergyTransformator = new EnergySmelter("matter-energy transformator") {{
        	health = 800;
        	size = 3;
            outputItem = new ItemStack(AsItems.energyUnit, 5);
            craftTime = 60f;
            requirements(Category.crafting, ItemStack.with(Items.silicon,150,Items.titanium,200,Items.plastanium,120,Items.phaseFabric,50));
            itemCapacity = 500;
        }};
        
        energyPackager = new GenericCrafter("energy-packager"){{
        	size = 4;
        	health = 800;
        	drawer = new DrawAnimation();
        	craftTime = 60f;
        	requirements(Category.crafting, ItemStack.with(Items.silicon,250,Items.lead,300,Items.metaglass,200,Items.surgeAlloy,50));
        	consumes.power(800f);
        	consumes.item(Items.metaglass,1);
        	outputItem = new ItemStack(AsItems.mineral, 1);
        	hasPower = true;
        	itemCapacity = 20;
        }};
        
        neutronMetalForge = new GenericCrafter("neutron-metal-Forge"){{
        	size = 4;
        	health = 800;
        	consumes.items(ItemStack.with(Items.surgeAlloy, 1, AsItems.mineral, 10));
        	craftTime = 90f;
        	consumes.power(7.5f);
        	outputItem = new ItemStack(AsItems.neutronMaterial, 1);
        	requirements(Category.crafting, ItemStack.with(Items.titanium,250,Items.phaseFabric,100,Items.thorium,400,Items.surgeAlloy,150,Items.lead,300));
        	hasPower = true;
        	itemCapacity = 20;
        }};
        
        
        civilianFactory = new GenericCrafter("civilian-factory"){{
            size = 3;
            health = 750;
            consumes.items(ItemStack.with(Items.silicon, 3, AsItems.mineral, 5, Items.copper, 4));
            consumes.power(5.0f);
            outputItem = new ItemStack(AsItems.industryProduction, 1);
            requirements(Category.crafting, ItemStack.with(Items.titanium,150,Items.lead,150,Items.copper,200,Items.plastanium,50));
            craftEffect = AsEffects.energyMedium;
            hasPower = true;
            itemCapacity = 70;
        }};
        
        autoFarm = new GenericCrafter("autoFarm"){{
            size = 3;
            health = 750;
            consumes.item(Items.sporePod, 2);
            consumes.power(2.0f);
            consumes.liquid(Liquids.water, 0.16f);
            outputItem = new ItemStack(AsItems.food, 3);
            craftTime = 90f;
            requirements(Category.crafting, ItemStack.with(Items.titanium,150,Items.lead,150,Items.copper,200,Items.plastanium,50));
            hasPower = true;
            drawer = new DrawWeave();
            itemCapacity = 50;
        }};
    }
}