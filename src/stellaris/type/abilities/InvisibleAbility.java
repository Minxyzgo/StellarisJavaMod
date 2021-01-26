    package stellaris.type.abilities;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import stellaris.Main;
import stellaris.content.AsEffects;
import stellaris.type.units.InvisibleUnit;
import stellaris.type.units.PowerUnit;

public class InvisibleAbility extends Ability {
    public int mainWeaponIndex = 0;
    public float reloadInvisible = 5 * 60;
    public boolean isUnVisibleShoot = true;
    public float unVisibleRange = 30;
    public boolean canDetection = true;
	
	
	@Override
	public void update(Unit unit) {
	    InvisibleUnit innerUnit = (InvisibleUnit)unit;
	    PowerUnit type = (PowerUnit)unit.type;
	    
	    if((!innerUnit.isVisible) && innerUnit.visDuction <= 0 && innerUnit.status() >= type.consumePower) {
	        Fx.unitSpawn.at(unit.x, unit.y, unit.rotation, unit.type);
	        innerUnit.status(Math.max(innerUnit.status() - type.consumePower * Time.delta, 0f));
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
	public String localized() {
	    return "Invisible";
	}
}