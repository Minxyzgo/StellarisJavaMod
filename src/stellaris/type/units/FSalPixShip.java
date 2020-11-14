package stellaris.type.units;

import mindustry.type.UnitType;
import mindustry.content.StatusEffects;
import mindustry.type.Weapon;
import stellaris.Main;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Interval;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;

import static mindustry.Vars.*;
import static arc.Core.*;
public class FSalPixShip extends UnitType {
	public FSalPixShip(String name) {
		super(name);
		abilities.add(ability);
		abilities.add(new ForceFieldAbility(320, 100, 150000, 550));
		constructor = () -> new FShip();

		Weapon w = new FSMainWeapon(content.transformName("mainTurret")) {
			{
				bullet = new FSLaserBullet();
				reload = 755;
				shotDelay = 15;
				shootY = 9.5f;
				occlusion = 20f;
				recoil = 9f;
				shootSound = Sounds.laserbig;
				x = 0f;
				y = 75f;
				rotateSpeed = 0.12f;
				rotate = true;
				ejectEffect = Fx.none;
			}
		};
		weapons.add(w);
	}

	{
		health = 125000;
		flying = true;
		speed = 0.01f;
		drag = -0.25f;
		hitSize = 170f;
		accel = 0.12f;
		rotateSpeed = 0.09f;
		armor = 85f;
		trailLength = 70;
		trailX = 35f;
		trailY = -34f;
		trailScl = 1.5f;

	}

	public static final int count = 8;
	public static final float frameSpeed = 3f;

	public FSAbility ability = new FSAbility();

	public class FShip extends MechUnit {
		public float bulletLife = -1;

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(bulletLife);
		}

		@Override
		public void read(Reads read) {
			super.read(read);
			bulletLife = read.f();
		}
	}

	public class FSAbility extends Ability {


		@Override
		public void update(Unit unit) {
			FShip innerUnit = (FShip)unit;
			WeaponMount mount = MainShoot(unit);
			
			Bullet b = mount.bullet;

			//if (b.data == null) b.data = this;
			float baseTime = ((FSMainWeapon)mount.weapon).shootDurtion;

			Weapon weapon = mount.weapon;
			float rotation = innerUnit.rotation - 90;
			float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
			float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
			float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
				  wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
			float shootX = wx + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY),
				  shootY = wy + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
			float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (innerUnit.rotation - innerUnit.angleTo(mount.aimX, mount.aimY));
			
			
			//if (mount.reload == weapon.reload && (b == null || (b != null && !(b.type instanceof FSLaserBullet)))) b = weapon.bullet.create(innerUnit, wx, wy, weaponRotation);
			

			if (innerUnit.bulletLife < baseTime && b != null) {
			    innerUnit.isShooting(true);
			    mount.reload = weapon.reload;
				b.rotation(f);
				b.set(shootX, shootY);
				b.time(0f);
				//mount.shoot = true;
				mount.heat = 1f;
				innerUnit.bulletLife += Time.delta;
			}
			
			if (innerUnit.bulletLife >= baseTime && b != null) {
				innerUnit.bulletLife = 0;
				innerUnit.isShooting(false);
				mount.reload--;
				//mount.shoot = false;
					//b.absorb();
				b = null;
				return;
			}
			
			if(Main.test) ui.showInfoToast("m-r" + mount.reload + " m-s:" + mount.shoot + " bIn:" + (mount.bullet == null) + " isS:" + unit.isShooting + " sbd:" + innerUnit.bulletLife, 0.1f);

			if (b != null && innerUnit.isShooting && b.timer(4, 5)) {
				new  Effect(25, e -> {
					Draw.color(Color.valueOf("#0092DD"), Color.valueOf("#529DFF"), e.fin());

					Lines.stroke(e.fin() * 3f);
					Lines.circle(e.x, e.y, e.fout() * 60f);
					Lines.stroke(e.fin() * 1.75f);
					Lines.circle(e.x, e.y, e.fout() * 45f);

					Angles.randLenVectors(e.id, 25, 1 + 120 * e.fout(), e.rotation, 100, (x, y) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12f + 1);
					});
				}).at(shootX, shootY, f);
			}
		}

		@Override
		public void draw(Unit unit) {

			WeaponMount mount = MainShoot(unit);
			Weapon weapon = mount.weapon;
			float rotation = unit.rotation - 90;
			float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
			float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
			float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
				  wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
			float shootX = wx + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY),
				  shootY = wy + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
			float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
			float s = 0.3f;
			float ts = 0.6f;
			if (mount.reload <= 485f) {
				Draw.color();
				Draw.blend(Blending.additive);
				Draw.color(Color.valueOf("#D64821"));
				Tmp.v1.trns(f, ((FSLaserBullet)weapon.bullet).length * 1.1f);
				Draw.alpha(mount.reload * ts * (1f - s + Mathf.absin(Time.time(), 3f, s)));
				Drawf.laser(unit.team, FSMainWeapon.warning, atlas.find("clear"), shootX, shootY, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
				Draw.color();
				Drawf.light(unit.team, wx, wy, unit.hitSize * 2.25f, Color.valueOf("#0092DD"), mount.heat);
				Draw.blend();
				Draw.color();
				Draw.alpha(Mathf.absin(1.75f, count));
				Draw.rect(FSMainWeapon.lightRegions[(int)(mount.reload / frameSpeed) % 6], wx, wy, weaponRotation + 90f);

			}
		}

		public @Nullable WeaponMount MainShoot(Unit unit) {
			for (WeaponMount mount : unit.mounts) {
				if (mount.weapon instanceof FSMainWeapon) return mount;
			}
			return null;
		}
	}

	public static class FSMainWeapon extends Weapon {
		public int shootDurtion;
		public static TextureRegion[] laserRegions = new TextureRegion[count], lightRegions = new TextureRegion[6];
		public static TextureRegion laserHit, warning;
		public FSMainWeapon(String name) {
			this.name = name;
		}

		{
			shootDurtion = 180;
			mirror = false;
		}

		@Override
		public void load() {
			super.load();
			laserHit = atlas.find("laser-end");
			warning = atlas.find(name + "-warning");
			for (int i = 0; i < count; i++) {
				laserRegions[i] = atlas.find(name + "-laser-" + i);
				if (i < 6) lightRegions[i] = atlas.find(name + "-light-" + i);
			}
		}
	}



	public class FSLaserBullet extends ContinuousLaserBulletType {

		{
			//absorbable = false;
			length = 450;
			damage = 1;
			drawSize = length * 1.25f;
			shake = 1f;
			fadeTime = 16f;
			float[] tscales = {2f, 0.6f, 0.4f, 0.2f};
			this.tscales = tscales;
			float[] strokes = {1.125f, 1f, 0.85f, 0.7f};
			this.strokes = strokes;
			float[] lens = {0.65f, 0.75f, 0.85f, 1f};
			lenscales = lens;
			width = 7f;
			oscScl = 1.2f;
			oscMag = 2.4f;
			largeHit = true;
			status = StatusEffects.none;
			hitEffect = new Effect(18, e -> {
				Draw.color(Color.valueOf("#D64821"));
				Angles.randLenVectors(e.id, 1, 80 * e.fin(), 0, 360, (x, y) -> {
				    Fill.poly(e.x + x, e.y + y, 6, 10 * e.fout());
				});
			});
		}

		@Override
		public void draw(Bullet b) {

			float realLength = Damage.findLaserLength(b, length);
			float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
			float baseLen = realLength * fout;

			Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
			/*for (int s = 0; s < colors.length; s++) {
				Draw.color(Color.valueOf("#529DFF"));
				/*for (int i = 0; i < tscales.length; i++) {
					Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * 35f);
					Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout * strokes[s] * tscales[i]);
					Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
				}




			}*/
			Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
			Angles.randLenVectors(b.id, 3, 25 * b.fin(), b.rotation(), 360, (x, y) -> {
				Draw.color(Color.white, Color.valueOf("#529DFF"), b.fin() + 1.25f);
				Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.fout() * 5);
				Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Mathf.angle(x, y), b.fslope() * 12 + 1);
			});
			Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout);
			
			Drawf.laser(b.team, FSMainWeapon.laserRegions[(int) Mathf.absin(Time.time(), frameSpeed, count - 0.001f)], FSMainWeapon.laserHit, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y);
			Angles.randLenVectors(b.id, 5, 1 + 75 * b.fin(), b.rotation(), 180, (x, y) -> {
				Lines.stroke(b.fout() * 0.75f);
				Lines.lineAngle(b.x + x, b.y + y, b.rotation(), b.fslope() * 6.25f + 4);
			});
			


			if (b.timer(2, 8)) {
				new Effect(40, e -> {
					Draw.color(Color.white, Color.valueOf("#529DFF"), e.fin() * 0.625f);
					Angles.randLenVectors(e.id, 7, 1 + 60 * e.fin(), e.rotation, 360, (x, y) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6 + 12);
					});
					Lines.stroke(e.fout() * 4.125f);
					Lines.circle(e.x, e.y, e.fin() * 45);
				}).at(b.x + Tmp.v1.x, b.y + Tmp.v1.y);
			}


			Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 40, lightColor, 0.7f);
			Draw.reset();



			//FSAbility ab = FSalPixShip.this.ability;
			//WeaponMount mount = ab.MainShoot(ab.innerUnit);
			//float rotation = mount.rotation - 90;
		}
	}
}