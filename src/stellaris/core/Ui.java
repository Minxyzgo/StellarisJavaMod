package stellaris.core;

import java.lang.reflect.Field;

import arc.Events;

import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.fragments.HudFragment;
import mindustry.ui.fragments.MenuFragment;
import stellaris.ui.frg.PlaceFrg;

import static mindustry.Vars.*;
public class Ui {
    public Ui() {
        Events.on(EventType.WorldLoadEvent.class, a -> {
            menu();
            place();
        });
    }
    
    
	private void menu(){
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
	
	private void place(){
	    try{
	        Field nameField = HudFragment.class.getDeclaredField("blockfrag");
	        
	        nameField.setAccessible(true);
	        nameField.set(ui.hudfrag, new PlaceFrg());
	        if(!(ui.hudfrag.blockfrag instanceof PlaceFrg)) throw new Error("it isn't PlaceFrg");
	    }catch(NoSuchFieldException | IllegalAccessException ex) {
            throw new Error(ex);
        }
	}
}