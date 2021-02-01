package minxyzgo.mlib.type;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.type.Skills.DataFireEvent;
import static minxyzgo.mlib.Tool.*;

public abstract class DataSkill  extends ImageButton {
    public final int id;
    public float cooldown = 21.5f;
    
    public abstract String getType();
    
    {
        id = skills.pocSeq.size;
        skills.pocSeq.add(this);
    }
    
    public DataSkill(TextureRegion region) {
        super(region);
    }
    
    public DataSkill(TextureRegion region, ImageButton.ImageButtonStyle style) {
        super(region, style);
    }
    
    public abstract EntSkill getEnt();
    
    public void update() {
        getEnt().update();
    }
    
    public void drawEnt() {
        
    }
    
    public abstract void callSkill(Player player, Object... objects);
    
    public void sendSkill(Object... objects) {
        Events.fire(new DataFireEvent(this, objects));
    };
    
    public void reset() {
        getEnt().reload = 0;
    }
    
    public void build(Table parent) {}
}