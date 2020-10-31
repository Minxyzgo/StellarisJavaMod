package stellaris.core;

import java.lang.reflect.Field;


import arc.*;
import arc.scene.ui.layout.Table;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.ui.MobileButton;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.*;

public class Ui{
    public MobileButton nmsl;
    
    public Ui() {
        Events.on(ClientLoadEvent.class, a -> {
            menu();
            //place();
        });
    }
    
    
	private void menu(){
	    try{
	        Field A = MenuFragment.class.getDeclaredField("container");
	        A.setAccessible(true);
	        Table t = (Table)A.get(ui.menufrag);
	        nmsl = new MobileButton(Icon.menu, "nmsl", () -> Core.app.exit());
	        t.table(table -> {
                table.defaults().set(t.defaults());

                table.add(nmsl);
            }).colspan(4);
	    }catch(NoSuchFieldException | IllegalAccessException ex) {
            throw new Error(ex);
        }
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