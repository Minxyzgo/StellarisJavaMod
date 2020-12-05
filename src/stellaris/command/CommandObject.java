package stellaris.command;

import java.lang.reflect.Field;

import arc.audio.Music;
import arc.files.Fi;
import arc.func.Func;
import arc.struct.ObjectMap;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import mindustry.gen.Musics;

public class CommandObject {
	public static final ObjectMap<String, CObject<?>> map = new ObjectMap<>();

	static {
		map.put("Music", new CObject<Music>(Music.class).with(s -> {
			try {
				Field field = Musics.class.getField(s);
				return (Music)field.get(Musics.class);
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("Did not find music like " + s);
			} catch (IllegalAccessException e) {
				Log.err(e);
				throw new ArcRuntimeException("Runtime err");
			}
		}));
	}

	public static class CObject<T> {
		public Class<T> clzss;
		public Func<String, T> func;
		
		public CObject (Class<T> clzss) {
		    this.clzss = clzss;
		}
		
		public CObject<T> with(Func<String,T> func) {
			this.func = func;
			return this;
		}
		
		public boolean needFunc() {
		    return func != null;
		}
		
		//after
		public T get(Fi file) {
			return null;
		}
	}
}