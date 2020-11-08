package stellaris.rule;

import static stellaris.rule.AsEvents.*;

import arc.Events;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.*;

import static stellaris.rule.AsEvents.*;

public abstract class Rule implements Entityc{
    public void loadRule(){
        Groups.all.add(this);
        Events.fire(new RuleLoadEvent(this));
    }
    
    abstract public void remote();
    
    abstract public RuleEnum type();
    
    @Override
    public void write(Writes write){
        write.i(type().ordinal());
    }
    
    @Override
    public void read(Reads read){
        Events.fire(new RuleReloadEnumEvent(this));
    }
}