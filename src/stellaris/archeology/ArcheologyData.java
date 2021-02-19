package stellaris.archeology;

import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

public class ArcheologyData {
    
    public int events = 0;
    public int schedule = 0;
    public float progress = 0;
    public boolean crafting = false;
    public boolean finish = false;
    
    public Seq<ArcheologyEvent> totalEvents = new Seq<>();
    
    public @Nullable ArcheologyEvent beginEvent;
    public @Nullable Building build;
}