package minxyzgo.mlib.type;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

import java.util.StringJoiner;

import arc.*;
import arc.input.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import minxyzgo.mlib.entities.*;
 /**
 * Skills players can operate
 * You can change the skill with
 * {@link Events#<T>fire(Class<T>, Cons<T>)}
 * @author Minxyzgo
 */
public class Skills {
    public static final String TYPE_SKILL = "TYPE_SKILL";
    public static final Seq<DataSkill> pocSeq = new Seq<>(255);
    public static DataSkill NullsSkill = new DataSkill((t, n) -> {});
    public static DataSkill dataSkill = NullsSkill;
    public static Table baseGroup;
    private static InputProcessor processor = new InputProcessor() {
        @Override
        public boolean keyDown(KeyCode keycode){
            //when the user presses the keyboard
            return dataSkill.listener.get(keycode);
            //You can monitor its skills
        }
    };
    
    static {
        
        Events.on(ServerLoadEvent.class, e -> init());
        Events.on(ClientLoadEvent.class, e -> {
            init();
            //show ui
            ui.hudGroup.fill(full -> {
                baseGroup = full;
                full.center().left().marginBottom(40f).visibility = () -> state.isGame() && ui.hudfrag.shown && dataSkill != NullsSkill;
            });
            Core.input.addProcessor(processor);
        });
        Events.on(ResetEvent.class, e -> {
            dataSkill = NullsSkill;
        });
        Events.on(DataFireEvent.class, Skills::writePoc);
        Events.on(DataChangeEvent.class, e -> {
            setData(e.skill, e.ent);
        });
        Events.on(UnitChangeEvent.class, e -> {
            if(e.player == player && e.player.unit() instanceof Skillc)
                setData(pocSeq.get(((Skillc)e.player.unit()).skillId()), (Skillc)e.player.unit());
        });
    }
    
    public static void setData(DataSkill data, Skillc ent) {
        //Called when a DataChangeEvent fired
        dataSkill = data;
        //Make the table update
        baseGroup.clear();
        baseGroup.invalidate();
        ent.build(baseGroup);
    }
    
    private static void init() {
        netClient.addPacketHandler(TYPE_SKILL, Skills::readPoc);
        netServer.addPacketHandler(TYPE_SKILL, (player, msg) -> {
            readPoc(msg);
        });
    }
    
    
    private static void readPoc(String base) {
        String[] str = base.split(",");
        DataSkill skill = pocSeq.get(Integer.valueOf(str[0]));
        Entityc ent = Groups.all.getByID(Integer.valueOf(str[1]));
        String[] copystr = new String[str.length - 2];
        System.arraycopy(str, 1, copystr, 0, str.length - 2);
        //The configuration will be output again as an array
        if(net.server()) Call.clientPacketReliable(TYPE_SKILL, base);
        skill.cons.get(ent, copystr);
    }
    
    private static void writePoc(DataFireEvent event) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(String.valueOf(event.skill.id));
        joiner.add(String.valueOf(event.dataId));
        joiner.add(event.config);
        String pack = joiner.toString();
        Call.clientPacketReliable(TYPE_SKILL, pack);
        Call.serverPacketReliable(TYPE_SKILL, pack);
    }
    
    //Called when the player’s skills were changed
    public static class DataChangeEvent {
        public DataSkill skill;
        public Skillc ent;
        
        public DataChangeEvent(DataSkill skill, Skillc ent) {
            this.skill = skill;
            this.ent = ent;
        }
    }
    
    //Called when the player’s skill fired
    public static class DataFireEvent {
        public DataSkill skill;
        public String config;
        public int dataId;
        
        public DataFireEvent(DataSkill skill, int dataId) {
            this.skill = skill;
            this.dataId = dataId;
        }
    }
}