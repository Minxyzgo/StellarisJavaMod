package minxyzgo.mlib;

import java.lang.reflect.*;

import arc.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.mod.*;
import minxyzgo.mlib.content.*;
import minxyzgo.mlib.io.*;
import minxyzgo.mlib.type.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Tool extends Mod {
	public static JsonLoad jsonLoad = new JsonLoad();
	public final static boolean loadExample = true, showTerminal = true;
	public static BuildContentParser parser;


	@Override
	public void init() {
		jsonLoad.init();
		if(showTerminal) showTerminal();
		parser = new BuildContentParser();
		try {
			Field parserField = Mods.class.getDeclaredField("parser");
			parserField.setAccessible(true);
			parserField.set(mods, parser);

			parser.contentParsers.addAll("minxyzgo.mlib.type",
										 "minxyzgo.mlib.type.block");
			parserType();
			Log.info("TOOL", "parser finish");
		} catch (Exception e) {
			Log.err("Error for @", e);
		}
	}

	public static void parserType() {
		parser.classParsers.put(Recipe.class, (type, data) -> {
			Recipe recipe = new Recipe("Recipe" + "$" + Recipe.nextId());
			if (data.has("consumes") && data.get("consumes").isObject()) {
				parser.readConsumes(recipe.consumes, recipe.name, "consumes", data);
				data.remove("consumes");
			}

			parser.readFields(recipe, data);
			return recipe;
		});
	}

	public static void onLoad(Runnable run) {
		Events.on(EventType.ClientLoadEvent.class, e -> run.run());
	}

	public static void unlockBlackList() {

		Class<?> clazz = Class.forName("rhino.Context");
		try {
			Method cmod = Platform.class.getDeclaredMethod("getScriptContext");
			Object context = cmod.invoke(null);
			Field shutter = clazz.getDeclaredField("classShutter");
			shutter.setAccessible(true);
			shutter.set(context, null);
			Field hasshutter = clazz.getDeclaredField("hasClassShutter");
			hasshutter.setAccessible(true);
			hasshutter.set(context, false);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static void showTerminal() {
	    enableConsole = true;
		onLoad(() -> {
			loadLogger();
		});
	}

	@Override
	public void loadContent() {
		if (loadExample) new Examples().load();
	}
}