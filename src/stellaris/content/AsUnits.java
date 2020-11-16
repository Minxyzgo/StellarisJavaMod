package stellaris.content;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import stellaris.type.units.FSalPixShip;
import stellaris.type.units.PowerUnit;

public class AsUnits implements ContentList {
	public static UnitType fship;
	@Override
	public void load() {
		fship = new FSalPixShip("fs") {
			{
				Weapon ls = new Weapon(FSalPixShip.smallLaserName) {
					{
						x = 21;
						y = 24;
						bullet = new FSalPixShip.SmallLaser();
						reload = 18f;
						rotate = true;
						rotateSpeed = 7f;
						ejectEffect = Fx.none;
						shootCone = 15f;
						recoil = 0.25f;
					}
				},
				ls2 = ls.copy(),
				ls3 = ls.copy(),
				ls4 = ls.copy();
				ls2.x = 21;
				ls2.y = 46;
				ls3.x = 13;
				ls3.y = 87;
				ls4.x = 26;
				ls4.y = 64;

				weapons.add(ls);
				weapons.add(ls2);
				weapons.add(ls3);
				weapons.add(ls4);
			}
		};
	}
}