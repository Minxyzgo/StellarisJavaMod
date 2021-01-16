package minxyzgo.mlib.entities;

import arc.scene.ui.layout.*;
import minxyzgo.mlib.type.*;

public interface Skillc {
    void build(Table parent);
    
    EntSkill ent();
    
    default DataSkill getSkill() {
        return Skills.pocSeq.get(skillId());
    }
    
    default byte skillId() {
        return -1;
    }
}