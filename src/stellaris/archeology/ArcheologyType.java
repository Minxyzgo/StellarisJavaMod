package stellaris.archeology;

import arc.*;

public enum ArcheologyType {
    begin,
        
    intermediate,
        
    finalEvent;
    
    public String localized() {
        return Core.bundle.get("ArcheologyType." + toString(), toString());
    }
}