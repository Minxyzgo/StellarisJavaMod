package stellaris.content;
import arc.Events;
import arc.struct.ObjectMap;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.type.Item;
import mindustry.type.Weather;
import mindustry.world.Block;

import static mindustry.Vars.*;

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
			MappableContent cc = (MappableContent)content.getByID(c.getContentType(), c.id);
			String[] name = cc.name.split("-");
			if(name[0].equals(stname())) {
				map[cc.getContentType().ordinal()].put(name[1], cc);
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
	
	public String stname() {
		return content.transformName("").split("-")[0];
	}
}