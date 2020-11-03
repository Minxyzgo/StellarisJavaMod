package stellaris.core;

import java.lang.reflect.Field;


import arc.*;
import arc.scene.Element;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.ui.MobileButton;
import mindustry.ui.fragments.MenuFragment;
import stellaris.ui.draw.MenuRed;
import stellaris.ui.frg.MenuFrg;

import static mindustry.Vars.*;

public class Ui{
    public MobileButton ste;
    
    public Ui() {
        ste = new MobileButton(Icon.menu, "stellaris", () -> ui.showInfo("by Minxyzgo"));
        Events.on(ClientLoadEvent.class, a -> {
            menu();
            //place();
        });
    }
    
    
	private void menu(){
	    try{
	        Field A = MenuFragment.class.getDeclaredField("renderer");
	        A.setAccessible(true);
	        A.set(ui.menufrag, new MenuRed());
	        
	        
	    }catch(NoSuchFieldException | IllegalAccessException ex) {
	        ui.showException(ex);
        }
        Table t = ui.menuGroup.<Table>find("buttons");
        if(t == null) {
            Seq<String> s = new Seq<>();
            ui.menuGroup.forEach(e -> s.add(e.name));
            
            
            ui.showInfo(Strings.join(",", s.toArray(String.class)));
            
            
            //ui.showException(new NullPointerException("t is null"));
            return;
        }
        t.table(table -> {
            table.defaults().set(t.defaults());
            table.row();
            table.add(ste);
            //ui.showInfo("over.");
            table.visible = true;
        }).colspan(2);
       // t.reset();
        
        /*ui.menuGroup.fill(t -> {
            t.clear();
            t.setSize(Core.graphics.getWidth(), Core.graphics.getHeight());
            float size = 120f;
            t.defaults().size(size).pad(5).padTop(4f);
            t.marginTop(60f);
            rows(t, 15);
            t.add(ste);
            t.visible = true;
        });*/
	}
	
	
	public static void rows(Table t, int times){
	    for(int x = 0;x < times;x++){
	        t.row();
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