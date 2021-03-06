package minxyzgo.mlib.type;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import minxyzgo.mlib.entities.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

 /**
 * Skills players can operate
 * You can change the skill with
 * {@link Events#<?>fire(Class<T>, Cons<T>)}
 * @author Minxyzgo
 */
 
@SuppressWarnings("unchecked")
public class Skills implements ApplicationListener {
    public static final String TYPE_SKILL = "TYPE_SKILL";
    public float dataCooldown;
    public final Seq<DataSkill> pocSeq = new Seq<>(255);
    public Table baseGroup;
    
    private DataSkill[] dataSkills;
    
    {
        
        Events.on(ServerLoadEvent.class, e -> initNet());
        Events.on(ClientLoadEvent.class, e -> {
            initNet();
            //show ui
            ui.hudGroup.fill(full -> {
                baseGroup = full;
                full.center().left().marginBottom(40f).visibility = () -> state.isGame() && ui.hudfrag.shown;
            });
            
        });
        Events.on(ResetEvent.class, e -> {
            reset();
        });
        Events.on(DataFireEvent.class, this::writePoc);
        Events.on(UnitChangeEvent.class, e -> {
            baseGroup.clearChildren();
            dataSkills = null;
            
            if(e.player == player && e.player.unit().type instanceof Skillc) {
                Skillc skillcs = (Skillc)e.player.unit().type;
                
                dataSkills = skillcs.getSkill();
                for(DataSkill data : skillcs.getSkill()) {
                    data.build(baseGroup);
                }
                
                baseGroup.invalidate();
            }
        });
        Core.app.addListener(this);
    }
    
    @Nullable
    public DataSkill getType(String type) {
        for(DataSkill skill : dataSkills) {
            if(skill.getType().equals(type)) return skill;
        }
        
        return null;
    }
    
    @Override
    public void update() {
        if(dataSkills != null && dataSkills.length > 0) {
            for(DataSkill data : dataSkills) {
                data.update();
                
                Draw.reset();
                Draw.proj(Core.camera);
                Draw.sort(true);
                Draw.draw(Layer.effect, data::drawEnt);
                Draw.sort(false);
                Draw.reset();
                Draw.flush();
            }
        }
    }
    
    private void initNet() {
        netClient.addPacketHandler(TYPE_SKILL, this::readPoc);
        netServer.addPacketHandler(TYPE_SKILL, (player, msg) -> {
            readPoc(msg);
        });
    }
    
    private void readPoc(String base) {
        String[] str = base.split(",");
        DataSkill skill = pocSeq.get(Integer.valueOf(str[0]));
        Player entPlayer = Groups.player.getByID(Integer.valueOf(str[1]));
        String[] copystr = new String[str.length - 2];
        System.arraycopy(str, 2, copystr, 0, str.length - 2);
        //The configuration will be output again as an array
        if(net.server()) Call.clientPacketReliable(TYPE_SKILL, base);
        dataCooldown = skill.cooldown;
        pocSeq.each(DataSkill::reset);
        skill.callSkill(entPlayer, (Object[])copystr);
    }
    
    private void writePoc(DataFireEvent event) {
        event.skill.callSkill(player, event.config);
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(String.valueOf(event.skill.id));
        joiner.add(String.valueOf(player.id));
        for(Object obj : event.config) {
            joiner.add(String.valueOf(obj));
        }
        String pack = joiner.toString();
        Call.clientPacketReliable(TYPE_SKILL, pack);
        Call.serverPacketReliable(TYPE_SKILL, pack);
    }
    
    public void reset() {
        dataSkills = null;
    }
    
    
    //Called when the player’s skill fired
    public static class DataFireEvent {
        public DataSkill skill;
        public Object[] config;
        
        public DataFireEvent(DataSkill skill, Object[] config) {
            this.skill = skill;
            this.config = config;
        }
    }
}