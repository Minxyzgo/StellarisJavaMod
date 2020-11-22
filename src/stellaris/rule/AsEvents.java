package stellaris.rule;

import arc.func.Cons;
import stellaris.type.units.InvisibleUnit;

public class AsEvents {
	public static class RuleLoadEvent{
	    public Rule r;
	    public RuleLoadEvent(Rule rule){
	        r = rule;
	    }
	}
	
	public static class RuleDisposeEvent{
	    public Rule rule;
	    public Cons<Rule> cons;
	    public RuleDisposeEvent(Rule rule, Cons<Rule> cons){
	        this.rule = rule;
	        this.cons = cons;
	    }
	}
	
	public static class RuleReloadEnumEvent{
	    public Rule r;
	    public RuleReloadEnumEvent(Rule r){
	        this.r = r;
	    }
	}
	
	
}