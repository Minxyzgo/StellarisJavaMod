package stellaris.type.units;

import mindustry.type.UnitType;
import mindustry.type.Weapon;
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
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
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
		constructor = () -> new FShip();
		flying = true;
		Weapon w = new FSMainWeapon("mainTurret");
		w.bullet = new FSLaserBullet();
		w.reload = 555;
		w.shotDelay = 180;
		weapons.add(w);
	}

	public FSAbility ability = new FSAbility();

	public class FShip extends MechUnit {

	}

	public static class FSAbility extends Ability {
		public static TextureRegion[] lights, lasers;
		public static TextureRegion laserHit;
		public static final int count = 8;
		public final Interval timer = new Interval();
		public static final float frameSpeed = 1.25f;
		public Unit innerUnit;

		static {
			lights = new TextureRegion[count];
			lasers = new TextureRegion[6];
			laserHit = atlas.find(content.transformName("laser-end"));
			for (int i = 0; i < count; i++) {
				if (i < 6) lights[i] = atlas.find(content.transformName("light-" + i));
				lasers[i] = atlas.find(content.transformName("laser-" + i));
			}
		}

		@Override
		public void update(Unit unit) {
			if (innerUnit == null) innerUnit = unit;
		}

		@Override
		public void draw(Unit unit) {

			WeaponMount mount = MainShoot(unit);
			if (mount.shoot) {
				if (timer.get(mount.weapon.shotDelay / 60)) {
					Draw.color();
					Draw.blend(Blending.additive);
					Drawf.light(unit.team, mount.aimX, mount.aimY, unit.hitSize * 2.25f, Color.valueOf("#0092DD"), mount.heat);
					Draw.blend();
					Draw.color();
					Draw.alpha(Mathf.absin(1.75f, count));
					Draw.rect(lights[(int)(mount.reload / frameSpeed) % count], mount.weapon.x, mount.weapon.y, unit.rotation - 90);
				}
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
	    public FSMainWeapon(String name){
            this.name = name;
        }
	}



	public class FSLaserBullet extends ContinuousLaserBulletType {
	    
		{
			absorbable = false;
			length = 1000;
			damage = 1000;
			drawSize = length * 1.25f;
			shake = 1f;
			fadeTime = 16f;
			float[] tscales = {2f, 0.6f, 0.4f, 0.2f};
			this.tscales = tscales;
			float[] strokes = {1.125f, 1f, 0.85f, 0.7f};
			this.strokes = strokes;
			float[] lens = {0.65f, 0.75f, 0.85f, 1f};
			lenscales = lens;
			width = 5f;
		    oscScl = 1.2f;
		    oscMag = 2.4f;
		    largeHit = true;
		}

		@Override
		public void draw(Bullet b) {
			float realLength = Damage.findLaserLength(b, length);
			float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
			float baseLen = realLength * fout;

			Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
			for (int s = 0; s < colors.length; s++) {
				Draw.color(Color.valueOf("#529DFF"));
				for (int i = 0; i < tscales.length; i++) {
					Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * 35f);
					Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout * strokes[s] * tscales[i]);
					Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);

				}
				Drawf.laser(b.team, FSAbility.lasers[(int)Mathf.absin(FSAbility.frameSpeed, FSAbility.count - 0.001f)], FSAbility.laserHit, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y);
				Angles.randLenVectors(b.id, 3, 25 * b.fin(), b.rotation(), 360, (x, y) -> {
					Draw.color(Color.white, Color.valueOf("#529DFF"), b.fin() + 1.25f);
					Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.fout() * 5);
					Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Mathf.angle(x, y), b.fslope() * 12 + 1);
				});

			}
			float x2 = Angles.trnsx(b.rotation(), baseLen);
			float y2 = Angles.trnsy(b.rotation(), baseLen);
			new Effect(40, e -> {
				Draw.color(Color.white, Color.valueOf("#529DFF"), e.fin() * 0.625f);
				Angles.randLenVectors(e.id, 7, 1 + 60 * e.fin(), e.rotation, 360, (x, y) -> {
					Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 7 + 4);
				});
				Lines.stroke(e.fout() * 1.312f);
				Lines.circle(e.x, e.y, e.fin() * 60);
			}).at(x2, y2);
			Tmp.v1.trns(b.rotation(), baseLen * 1.1f);

			Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 40, lightColor, 0.7f);
			Draw.reset();



			//FSAbility ab = FSalPixShip.this.ability;
			//WeaponMount mount = ab.MainShoot(ab.innerUnit);
			//float rotation = mount.rotation - 90;
		}
	}
}