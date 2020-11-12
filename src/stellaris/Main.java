package stellaris;

import mindustry.mod.*;
import stellaris.content.*;
//import stellaris.content.JsonLoad;
import stellaris.core.Ui;

public class Main extends Mod{
//	public static JsonLoad load;

	public static int POINT = 150;
	private static String ccxxxzzs = "I Saw Your Ship";
	public static Ui asUi;
    public Main(){

    }
    

    @Override
    public void loadContent(){
		new AsItems().load();
		new AsBlocks().load();
		new AsUnits().load();
    }
    
    @Override
    public void init(){
        asUi = new Ui();
    }

}