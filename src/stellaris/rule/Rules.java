package stellaris.rule;

import arc.Events;
import arc.struct.Seq;
import stellaris.rule.AsEvents.RuleReloadEnumEvent;

import static mindustry.game.EventType.*;
public final class Rules{
    public final static int LOAD = 0;
    public final static int RELOAD = 1;
    private Seq<Rule>[] rules = new Seq[1];
    public Rules(){
        rules[0] = new Seq<>();
        rules[1] = new Seq<>();
        Events.on(ResetEvent.class, e ->{
            getLoad().each(Rule::remote);
        });
        Events.on(WorldLoadEvent.class, e ->{
            getLoad().each(Rule::loadRule);
        });
        Events.on(RuleReloadEnumEvent.class, this::addEnum);
    }
    
    public Seq<Rule> getLoad(){
        return (Seq<Rule>)rules[LOAD];
    }
    
    public void addEnum(RuleReloadEnumEvent e){
        rules[RELOAD].add(e.r);
    }
}