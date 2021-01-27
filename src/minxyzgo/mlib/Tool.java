package minxyzgo.mlib;

import java.lang.reflect.*;
import java.util.*;


import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import mindustry.ctype.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.*;
import minxyzgo.mlib.content.*;
import minxyzgo.mlib.io.*;
import minxyzgo.mlib.type.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class Tool extends Mod {
    private static int nextClassId;
	public static JsonLoad jsonLoad = new JsonLoad();
	public static Skills skills = new Skills();
	public final static boolean loadExample = true, showTerminal = true;
	public static BuildContentParser parser;
	public static ContentType crecipe;
	public static float JAVA_VERSION;
	//private static ObjectSet<Cons<MappableContent>> initialization = new ObjectSet<>();
	//public static ObjectSet<MappableContent> customContentMap = new ObjectSet<>();

	private static final float JAVA_E = 1.8f, JAVA_S = 1.7f;
	//private static final int baseTypeLength = 15;

	static {
	    JAVA_VERSION = Float.valueOf(System.getProperty("java.specification.version"));
	    /*
		onLoad(() -> {
			crecipe = andType(ContentType.class, "Recipe");
			entAddContent();
		});
		*/
	}

	@Override
	public void loadContent() {
	    nextClassId = EntityMapping.idMap.length;
		Events.fire(new ToolLoadEvent());
		onLoad(jsonLoad::init);
		if (showTerminal) showTerminal();
		parser = new BuildContentParser();
		try {
			Field parserField = Mods.class.getDeclaredField("parser");
			parserField.setAccessible(true);
			parserField.set(mods, parser);

			parser.contentParsers.addAll("minxyzgo.mlib.type",
										 "minxyzgo.mlib.type.block");
			parserType();

			Log.info("TOOL");
		} catch (Exception e) {
			Log.err("Error for @", e);
		}

		if (loadExample) new Examples().load();
	}
	
	public static synchronized int nextClassId(Prov<? extends Unit> type) {
	    return nextClassId(type, "");
	}
	
	public static synchronized int nextClassId(Prov<? extends Unit> type, String name) {
	    EntityMapping.idMap[nextClassId] = type;
	    if(!name.equals("")) EntityMapping.nameMap.put(name, type);
	    return nextClassId++;
	}

	public static void parserType() {
		parser.classParsers.put(Recipe.class, (type, data) -> {
			Recipe recipe = new Recipe("Recipe" + "$" + Recipe.nextId(), true);
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

	public static void onCreate(Runnable run) {
		Events.on(ToolLoadEvent.class, e -> run.run());
	}

	public static void unlockBlackList() {

		try {
			Class<?> clazz = Class.forName("rhino.Context");
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

	public static synchronized <T extends Enum<T>> T andType(Class<T> clazz, String name) {
		return andType(clazz, name, new Class[] {}, new Object[] {});
	}

	public static synchronized <T extends Enum<T>> T andType(Class<T> clazz, String name, Class<?>[] pamClazzes, Object[] pamObjects) {

		try {
			Constructor<T> c0 = clazz.getDeclaredConstructor(new Seq<Class<?>>(new Class<?>[] {String.class, int.class}).and(pamClazzes).toArray(Class.class));
			c0.setAccessible(true);

			T enumValue;

			Field valuesField = clazz.getDeclaredField("$VALUES");
			valuesField.setAccessible(true);


			T[] values = (T[])valuesField.get(clazz);

			int length = values.length;
			
			T[] newValues = Arrays.copyOf(values, length + 1);


			if (android || JAVA_VERSION == JAVA_S) {
				try {
					return enumValue = Enum.valueOf(clazz, name);
				} catch (IllegalArgumentException e) {
					if (pamObjects.length > 0) enumValue = (T)c0.newInstance(name, length, pamObjects);
					enumValue = (T)c0.newInstance(name, length);
					Log.info("Loading type: " + name);
				}

				newValues[length] = enumValue;
				valuesField.set(clazz, newValues);
				try {
				    Field allField = clazz.getDeclaredField("all");
			        allField.setAccessible(true);
				    allField.set(clazz, newValues);
				    Log.info("after all Array length: " + ((T[])allField.get(clazz)).length);
				} catch(NoSuchFieldException e_t) {
				    Log.info(clazz.getSimpleName() + " without all");
				}

				

				Log.info("finish " + enumValue);
				content.clear();

			} else if (JAVA_VERSION == JAVA_E) {
				//no Test
				Method met = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
				met.setAccessible(true);

				Object obj = met.invoke(c0);
				Method met2 = obj.getClass().getDeclaredMethod("newInstance", Object[].class);

				newValues[length] = enumValue = (T)met2.invoke(obj, new Seq<Object>(new Object[] {name, Integer.valueOf(length)}).and(pamObjects).toArray());


				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);

				int modifiers = modifiersField.getInt(valuesField);
				modifiers &= ~Modifier.FINAL;
				try {
				    Field allField = clazz.getDeclaredField("all");
			        allField.setAccessible(true);
			        modifiersField.setInt(allField, modifiers);
			        allField.set(clazz, newValues);
				} catch(NoSuchFieldException e_t) {
				    Log.info(clazz.getSimpleName() + " without all");
				}

				modifiersField.setInt(valuesField, modifiers);

				valuesField.set(clazz, newValues);
				content.clear();

			} else {
				throw new IllegalAccessError("andType method does not support Java " + JAVA_VERSION);
			}

			return enumValue;

		} catch (Exception e) {
			Log.err(e);
			return null;
		}
	}

	public static synchronized void entAddContent() {
		try {
		    //content.each(cont -> Log.info(cont.toString()));
			Field nameMapField = ContentLoader.class.getDeclaredField("contentNameMap");
			nameMapField.setAccessible(true);
			Field idMapField = ContentLoader.class.getDeclaredField("contentMap");
			idMapField.setAccessible(true);

			ObjectMap<String, MappableContent>[] nameMap = Arrays.copyOf((ObjectMap<String, MappableContent>[])nameMapField.get(content), ContentType.all.length);
			Seq<Content>[] contentMap = Arrays.copyOf((Seq<Content>[])idMapField.get(content), ContentType.all.length);

			for (ContentType type : ContentType.all) {
				int ordinal = type.ordinal();
				if (nameMap[ordinal] == null) nameMap[ordinal] = new ObjectMap<>();
				if (contentMap[ordinal] == null) contentMap[ordinal] = new Seq<>();
			}

			nameMapField.set(content, nameMap);
			idMapField.set(content, contentMap);
			
			Events.fire(new ToolTypeLoadEvent());
			/*
			Log.info("rerun");
			for(ContentType type : ContentType.all){
			    for(MappableContent content : nameMap[type.ordinal()].values()) {
			        Log.info(content.toString());
			    }
			}
*/
/*
            for(MappableContent c : customContentMap) {
                content.handleMappableContent(c);
            }
            
			Core.assets.loadRun("contentinit2", Tool.class, () -> initialize(Content::init, customContentMap), () -> initialize(Content::load, customContentMap));
			*/
			
		} catch (Exception e) {
			Log.err(e);
		}
	}
/*
	private static void initialize(Cons<MappableContent> callable, ObjectSet<MappableContent> contentMap) {
		if (initialization.contains(callable)) return;

		for (ContentType type : ContentType.all) {
			if (type.ordinal() <= baseTypeLength) continue;
			for (MappableContent content : contentMap) {
				try {
					callable.get(content);
				} catch (Throwable e) {
					if (content.minfo.mod != null) {
						Log.err(e);
						mods.handleContentError(content, e);
					} else {
						throw new RuntimeException(e);
					}
				}
			}
		}

		initialization.add(callable);
	}
	
	public static void handleMappableContent(MappableContent content) {
	    if (customContentMap.contains(content)) return;
	    customContentMap.add(content);
	}
*/
	public static class ToolLoadEvent {}
	public static class ToolTypeLoadEvent {}
}