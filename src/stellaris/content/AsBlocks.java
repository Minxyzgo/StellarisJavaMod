package stellaris.content;


import mindustry.content.Items;
import mindustry.ctype.ContentList;
import mindustry.ctype.ContentType;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import stellaris.type.blocks.crafting.EnergySmelter;
import static stellaris.Main.*;

public class AsBlocks implements ContentList {
    public static Block MatterEnergyTransformator;

            
    @Override
    public void load() {
        MatterEnergyTransformator = new EnergySmelter("matter-energy transformator") {{
            outputItem = new ItemStack(load.getItem("EnergyUnit"),5);
            requirements(Category.crafting, ItemStack.with());
            itemCapacity = 1000;
        }};
    }
}
