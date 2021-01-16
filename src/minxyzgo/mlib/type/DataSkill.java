package minxyzgo.mlib.type;

import arc.func.*;
import arc.input.*;
import mindustry.gen.*;

public class DataSkill {
    public final Cons2<Entityc, String[]> cons;
    public final int id;
    public Func<KeyCode, Boolean> listener = t -> false;
    
    public DataSkill(Cons2<Entityc, String[]> cons) {
        this.cons = cons;
        this.id = Skills.pocSeq.size + 1;
        Skills.pocSeq.add(this);
    }
    
    public DataSkill(Cons2<Entityc, String[]> cons, Func<KeyCode, Boolean> listener) {
        this(cons);
        this.listener = listener;
    }
}