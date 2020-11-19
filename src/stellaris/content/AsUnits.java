package stellaris.content;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentList;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import stellaris.type.units.FSalPixShip;
import stellaris.type.units.PowerUnit;
import arc.func.Cons2;

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
						bullet = AsBullets.smallLaser;
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
				ls4.y = -64;

				weapons.add(ls);
				weapons.add(ls2);
				weapons.add(ls3);
				weapons.add(ls4);
				
				/*  丨  32    丨  21
				39    丨  21
				55    丨  20
				80    丨  16
				96    丨  12
				100   丨  12-
				28   丨  26-
				39   丨  26-
				88   丨  26*/
				Weapon bc = new Weapon(FSalPixShip.bcWeapon) {
				    {
				        shots = 3;
				        reload = 35f;
				        shootCone = 25f;
				        inaccuracy = 3f;
				        spacing = 3f;
				        ejectEffect = Fx.none;
				        bullet = new FSalPixShip.BcBulletType();
				        rotate = true;
				        rotateSpeed = 6f;
				        x = 21;
				        y = 32;
				    }
				};
				Cons2<Float, Float> bw = (x, y) -> {
				    Weapon wn = bc.copy();
				    wn.x = x;
				    wn.y = y;
				    weapons.add(wn);
				    
				};
				bw.get(21f, 40f);
				bw.get(20f, 56f);
				bw.get(16f, 81f);
				bw.get(12f, 97f);
				bw.get(12f, 101f);
				bw.get(26f, -28f);
				bw.get(26f, -39f);
				bw.get(26f, -88f);
			}
		};
	}
}