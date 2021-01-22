package minxyzgo.mlib.type;

import minxyzgo.mlib.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.type.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.game.EventType.*;
import static mindustry.Vars.*;


public class Recipe extends UnlockableContent{
    private static int id = 0;
    public boolean isJson = false;
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
    
    public Recipe(String name, boolean  isJson) {
        super(name);
        this.isJson = isJson;
        if(isJson) Events.on(Tool.ToolLoadEvent.class, e -> {
            this.isJson = false;
            content.getBy(ContentType.error).remove(this);
            content.handleContent(this);
            content.handleMappableContent(this);
        });
    }
    
    @Override
    public void init(){
        consumes.init();
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
        
        if(consumes.has(ConsumeType.power)) {
            ConsumePower conPower= consumes.getPower();
            stats.add(Stat.powerUse, conPower.usage);
        }
        
        for (ItemStack i : itemOutput) {
            stats.add(Stat.output, i);
        }
        
        if(liquidOutput != null){
            stats.add(Stat.output, liquidOutput.liquid, liquidOutput.amount, false);
        }
        stats.add(Stat.input,"@time-usage "+(time / Time.toSeconds) + "s");
    }
    
    public static int nextId() {
        return id++;
    }

	@Override
	public ContentType getContentType() {
		return isJson ? ContentType.error : Tool.crecipe;
	}
}
