package stellaris.core;

import java.lang.reflect.Field;

import arc.Events;

import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.*;
public class Ui {
    public Ui() {
        Events.on(EventType.WorldLoadEvent.class, a -> {
            menu();
        });
    }
    
    
	public void menu(){
	    try {
            Field A = MenuFragment.class.getDeclaredField("renderer");
            A.setAccessible(true);
            Field B = MenuRenderer.class .getDeclaredField("flyerType");
            B.setAccessible(true);
            B.set(A.get(ui.menufrag), UnitTypes.scepter);
            
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new Error(ex);
        }
	}
}