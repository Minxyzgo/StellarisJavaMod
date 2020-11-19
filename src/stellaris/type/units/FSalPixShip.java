package stellaris.type.units;

import mindustry.type.UnitType;
import mindustry.content.StatusEffects;
import mindustry.type.Weapon;
import stellaris.Main;
import stellaris.content.AsBullets;
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
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
//import stellaris.type.units.PowerUnit.PowerWeapon;

import static mindustry.Vars.*;
import static arc.Core.*;
public class FSalPixShip extends PowerUnit {
	public FSalPixShip(String name) {
		super(name);
		abilities.add(ability);
		abilities.add(new ForceFieldAbility(320, 100, maxShield, 550));
		abilities.add(new LaserAbility());
		abilities.add(new BcAbility());
	}

	{
		health = 125000;
		flying = true;
		speed = 0.17f;
		drag = 0.05f;
		hitSize = 150f;
		accel = 0.12f;
		rotateSpeed = 0.23f;
		armor = 85f;
		trailLength = 70;
		maxPower = 8000f;
		powerProduction = 5f;
		constructor = () -> new FShip();

		Weapon
		w = new FSMainWeapon(content.transformName("mainTurret")) {
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
				//rotateSpeed = 0.12f;
				shootCone = 45f;
				rotate = false;
				ejectEffect = Fx.none;
			}
		};
		/*	l1 = new SmallLaserWeapon(n){{
			    x = 21;
			    y = 24;
			}},
			l2 = new SmallLaserWeapon(n){{
			    x = 21;
			    y = 46;
			}},
			l3 = new SmallLaserWeapon(n){{
			    x = 13;
			    y = 87;
			}},
			l4 = new SmallLaserWeapon(n){{
			    x = 26;
			    y = 64;
			}};
		*/
		weapons.add(w);
		/*24    丨  2146    丨  2187    丨  13-64   丨  26*/
		/*
			weapons.add(l1);
			weapons.add(l2);
			weapons.add(l3);
			weapons.add(l4);
		*/
	}


	public static final int count = 8;
	public static final float frameSpeed = 3f;
	public float mainConsunePower = 2000f;
	public FSAbility ability = new FSAbility();
	public static String smallLaserName = content.transformName("smallLaserTurret"),
						 bcWeapon = content.transformName("BcWeapon");

	public class FShip extends MechUnit implements Powerc {
		public float bulletLife = -1;
		public float power = 8000f;

		@Override
		public float powerc() {
			return power / ((PowerUnit)type).maxPower;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(bulletLife);
			write.f(power);
		}

		@Override
		public void read(Reads read) {
			super.read(read);
			bulletLife = read.f();
			power = read.f();
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
			//float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (innerUnit.rotation - innerUnit.angleTo(mount.aimX, mount.aimY));


			//if (mount.reload == weapon.reload && (b == null || (b != null && !(b.type instanceof FSLaserBullet)))) b = weapon.bullet.create(innerUnit, wx, wy, weaponRotation);

			if (unit.shield < maxShield) innerUnit.power = Math.max(innerUnit.power - forceConsumePower * Time.delta, 0f);

			if (innerUnit.power < maxPower) innerUnit.power = Math.min(innerUnit.power + powerProduction * Time.delta, maxPower);

			if (((innerUnit.bulletLife < baseTime && b != null && mount.reload == weapon.reload) || innerUnit.isShooting) && innerUnit.power > mainConsunePower) {
				innerUnit.isShooting(true);
				innerUnit.power = Math.max(innerUnit.power - (mainConsunePower / baseTime * Time.delta), 0f);
				mount.reload = weapon.reload;
				b.rotation(unit.rotation);
				b.set(shootX, shootY);
				b.time(0f);
				//mount.shoot = true;
				mount.heat = 1f;
				innerUnit.bulletLife += Time.delta;
			}

			if (innerUnit.bulletLife >= baseTime || b == null || innerUnit.power < mainConsunePower) {
				innerUnit.bulletLife = 0;
				innerUnit.isShooting(false);
				/*float reloade = weapon.reload - 1;
				if (b != null && innerUnit.power >= mainConsunePower) mount.reload = reloade;*/
				if (innerUnit.power <= 2.99f) innerUnit.shield = -(maxShield * 0.5f);
				//b.absorb();
				b = null;
			}


			if (Main.test) ui.showInfoToast("m-r" + mount.reload + " m-s:" + mount.shoot + " bIn:" + (mount.bullet == null) + " isS:" + unit.isShooting + " sbd:" + innerUnit.bulletLife, Time.delta);

			if (b != null && innerUnit.isShooting && b.timer(4, 5)) {
				new  Effect(25, e -> {
					Draw.color(Color.white, unit.team.color, e.fin());

					Lines.stroke(e.fin() * 3f);
					Lines.circle(e.x, e.y, e.fout() * 60f);
					Lines.stroke(e.fin() * 1.75f);
					Lines.circle(e.x, e.y, e.fout() * 45f);

					Angles.randLenVectors(e.id, 25, 1 + 120 * e.fout(), e.rotation, 100, (x, y) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12f + 1);
					});
				}).at(shootX, shootY, unit.rotation);


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
			//float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
			float s = 0.3f;
			float ts = 0.6f;
			if (!unit.isShooting) {
				Draw.color();
				Draw.blend(Blending.additive);
				Draw.color(unit.team.color);
				Tmp.v1.trns(unit.rotation, ((FSLaserBullet)weapon.bullet).length * 1.1f);
				Draw.alpha(mount.reload * ts * (1f - s + Mathf.absin(Time.time(), 3f, s)));
				Drawf.laser(unit.team, FSMainWeapon.warning, atlas.find("clear"), shootX, shootY, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
				Draw.color();
				Drawf.light(unit.team, wx, wy, unit.hitSize * 2.25f, Color.valueOf("#0092DD"), mount.heat);
				Draw.blend();
				Draw.color();
				Draw.alpha(Mathf.absin(1.75f, count));
				Draw.rect(FSMainWeapon.lightRegions[(int)(mount.reload / frameSpeed) % 6], wx, wy, weaponRotation);

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
			shootDurtion = 185;
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

	public static class LaserAbility extends Ability {
		public float consumePower = 25f;
		@Override
		public void update(Unit unit) {
			FShip innerUnit = (FShip)unit;
			for (WeaponMount mount : unit.mounts) {
				if (mount.weapon.name.equals(smallLaserName) && !(mount.weapon instanceof FSMainWeapon)) {
					Weapon weapon = mount.weapon;
					float rotation = unit.rotation - 90;
					float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
					float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
					float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
						  wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
					float shootX = wx + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY),
						  shootY = wy + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
					float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
					Bullet b = mount.bullet;
					boolean consume = innerUnit.power >= consumePower;
					if(b == null) return;
					if ((!(b.type instanceof SmallLaser) && consume && mount.shoot) || b.team != unit.team) return;

					if (mount.shoot && b != null && consume) {
						innerUnit.power = Math.max(innerUnit.power - (consumePower / Time.toSeconds * Time.delta), 0f);
						mount.reload = weapon.reload;
						b.data = mount;
						b.rotation(f);
						b.set(shootX, shootY);
						b.time(0f);
						mount.heat = 1f;
					}

					if (!mount.shoot || !consume) {
						b = null;
					}
				}
			}
		}
	}

	public static class SmallLaser extends ContinuousLaserBulletType {


		{
			tscales = new float[] {1f, 0.6f, 0.4f, 0.2f};
			strokes = new float[] {0.1f, 0.1f, 0.1f, 0.1f};
			damage = 60;
			pierce = true;
			length = 80f * 8f;
			largeHit = false;
		}

		@Override
		public void update(Bullet b) {
			Effect hit = new Effect(8, e -> {
				Draw.color(Color.white, b.team.color, e.fin());
				Lines.stroke(e.fout() * 1f + 0.2f);
				Lines.circle(e.x, e.y, e.fin() * 6f);
			});
			float realLength = Damage.findLaserLength(b, length);
			float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
			if (b.data == null || !(b.data instanceof WeaponMount))return;
			WeaponMount mount = (WeaponMount)b.data;
			float baseLen = Math.min(realLength * fout, Mathf.dst(b.x, b.y, mount.aimX, mount.aimY));
			if (b.timer(1, 5f)) {
				Damage.collideLine(b, b.team, hit, b.x, b.y, b.rotation(), baseLen, largeHit);
			}
		}

		@Override
		public void draw(Bullet b) {
			float realLength = Damage.findLaserLength(b, length);
			float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
			if (b.data == null || !(b.data instanceof WeaponMount))return;
			WeaponMount mount = (WeaponMount)b.data;
			float baseLen = Math.min(realLength * fout, Mathf.dst(b.x, b.y, mount.aimX, mount.aimY));

			//Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
			Draw.blend(Blending.additive);
			Draw.color(b.team.color);
			Draw.z(120f);
			Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout * 0.1f);
			Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
			Draw.blend();
			Draw.color();
			Draw.reset();
		}
	}

	public static class BcBulletType extends BasicBulletType {
		{
			lifetime = 70f;
			speed = 5f;
			height = 8f * 2f;
			width = 2f * 2f;
			damage = 45;
			sprite = content.transformName("BcBullet");
			splashDamage = 25f;
			splashDamageRadius = 6f;
			trailChance = 1.5f;
		}

		@Override
		public void update(Bullet b) {
			if (trailChance > 0) {
				if (Mathf.chanceDelta(trailChance)) {
					new Effect(12, e -> {
						Lines.stroke(e.fout() * 3);
						Angles.randLenVectors(e.id, 1, 1 + 0 * e.fin(), e.rotation, 0, (x, y) -> {
							Draw.color(Color.white, b.team.color, e.fin());
							Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 5);
						});
					}).at(b.x, b.y, b.rotation());
				}
			}
		}

		@Override
		public void hit(Bullet b, float x, float y) {
			new Effect(20f, e -> {
				Draw.color(Color.white, b.team.color, e.fin());
				Angles.randLenVectors(e.id, 5, e.finpow() * 6f, e.rotation, 20f, (x2, y2) -> {
					Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
				});
			}).at(x, y, b.rotation());
			hitSound.at(b);

			Effect.shake(hitShake, hitShake, b);
			if (splashDamageRadius > 0)
				Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), collidesAir, collidesGround);
		}

		@Override
		public void draw(Bullet b) {
			float height = this.height * ((1f - shrinkY) + shrinkY * b.fout());
			float width = this.width * ((1f - shrinkX) + shrinkX * b.fout());
			float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f);

			Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

			Draw.mixcol(mix, mix.a);

			Draw.color(b.team.color);
			Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);

			Draw.reset();
		}
		@Override
		public void despawned(Bullet b) {
			hit(b);
		}
	}

	public static class BcAbility extends Ability {
		public float con = 1f;

		@Override
		public void update(Unit unit) {
			for (WeaponMount mount : unit.mounts) {
				if (mount.weapon.name.equals(bcWeapon)) {

					FShip innerUnit = (FShip)unit;
					boolean consume = innerUnit.power >= con;
					if (mount.reload == mount.weapon.reload && consume) {
						innerUnit.power = Math.max(innerUnit.power - 1f, 0f);
					}
				}
			}
		}

		@Override
		public void draw(Unit unit) {
			for (WeaponMount mount : unit.mounts) {
				if (mount.weapon.name.equals(bcWeapon)) {
					FShip innerUnit = (FShip)unit;
					Weapon weapon = mount.weapon;
					float rotation = unit.rotation - 90;
					float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
					float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
					float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
						  wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
					float shootX = wx + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY),
						  shootY = wy + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
					float f = weapon.rotate ? weaponRotation + 90f : Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
					boolean consume = innerUnit.power >= con;
					if (mount.reload == mount.weapon.reload && consume) {
						new Effect(20f, e -> {
							Draw.color(Color.white, unit.team.color, e.fin());
							Lines.stroke(0.5f + e.fout());
							Lines.circle(e.x, e.y, e.fin() * 5f);
							Angles.randLenVectors(e.id, 5, e.finpow() * 6f, e.rotation, 20f, (x, y) -> {
								Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f);
							});
						}).at(shootX, shootY, f);
					}
				}
			}
		}
	}

	public class FSLaserBullet extends ContinuousLaserBulletType {

		{
			//absorbable = false;
			length = 1050;
			damage = 3000f;
			pierce = true;
			drawSize = length * 1.25f;
			shake = 1f;
			fadeTime = 16f;
			width = 7f;
			oscScl = 1.2f;
			oscMag = 2.4f;
			largeHit = true;
			status = StatusEffects.none;
			hitEffect = Fx.none;
		}

		@Override
		public void update(Bullet b) {
			Effect hit = new Effect(8, e -> {
				Draw.color(Color.white, b.team.color, e.fin());
				Lines.stroke(0.5f + e.fout());
				Lines.circle(e.x, e.y, e.fin() * 5f);
			});



			if (b.timer(1, 5f)) {
				Damage.collideLine(b, b.team, hit, b.x, b.y, b.rotation(), length, largeHit);
			}

			if (shake > 0) {
				Effect.shake(shake, shake, b);
			}
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
				Draw.color(Color.white, b.team.color, b.fin() + 1.25f);
				Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.fout() * 5);
				Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Mathf.angle(x, y), b.fslope() * 12 + 1);
			});
			Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout);
			Draw.blend(Blending.additive);
			Drawf.laser(b.team, FSMainWeapon.laserRegions[(int) Mathf.absin(Time.time(), frameSpeed, count - 0.001f)], FSMainWeapon.laserHit, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y);
			Angles.randLenVectors(b.id, 5, 1 + 75 * b.fin(), b.rotation(), 180, (x, y) -> {
				Lines.stroke(b.fout() * 0.75f);
				Lines.lineAngle(b.x + x, b.y + y, b.rotation(), b.fslope() * 6.25f + 4);
			});
			Draw.color(Color.white);
			Lines.stroke(2f);
			Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * 0.6f, false);
			Draw.blend();
			Draw.color();
			if (b.timer(2, 8)) {
				new Effect(40, e -> {
					Draw.color(Color.white, b.team.color, e.fin() * 0.625f);
					Angles.randLenVectors(e.id, 7, 1 + 60 * e.fin(), e.rotation, 360, (x, y) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6 + 12);
					});
					Lines.stroke(e.fout() * 5.125f);
					Lines.circle(e.x, e.y, e.fin() * 15);
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
