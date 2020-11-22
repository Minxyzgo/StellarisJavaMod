package stellaris.type.abilities;

import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import stellaris.type.intf.Powerc;

public class BasicAbilities {
    
	public static class PowerAbility extends Ability {
	    public PowerAbility(float maxPower, float powerProduction, float consumePower) {
	        this.maxPower = maxPower;
	        this.powerProduction = powerProduction;
	        this.consumePower = consumePower;
	    }
	    
	    public float maxPower = 100f;
	    public float powerProduction = 1f;
	    public float consumePower = 0f;
	    
	    @Override
	    public void update(Unit unit) {
	        if(!(unit instanceof Powerc)) return;
	        Powerc c = (Powerc)unit;
	        if(c.powerc() <= 0.99f) {
	            c.status(Math.min(c.status() + powerProduction * Time.delta, c.maxPower()));
	        }
	        
	        if(con(unit)) {
	            if(c.conPower(consumePower)) c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
	        }
	        
	        powerAct(unit);
	    }
	    
	    public boolean con(Unit unit) {
	        Powerc iunit = (Powerc)unit;
	        return consumePower > 0f && iunit.conPower(consumePower);
	    }
	    
	    public void powerAct(Unit c){
	        
	    }
	}
	
	public static class PowerForceAbility extends PowerAbility {
	    public PowerForceAbility(float maxPower, float powerProduction, float consumePower) {
	        super(maxPower,powerProduction, consumePower);
	    }
	    
	    public float maxShield = 150000f;
	    
	    @Override
	    public boolean con(Unit unit) {
	        return super.con(unit) && unit.shield < maxShield;
	    }
	    
	    @Override
	    public void powerAct(Unit unit) {
	        Powerc p = (Powerc)unit;
	        if(!p.conPower(consumePower)) unit.shield = -maxShield * 0.75f;
	    }
	}
}