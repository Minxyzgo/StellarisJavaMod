package stellaris;

import arc.util.*;
import mindustry.mod.*;
import minxyzgo.mlib.*;
import stellaris.archeology.*;
import stellaris.content.*;
import stellaris.core.*;

public class Main extends Mod{
    public static final String modName = "stellaris";
	public static int POINT = 150;
	private static String ccxxxzzs = "I Saw Your Ship";
	public static Ui asUi;
	public static boolean test = false;
	public static Tool tool = new Tool();
	public static Archeology archeology;

    public static String transform(String name) {
        return modName + "-" + name;
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
			 "stellaris.type.draw",
                "stellaris.archeology"
        );
		/*Tool.parser.classParsers.put(.class, (type, data) -> {
			Recipe recipe = new Recipe("Recipe" + "$" + Recipe.nextId(), true);
			if (data.has("consumes") && data.get("consumes").isObject()) {
				parser.readConsumes(recipe.consumes, recipe.name, "consumes", data);
				data.remove("consumes");
			}

			parser.readFields(recipe, data);
			return recipe;
		});*/
		
		archeology = new Archeology();
    }
    
    @Override
    public void init(){
        asUi = new Ui();
        tool.init();
        Log.info("STE @", "loaded");
    }

}