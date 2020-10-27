package stellaris;

import mindustry.mod.*;
import stellaris.content.*;
//import stellaris.content.JsonLoad;

public class Main extends Mod{
//	public static JsonLoad load;
	public static int POINT = 150;
    public Main(){
        
    }

    @Override
    public void loadContent(){
		new AsItems().load();
		new AsBlocks().load();
    }

}