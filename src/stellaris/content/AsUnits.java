package stellaris.content;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import stellaris.type.units.FSalPixShip;
import stellaris.type.units.PowerUnit;

public class AsUnits implements ContentList{
    public static UnitType fship;
    @Override
	public void load(){
	    fship = new FSalPixShip("fs") {{
	        Weapon ls = new PowerUnit.PowerWeapon(Vars.content.transformName("smallLaserTurret")) {{
	            x = 21;
		        y = 24;
	    	    ejectEffect = Fx.none;
	    	    shootCone = 15f;
	        }};
	        weapons.add(ls);
	    }};
	}
}