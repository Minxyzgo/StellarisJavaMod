package stellaris.content;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import stellaris.type.abilities.*;
import stellaris.type.intf.Powerc;
import stellaris.type.units.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.Time;
import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.BlockFlag;

public class AsUnits implements ContentList {
	public static UnitType fship, fhz;
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

		fhz = new UnitType("fhz") {
			{
				health = 750f;
				commandLimit = 4;
				abilities.add(new InvisibleAbility(480f, 0.3f, 200f));
				constructor = () -> new InvisibleUnit();
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
				new Weapon() {
					{
						x = y = 0f;
						mirror = false;
						ejectEffect = Fx.casing3;
						reload = 55f;
						minShootVelocity = 0.01f;
						shots = 4;
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

			@Override
			public void display(Unit unit, Table table) {

				table.table(t -> {
					t.left();
					t.add(new Image(icon(Cicon.medium))).size(8 * 4).scaling(Scaling.fit);
					t.labelWrap(localizedName).left().width(190f).padLeft(5);
				}).growX().left();
				table.row();

				table.table(bars -> {
					bars.defaults().growX().height(20f).pad(4);

					bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
					bars.row();
					if (unit instanceof Powerc) {
						Powerc pu = (Powerc)unit;
						bars.add(new Bar("power", Pal.powerBar, pu::powerc));
					}
					bars.row();
					if (Vars.state.rules.unitAmmo) {
						bars.add(new Bar(ammoType.icon + " " + Core.bundle.get("stat.ammo"), ammoType.barColor, () -> unit.ammo / ammoCapacity));
						bars.row();
					}
				}).growX();

				if (unit.controller() instanceof LogicAI) {
					table.row();
					table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().left();
					table.row();
					table.label(() -> Iconc.settings + " " + (long)unit.flag + "").color(Color.lightGray).growX().wrap().left();
				}

				table.row();
			}

		};

	}
}