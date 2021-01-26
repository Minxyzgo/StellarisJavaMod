package stellaris;

import arc.util.*;
import mindustry.mod.*;
import mindustry.type.*;
import stellaris.content.*;
//import stellaris.content.JsonLoad;
import stellaris.core.Ui;
import stellaris.type.units.PowerUnit;
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
		tool.loadContent();
		Tool.parser.contentParsers.addAll("stellaris.type.units",
			 "stellaris.type.abilities",
			 "stellaris.type.draw");
		/*Tool.parser.classParsers.put(.class, (type, data) -> {
			Recipe recipe = new Recipe("Recipe" + "$" + Recipe.nextId(), true);
			if (data.has("consumes") && data.get("consumes").isObject()) {
				parser.readConsumes(recipe.consumes, recipe.name, "consumes", data);
				data.remove("consumes");
			}

			parser.readFields(recipe, data);
			return recipe;
		});*/
    }
    
    @Override
    public void init(){
        asUi = new Ui();
        tool.init();
        Log.info("STE @", "loaded");
    }

}