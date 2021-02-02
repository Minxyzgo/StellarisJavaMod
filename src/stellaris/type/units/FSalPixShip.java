package stellaris.type.units;

import mindustry.content.StatusEffects;
import mindustry.world.meta.BlockFlag;
import minxyzgo.mlib.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.input.SkillButtonStack;
import minxyzgo.mlib.math.UnitMathf;
import minxyzgo.mlib.type.DataSkill;
import stellaris.type.abilities.InductionAbility;
import arc.*;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;

//import stellaris.type.units.PowerUnit.PowerWeapon;
import static mindustry.Vars.*;
import static arc.Core.*;

public class FSalPixShip extends PowerUnit implements Skillc {
	private static int classId = Tool.nextClassId(FShip::new);
	
	public FSMainWeapon mainWeapon;

	public DataSkill[] skills;

	public FSalPixShip(String name) {
		super(name);
		abilities.add(sability = new ForceFieldAbility(320, 100, 150000, 550));
		abilities.add(new InductionAbility());
	}
	public static final int count = 8;
	public static final float frameSpeed = 3f;
	public float mainConsunePower = 2000f;
	public SkillButtonStack owner;
	public static String smallLaserName = content.transformName("smallLaserTurret"),
						 bcWeapon = content.transformName("BcWeapon");
	public static Effect mainShootEffect = new Effect(25, e -> {
		Draw.color(Color.white, e.color, e.fin());

		Lines.stroke(e.fin() * 3f);
		Lines.circle(e.x, e.y, e.fout() * 60f);
		Lines.stroke(e.fin() * 1.75f);
		Lines.circle(e.x, e.y, e.fout() * 45f);

		Angles.randLenVectors(e.id, 25, 1 + 120 * e.fout(), e.rotation, 100, (x, y) -> {
			Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12f + 1);
		});
	}),
	despawnSLaserEffect = new Effect(40, e -> {
		Draw.color(Color.white, e.color, e.fin() * 0.625f);
		Angles.randLenVectors(e.id, 7, 1 + 60 * e.fin(), e.rotation, 360, (x, y) -> {
			Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6 + 12);
		});
		Lines.stroke(e.fout() * 5.125f);
		Lines.circle(e.x, e.y, e.fin() * 15);
	});
	public static TextureRegion skillRegion = null;



	{

		Tool.onLoad(() -> {
			
			owner = new SkillButtonStack(, 50f) {{
			    button = new InductionAbility.InductionSkill(this, region, new SkillButtonStack.SkillStyle());
			}};

			skills = new DataSkill[]{owner.button};
		});

		lowAltitude = true;
		health = 125000;
		flying = true;
		speed = 0.17f;
		drag = 0.05f;
		hitSize = 150f;
		accel = 0.12f;
		rotateSpeed = 0.23f;
		armor = 85f;
		trailLength = 120;
		trailX = 35f;
		trailY = -71f;
		trailScl = 4f;
		destructibleWreck = false;
		targetFlag = BlockFlag.turret;
		maxPower = 8000f;
		powerProduction = 5f;
		conShieldPower = 2f;
		faceTarget = true;
		rotateShooting = true;
		drawShields = false;
		constructor = FShip::new;


		mainWeapon = new FSMainWeapon(content.transformName("mainTurret")) {
			{
				bullet = new FSLaserBullet() {
					{
						lifetime = 185f;
						shake = 2.5f;
						shootEffect = new Effect(30, e -> {
							Draw.color(Pal.lancerLaser, Color.white, e.fin());
							Lines.stroke(e.fin() * 3f);
							Lines.circle(e.x, e.y, e.fout() * 60);
							Lines.stroke(e.fin() * 1.75f);
							Lines.circle(e.x, e.y, e.fout() * 45);
							Angles.randLenVectors(e.id, 25, 1 + 120 * e.fout(), e.rotation, 100, (x, y) -> {
								Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 1);
							});
						});
					}
				};
				typeId = "mainWeapon";
				continuous = true;
				reload = 755;
				shotDelay = 2;
				shootY = 9.5f;
				recoil = 9f;
				shootSound = Sounds.laserbig;
				firstShotDelay = 39f;
				cooldownTime = 450f;
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
		weapons.add(mainWeapon);
		/*24    丨  2146    丨  2187    丨  13-64   丨  26*/
		/*
			weapons.add(l1);
			weapons.add(l2);
			weapons.add(l3);
			weapons.add(l4);
		*/
		weaponacts.put("mainWeapon", (mount, weapon, unit) -> {
			Bullet b = mount.bullet;
			FShip innerUnit = (FShip)unit;
			if (b != null && mount.bullet.isAdded() && mount.bullet.time < mount.bullet.lifetime && mount.bullet.type == weapon.bullet) {
			    float baseTime = b.type.lifetime;
				float[] shootxy = UnitMathf.getShootXY(unit, mount);
				if (b.timer(4, 5)) mainShootEffect.at(shootxy[0], shootxy[1], shootxy[4] + 90f, unit.team.color);
				innerUnit.power = Math.max(innerUnit.power - (mainConsunePower / baseTime * Time.delta), 0f);
				mount.heat = 1f;
			}

			return false;
		});
		weaponacts.put("smallLaser", (mount, weapon, unit) -> {
			Bullet b = mount.bullet;
			FShip innerUnit = (FShip)unit;
			if (mount.bullet.isAdded() && mount.shoot && b != null) {
				innerUnit.power = Math.max(innerUnit.power - (consumePower / Time.toSeconds * Time.delta), 0f);
				mount.reload = weapon.reload;
				b.data = mount;
				b.time(0f);
				mount.heat = 1f;
			}
			
			if(!mount.shoot) {
			    mount.bullet = null;
			}
			
			return false;
		});

	}
	
	@Override
	public void load() {
	    super.load();
	    skillRegion = Core.atlas.find(content.transformName("jumpSkill"));
	}

	@Override
	public void draw(Unit unit) {
		super.draw(unit);
		float s = 0.3f;
		float ts = 0.6f;
		for (WeaponMount mount : unit.mounts) {
			if (mount.weapon != mainWeapon) continue;
			if (mount.bullet == null || !mount.bullet.isAdded()) {
				Draw.color();
				Draw.blend(Blending.additive);
				Draw.color(unit.team.color);
				Draw.z(110);
				Tmp.v1.trns(unit.rotation, ((FSLaserBullet)mainWeapon.bullet).length * 1.1f);
				Draw.alpha(mount.reload * ts * (1f - s + Mathf.absin(Time.time, 3f, s)));
				float[] shootxy = UnitMathf.getShootXY(unit, mount);
				Drawf.laser(unit.team, FSMainWeapon.warning, atlas.find("clear"), shootxy[0], shootxy[1], unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
				Draw.color();
				Drawf.light(unit.team, shootxy[2], shootxy[3], unit.hitSize * 2.25f, Color.valueOf("#0092DD"), mount.heat);
				Draw.blend();
				Draw.color();
				Draw.alpha(Mathf.absin(1.75f, count));
				Draw.rect(FSMainWeapon.lightRegions[(int)(mount.reload / frameSpeed) % 6], shootxy[2], shootxy[3], shootxy[4]);
				Draw.reset();
			}
		}
	}

	public static class FShip extends BasePowerEntityUnit implements Trailc {
		transient Trail trail = new Trail(6);
		@Override
		public int classId() {
			return classId;
		}

		@Override
		public void update() {
			trail.length = type.trailLength;

			float scale = elevation();
			float offset = 64f / 2f + 64f / 2f * scale;

			float cx = x + Angles.trnsx(rotation + 180, offset), cy = y + Angles.trnsy(rotation + 180, offset);
			trail.update(cx, cy);
			super.update();
		}
		/*
		@Override
		public void add() {
		    super.add();
		    Main.asUi.play(Musics.game8);
		}
		*/
		@Override
		public Trail trail() {
			return trail;
		}

		@Override
		public void trail(Trail arg0) {

		}
	}

	public static class FSMainWeapon extends PowerWeapon {
		public static TextureRegion[] laserRegions = new TextureRegion[count], lightRegions = new TextureRegion[6];
		public static TextureRegion laserHit, warning;
		public FSMainWeapon(String name) {
			this.name = name;
		}

		{
			consumePower = 2000f / Time.toSeconds;
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
			Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * 0.1f);
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
		public void hit(Bullet b) {
			new Effect(20f, e -> {
				Draw.color(Color.white, b.team.color, e.fin());
				Angles.randLenVectors(e.id, 5, e.finpow() * 6f, e.rotation, 20f, (x2, y2) -> {
					Fill.circle(e.x + x2, e.y + y2, e.fout() * 1.5f);
				});
			}).at(b.x, b.y, b.rotation());
			hit(b, b.x, b.y);

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
			Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
			Angles.randLenVectors(b.id, 3, 25 * b.fin(), b.rotation(), 360, (x, y) -> {
				Draw.color(Color.white, b.team.color, b.fin() + 1.25f);
				Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.fout() * 5);
				Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Mathf.angle(x, y), b.fslope() * 12 + 1);
			});
			Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout);
			Draw.blend(Blending.additive);
			Drawf.laser(b.team, FSMainWeapon.laserRegions[(int) Mathf.absin(Time.time, frameSpeed, count - 0.001f)], FSMainWeapon.laserHit, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y);
			Angles.randLenVectors(b.id, 5, 1 + 75 * b.fin(), b.rotation(), 180, (x, y) -> {
				Lines.stroke(b.fout() * 0.75f);
				Lines.lineAngle(b.x + x, b.y + y, b.rotation(), b.fslope() * 6.25f + 4);
			});
			Draw.color(Color.white);
			Lines.stroke(2f);
			Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * 0.45f, false);
			Draw.blend();
			Draw.color();
			if (b.timer(2, 8)) {
				despawnSLaserEffect.at(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.team.color);
			}

			Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 40, lightColor, 0.7f);
			Draw.reset();
		}
	}

	@Override
	public DataSkill[] getSkill() {
		return skills;
	}
}