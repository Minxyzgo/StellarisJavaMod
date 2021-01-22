package minxyzgo.mlib.type;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.type.Skills.DataFireEvent;
import static minxyzgo.mlib.Tool.*;

public abstract class DataSkill  extends ImageButton {
    public final int id;
    
    public abstract String getType();
    
    public DataSkill() {
        id = skills.pocSeq.size;
        skills.pocSeq.add(this);
    }
    
    public abstract EntSkill getEnt();
    
    public void update() {
        getEnt().update();
    }
    
    public abstract void callSkill(Player player, Object... objects);
    
    public void sendSkill(Object... objects) {
        Events.fire(new DataFireEvent(this, objects));
    };
    
    public void reset() {}
    
    public void build(Table parent) {}
}