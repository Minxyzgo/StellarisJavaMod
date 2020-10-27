package stellaris.type.blocks.crafting;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.math.Mathf;
import arc.util.Time;
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
import static stellaris.Main.*;

public class EnergySmelter extends GenericSmelter {
    private Seq<ItemStack> ilist = new Seq<>();
	public EnergySmelter(String name){
		super(name);
		
		
		for (AsPoint.PointStack item : AsPoint.PointStack.values()) {
                ilist.add(new ItemStack(item.get(), POINT / item.getPoint()));
            }
            
            ConsumeItems cu = new ConsumeItems(ilist.toArray(ItemStack.class)) {
                public void build(Building tile, Table table) {
                    MultiReqImage image = new MultiReqImage();
                    ilist.each(i -> i.item.unlockedNow(), stack -> {
                        image.add(new ReqImage(new ItemImage(stack.item.icon(Cicon.medium), stack.amount), 
                        () -> tile != null && !(tile.items.empty()) && ((EnergyBuild)tile).getItem() == stack.item));
                    
                        table.add(image).size(8f * 4f);
                    });
                }
            };
            consumes.add(cu);
            buildType = () -> new EnergyBuild();
	}
	
	
	public class EnergyBuild extends GenericSmelter.SmelterBuild {
            private int itemId = -1;
            
            @Override
             public boolean consValid() {
                 for (AsPoint.PointStack item : AsPoint.PointStack.values()) {
                 	
                     if(items.has(item.get(), POINT / item.getPoint())) return true;
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
             public void draw(){
                 Draw.rect(block.region, x, y, block.rotate ? rotdeg() : 0.0f);
                 drawTeamTop();
                 if(warmup > 0f && flameColor.a > 0.001f){
                    float g = 0.3f;
                    float r = 0.06f;
                    float cr = Mathf.random(0.1f);
  
                    Draw.alpha(((1f - g) + Mathf.absin(Time.time(), 8f, g) + Mathf.random(r) - r) * warmup);

                    Draw.tint(getItem().color);
                    Fill.circle(x, y, 3f + Mathf.absin(Time.time(), 5f, 2f) + cr);
                    Draw.color(1f, 1f, 1f, warmup);
                    Draw.rect(topRegion, x, y);
                    Fill.circle(x, y, 1.9f + Mathf.absin(Time.time(), 5f, 1f) + cr);

                    Draw.color();
                }
             }
             
             
             
             @Override
             public boolean acceptItem(Building source, Item item) {
                 if(super.acceptItem(source, item) && ((itemId == item.id) || items.empty()) || ((itemId != -1) && !items.has(getItem()))) {
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