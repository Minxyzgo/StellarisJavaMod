package stellaris;

import mindustry.mod.*;
import stellaris.content.AsBlocks;
import stellaris.content.JsonLoad;

public class Main extends Mod{
	public static JsonLoad load;
	
    public Main(){
        
    }

    @Override
    public void loadContent(){
		load.init();
		new AsBlocks().load();
    }

}