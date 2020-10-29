package stellaris.type.blocks.turrets.interfaces;

import arc.func.Prov;
import stellaris.type.blocks.turrets.Level;

public interface BuildT {
    
    Prov<Level> level = () -> Level.open;
	default Level getLevel() {
	    return level.get();
	}
	     
	default boolean orLevel(Level level) {
	    return getLevel().ordinal() < level.ordinal();
	    }
}