package minxyzgo.mlib.type;

import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class Recipe extends UnlockableContent{
    private static int id = 0;
    public float time = 60;
    public Consumers consumes = new Consumers();
    public Seq<ItemStack> itemOutput = new Seq<>();
    public LiquidStack liquidOutput;
    public float updateEffectChance = 0.04f;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect =  Fx.none;
    
    public Recipe(String name) {
        super(name);
    }
    
    public TextureRegion icon(Cicon icon){
        if(cicons[icon.ordinal()] == null){
            cicons[icon.ordinal()] =
                Core.atlas.find(getContentType().name() + "-" + name + "-" + icon.name(),
                Core.atlas.find(getContentType().name() + "-" + name + "-full",
                Core.atlas.find(name + "-" + icon.name(),
                Core.atlas.find(name + "-full",
                Core.atlas.find(name,
                Core.atlas.find(getContentType().name() + "-" + name,
                Core.atlas.find(name + "1")))))));
        }
        return cicons[icon.ordinal()];
    }
    
    @Override
    public void setStats() {
        
        if(consumes.has(ConsumeType.item)) for(ItemStack stack : consumes.getItem().items){
            stats.add(Stat.input, stack);
        }
            
        if(consumes.has(ConsumeType.liquid)) {
            ConsumeLiquid con = consumes.<ConsumeLiquid>get(ConsumeType.liquid);
            stats.add(Stat.input, con.liquid, con.amount,  false);
        }
        
        for (ItemStack i : itemOutput) {
            stats.add(Stat.output, i);
        }
        
        if(liquidOutput != null){
            stats.add(Stat.output, liquidOutput.liquid, liquidOutput.amount, false);
        }
        stats.add(Stat.input,"@time-usage "+time);
    }
    
    public static int nextId() {
        return id++;
    }

	@Override
	public ContentType getContentType() {
		return ContentType.error;
	}
}
