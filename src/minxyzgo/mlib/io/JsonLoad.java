package minxyzgo.mlib.io;

import arc.*;
import arc.struct.*;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ctype.*;
import mindustry.ctype.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

 /**
 * A loader allows you to easily read the content of Json
 * @author Minxyzgo
 */

public class JsonLoad {
    
	private boolean empty = true;
	private ObjectMap<String, MappableContent>[] map;
	
	public void init() {
		empty = false;
		
		map = new ObjectMap[ContentType.all.length];
		
		for(ContentType type : ContentType.all){
            map[type.ordinal()] = new ObjectMap<>();
        }
        
		Events.on(EventType.ClientLoadEvent.class, e -> {
			load();
		});
	}
	
	
	private void load() {
		content.each(c -> {
			if(c instanceof MappableContent){
		    	MappableContent cc = (MappableContent)c;
		    	if(cc.minfo.mod == getCurrentMod()) {
		    	    String[] namety = cc.name.split("-");
		    	    boolean check = namety.length > 1;
		    	    //For modname-name-other
			    	map[cc.getContentType().ordinal()].put(check ? namety[1] : namety[0], cc);
		    	}
			}
		});
	}
	public boolean isEmpty() {
		return empty;
	}
	
	public UnlockableContent get(ContentType type, String name) {
		if(empty) throw new IllegalAccessError("Must be accessed after initialization");
		return (UnlockableContent)map[type.ordinal()].get(name);
	}
	
	public Item getItem(String name) {
		return (Item)get(ContentType.item, name);
	}
	
	public Block getBlock(String name) {
		return (Block)get(ContentType.block, name);
	}
	
	public Weather getWeather(String name) {
		return (Weather)get(ContentType.weather, name);
	}
	
	public ObjectMap<String, MappableContent> getType(ContentType type) {
		if(empty) throw new IllegalAccessError("Must be accessed after initialization");
		return map[type.ordinal()];
	}
	
	public LoadedMod getCurrentMod() {
	    return getLoadedMod(getName());
	}
	
	public LoadedMod getLoadedMod(String name) {
	    return mods.locateMod(name);
	}
	
	public String getName() {
		return content.transformName("").split("-")[0];
	}
}