package stellaris.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.ctype.ContentList;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import stellaris.type.units.FSalPixShip;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;

public class AsBullets implements ContentList {
	public static BulletType smallLaser, purpleBomb;
	@Override
	public void load() {
		smallLaser = new FSalPixShip.SmallLaser();
		purpleBomb = new BasicBulletType() {
			{
				sprite = "large-bomb";
				width = height = 120 / 4f;
				maxRange = 30f;

//Draw.color(Color.valueOf("#7b68ee"),Color.valueOf("#e4ebff"),e.fin());
				lightColor = Color.valueOf("#7b68ee");
				lightningColor = Color.valueOf("#7b68ee");
				keepVelocity = false;
				spin = 2f;

				shrinkX = shrinkY = 0.7f;

				speed = 0.001f;
				collides = false;

				splashDamage = 1000;
				splashDamageRadius = 135f;
				lightning = 12;
				lightningCone = 360f;
				lightningLength = 7;
				lightningDamage = 45f;

				backColor = Color.valueOf("#7b68ee");
				frontColor = Color.valueOf("#e4ebff");
				mixColorTo = Color.valueOf("#e4ebff");

				hitSound = Sounds.plasmaboom;


				despawnShake = 4f;

				collidesAir = false;

				lifetime = 70f;
				despawnEffect = new Effect(25, e -> {
					Draw.color(Color.valueOf("#e4ebff"), e.fin());
					Lines.stroke(e.fout() * 3.5f);
					Lines.circle(e.x, e.y, 4f + e.finpow() * 65f);
					Draw.color(Color.valueOf("#e4ebff"), e.fin());
					for (int i = 0; i < 5; i++) {
						Drawf.tri(e.x, e.y, 6f, 100f * e.fout(), i * 90);
					}

					Draw.color();
					for (int i = 0; i < 5; i++) {
						Drawf.tri(e.x, e.y, 3f, 35f * e.fout(), i * 90);
					}
				});
				hitEffect = new Effect(30, e -> {
					Draw.color(Color.valueOf("#e4ebff"), e.fin());

					e.scaled(8, i -> {
						Lines.stroke(3f * i.fout());
						Lines.circle(e.x, e.y, 4f + i.fin() * 30f);
					});

					Draw.color(Color.valueOf("#7b68ee"));

					Angles.	randLenVectors(e.id, 8, 2f + 30f * e.finpow(), (x, y) -> {
						Fill.circle(e.x + x, e.y + y, e.fout() * 6 + 0.5f);
					});

					Draw.color(Color.valueOf("#e4ebff"));
					Lines.stroke(2.5f * e.fout());

					Angles.randLenVectors(e.id + 1, 6, 1f + 29f * e.finpow(), (x, y) -> {
						Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 4f);
					}
										 );
				});
			};
		};
	}
}