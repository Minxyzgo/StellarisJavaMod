    package stellaris.type.abilities;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import stellaris.Main;
import stellaris.content.AsEffects;
import stellaris.type.units.InvisibleUnit;

public class InvisibleAbility extends BasicAbilities.PowerAbility {
    public int mainWeaponIndex = 0;
    public float reloadInvisible = 5 * 60;
    public boolean isUnVisibleShoot = true;
    public float unVisibleRange = 30;
    public boolean canDetection = true;
	public InvisibleAbility(float maxPower, float powerProduction, float consumePower) {
		super(maxPower, powerProduction, consumePower);
	}
	
	@Override
	public void powerAct(Unit unit) {
	    InvisibleUnit innerUnit = (InvisibleUnit)unit;
	    
	    if((!innerUnit.isVisible) && innerUnit.visDuction <= 0 && innerUnit.status() >= consumePower) {
	        Fx.unitSpawn.at(unit.x, unit.y, unit.rotation, unit.type);
	        innerUnit.status(Math.max(innerUnit.status() - consumePower * Time.delta, 0f));
	        innerUnit.isVisible = true;
	    }
	    
	    float length = Mathf.dst(unit.x, unit.y, unit.aimX, unit.aimY);
	    Teamc c = Units.closestTarget(unit.team, unit.x, unit.y, unVisibleRange);
	    if(innerUnit.isVisible && (((!unit.isPlayer() && length < unVisibleRange) || (unit.isPlayer() && c != null)) && isUnVisibleShoot || innerUnit.isShooting )){
	        innerUnit.isVisible = false;
	        innerUnit.visDuction = reloadInvisible;
	        
	      /*  if(!innerUnit.isPlayer() && !innerUnit.isVisible) {
	            float rotation = Angles.angle(unit.x, unit.y, unit.aimX, unit.aimY);
	            innerUnit.rotation(rotation - 180f);
	        }*/ 
	    }
	    
	    if(!innerUnit.isVisible) innerUnit.visDuction = Math.max(innerUnit.visDuction - Time.delta, 0);
	   // if(Main.test) Vars.ui.showInfoToast("isV:" + innerUnit.isVisible + " vd:" + innerUnit.visDuction, Time.delta);
	}
	
	@Override
	public boolean con(Unit unit) {
	    return false;
	}
	
	@Override
	public String localized() {
	    return "Invisible";
	}
}