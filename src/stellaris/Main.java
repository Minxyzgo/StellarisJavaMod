package stellaris;

import arc.util.*;
import mindustry.mod.*;
import stellaris.content.*;
//import stellaris.content.JsonLoad;
import stellaris.core.Ui;
import minxyzgo.mlib.*;

public class Main extends Mod{
//	public static JsonLoad load;

	public static int POINT = 150;
	private static String ccxxxzzs = "I Saw Your Ship";
	public static Ui asUi;
	public static boolean test = false;
	public static Tool tool = new Tool();
    public Main(){

    }
    

    @Override
    public void loadContent(){
		new AsItems().load();
		new AsBullets().load();
		new AsBlocks().load();
		new AsUnits().load();
    }
    
    @Override
    public void init(){
        asUi = new Ui();
        tool.init();
        Log.info("STE", "loaded");
    }

}