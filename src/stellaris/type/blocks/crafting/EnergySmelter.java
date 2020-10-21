package stellaris.type.blocks.crafting;

import arc.func.Boolf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;

import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Cicon;
import mindustry.ui.ItemImage;
import mindustry.ui.MultiReqImage;
import mindustry.ui.ReqImage;
import mindustry.world.blocks.production.GenericSmelter;
import mindustry.world.consumers.ConsumeItems;
import stellaris.type.AsPoint;

public class EnergySmelter extends GenericSmelter {
	public EnergySmelter(String name){
		super(name);
		
		Seq<ItemStack> ilist = new Seq<>();
		for (AsPoint.PointStack item : AsPoint.PointStack.values()) {
                ilist.add(new ItemStack(item.get(), 150 / item.getPoint()));
            }
            
            ConsumeItems cu = new ConsumeItems(ilist.toArray(ItemStack.class)) {
            	public final Boolf<Item> filter = item -> ilist.contains(i -> item.equals(i.item));
                public void build(Building tile, Table table) {
                    MultiReqImage image = new MultiReqImage();
                     Vars.content.items().each(i -> filter.get(i) && i.unlockedNow(),
                      item -> image.add(new ReqImage(new ItemImage(item.icon(Cicon.medium)), 
                      () -> tile != null && !(tile.items.empty()) && ((EnergyBuild)tile).getItem() == item)));
                    
                    table.add(image).size(8f * 4f);
                }
                
                
            };
            
            consumes.add(cu);
            buildType = () -> new EnergyBuild();
	}
	
	
	public class EnergyBuild extends GenericSmelter.SmelterBuild {
            private int itemId;
            
            @Override
             public boolean consValid() {
                 for (AsPoint.PointStack item : AsPoint.PointStack.values()) {
                     if(!items.has(item.get())) return true;
                 }
                 
                 return super.consValid();
             }
             
             
            @Override
            public void write(Writes write){
                super.write(write);
                write.i(itemId);
            }

            @Override
            public void read(Reads read, byte revision){
                 super.read(read, revision);
                 int id = read.i();
                 if(id != -1) itemId = id;
            }
             
             
             
             
             
             @Override
             public boolean acceptItem(Building source, Item item) {
                 if(super.acceptItem(source, item) && ((itemId == item.id) || items.empty())) {
                     itemId = item.id;
                     return true;
                 } else {
                     return false;
                 }
             }
             
             public Item getItem() {
             	return Vars.content.getByID(ContentType.item, itemId);
             }
        
    }
}
