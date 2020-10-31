package stellaris.core;

import java.lang.reflect.Field;

import arc.Events;

import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.fragments.HudFragment;
import mindustry.ui.fragments.MenuFragment;
//import stellaris.ui.frg.PlaceFrg;
import stellaris.ui.frg.*;

import static mindustry.Vars.*;
public class Ui {
    public Ui() {
        Events.on(EventType.ClientLoadEvent.class, a -> {
            menu();
            //place();
        });
    }
    
    
	private void menu(){
	    ui.menufrag = new MenuFrg();
	}
	
	/*private void place(){
	    try{
	        Field nameField = HudFragment.class.getDeclaredField("blockfrag");
	        
	        nameField.setAccessible(true);
	        nameField.set(ui.hudfrag, new PlaceFrg());
	        if(!(ui.hudfrag.blockfrag instanceof PlaceFrg)) throw new Error("it isn't PlaceFrg");
	    }catch(NoSuchFieldException | IllegalAccessException ex) {
            throw new Error(ex);
        }
	}*/
}