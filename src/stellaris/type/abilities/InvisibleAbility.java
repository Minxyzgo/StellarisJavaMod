package stellaris.type.abilities;

import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import stellaris.Main;
import stellaris.type.units.InvisibleUnit;

public class InvisibleAbility extends BasicAbilities.PowerAbility {
    public float invisibleDuration = 8 * 60;
    public float reloadInvisible = 5 * 60;
    public boolean isUnVisibleShoot = true;
    public float unVisibleRange = 200;
    public boolean canDetection = true;
	public InvisibleAbility(float maxPower, float powerProduction, float consumePower) {
		super(maxPower, powerProduction, consumePower);
	}
	
	@Override
	public void powerAct(Unit unit) {
	    InvisibleUnit innerUnit = (InvisibleUnit)unit;
	    if((!innerUnit.isVisible) && innerUnit.visDuction > 0f && innerUnit.visDuction <= invisibleDuration && innerUnit.conPower(consumePower)) {
	        Fx.unitSpawn.at(unit.x, unit.y, unit.rotation - 90, unit);
	        innerUnit.isVisible = true;
	    }
	    Teamc u = Units.closestTarget(unit.team, unit.x, unit.y, unVisibleRange);
	    if(innerUnit.visDuction < 0f || !innerUnit.conPower(consumePower) || ((u == null && isUnVisibleShoot) || innerUnit.isShooting)){
	        innerUnit.isVisible = false;
	        innerUnit.visDuction = invisibleDuration + reloadInvisible;
	    }
	    
	    if(innerUnit.conPower(consumePower)) innerUnit.visDuction -= Time.delta;
	    if(Main.test) Vars.ui.showInfoToast("isV:" + innerUnit.isVisible + " vd:" + innerUnit.visDuction, Time.delta);
	}
	
	@Override
	public boolean con(Unit unit) {
	    InvisibleUnit innerUnit = (InvisibleUnit)unit;
	    return super.con(unit) && innerUnit.isVisible;
	}
	
	@Override
	public String localized() {
	    return "Invisible";
	}
}