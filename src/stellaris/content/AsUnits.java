package stellaris.content;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import stellaris.type.abilities.*;
import stellaris.type.units.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.graphics.*;
import mindustry.gen.*;
import mindustry.world.meta.BlockFlag;

public class AsUnits implements ContentList {
	public static UnitType fship, fhz, faz;
	@Override
	public void load() {
		fship = new FSalPixShip("fs") {
			{
				PowerWeapon ls = new PowerWeapon(FSalPixShip.smallLaserName) {
					{
					    typeId = "smallLaser";
					    consumePower = 5f;
						x = 21;
						y = 24;
						continuous = true;
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
				PowerWeapon bc = new PowerWeapon(FSalPixShip.bcWeapon) {
					{
					    consumePower = 25f;
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
					PowerWeapon wn = bc.copy();
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

		fhz = new PowerUnit("fhz") {
			{
			    abilities.add(new InvisibleAbility());
			    maxPower = 480f;
				health = 750f;
				commandLimit = 4;
				constructor = InvisibleUnit::new;
				armor = 11f;
				speed = 2.7f;
				rotateSpeed = 4f;
				accel = 0.05f;
				drag = 0.015f;
				lowAltitude = false;
				flying = true;
				engineOffset = 4f;
				rotateShooting = false;
				hitSize = 8f;
				range = 140f;
				targetAir = false;
				targetFlag = BlockFlag.battery;

				weapons.add(
				new PowerWeapon() {
					{
						x = y = 0f;
						mirror = false;
						spacing = 15f;
						inaccuracy = 5f;
						//ejectEffect = Fx.casing3;
						reload = 55f;
						shotDelay = 15f;
						minShootVelocity = 0.01f;
						shots = 2;
						soundPitchMin = 1f;
						shootSound = Sounds.plasmadrop;
						bullet = AsBullets.purpleBomb;
						shootCone = 180f;
						ignoreRotation = true;

					}
				});
			}
			@Override
			public void drawBody(Unit unit) {
				InvisibleUnit innerUnit = (InvisibleUnit)unit;
				applyColor(unit);
				if (Vars.player.team() == unit.team && innerUnit.isVisible) {
					Draw.alpha(0.25f);
					Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
				} else if (!innerUnit.isVisible) {
					Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
				}

				Draw.reset();
			}

			@Override
			public void applyColor(Unit unit) {
				InvisibleUnit innerUnit = (InvisibleUnit)unit;
				if (innerUnit.isVisible) {
					Draw.mixcol(Color.white, 0.25f);
				} else {
					super.applyColor(unit);
				}
			}

			@Override
			public void drawWeapons(Unit unit) {

			}

			@Override
			public void drawEngine(Unit unit) {
				InvisibleUnit innerUnit = (InvisibleUnit)unit;
				if (!innerUnit.isVisible) super.drawEngine(unit);
			}
		};
		
		faz = new FSAircraftCarrier("faz");
	}
}