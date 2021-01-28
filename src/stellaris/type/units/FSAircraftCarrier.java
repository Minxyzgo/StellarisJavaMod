package stellaris.type.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import minxyzgo.mlib.*;
import stellaris.content.*;

import static mindustry.game.EventType.*;
public class FSAircraftCarrier extends PowerUnit {
	private static int classId = Tool.nextClassId(FSAircraftCarrierEntity::new);
	private static int classId_2 = Tool.nextClassId(FSAircraftCarrierEntity::new);
	public static Effect fsUnitSpirit = new Effect(14f, e -> {
		Unit to = (Unit)e.data();
		Draw.color(Color.valueOf("44A9EB"), Color.white, e.fin());
		Tmp.v1.set(e.x, e.y).interpolate(Tmp.v2.set(to), e.fin() * 2, Interp.pow2In).add(Tmp.v2.sub(e.x, e.y).nor().rotate90(1).scl(Mathf.randomSeedRange(e.id, 1f) * e.fslope() * 10f));
		float x = Tmp.v1.x, y = Tmp.v1.y;
		float size = 2.5f * e.fin();
		Lines.stroke(size);
		float len = Mathf.dst(x, y, to.x, to.y);
		Lines.lineAngle(x, y, to.rotation - 90f, len, false);
		Drawf.tri(e.x + x, e.y + y, Lines.getStroke() * 1.22f, size / 0.5f * 2f + size / 2f, to.rotation() - 90f);
		Fill.circle(x, y, 1f * size / 0.5f * e.fout());
		Lines.circle(x, y, 1.5f * size);
		Fill.circle(x, y, e.fout() * 10f + 2);
		Lines.stroke(e.fout() * 1.4f);
		Lines.circle(x, y, e.fin() * 60f);
		Lines.stroke(e.fin() * 12f);
		Angles.randLenVectors(e.id, 2, 1 + 40f * e.fin(), e.rotation, 25, (x2, y2) -> {
			Lines.lineAngle(x + x2, y + y2, Mathf.angle(x2, y2), e.fslope() * 12f + 1);
		});
	}),
	fsunitSpawn = new Effect(15f, e -> {
		Unit data = (Unit)e.data();
		Draw.alpha(e.fin() * Mathf.absin(Time.time, 4f, 1.5f));

		float scl = 1f + e.fout() * 2f;

		TextureRegion region = data.type.icon(Cicon.full);

		Draw.rect(region, e.x, e.y,
				  region.width * Draw.scl * scl, region.height * Draw.scl * scl, 180f);
		Draw.color(Color.valueOf("44A9EB"), Color.white, e.fin());
		Fill.circle(e.x, e.y, data.hitSize() * e.fout() * 0.5f);
		Lines.stroke(data.hitSize() * 0.5f * e.fout());
		Lines.circle(e.x, e.y, e.fin() * 52);
		for (int i : Mathf.signs) {
			Drawf.tri(e.x, e.y, data.hitSize() * 0.5f * e.fout(), 17f, e.rotation + 360f * i);
		}
		Angles.randLenVectors(e.id, 45, 65 * e.fin(), e.rotation, 360f, (x, y) -> {
			Lines.stroke(e.fout() * 2);
			Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 1);
		});
	}),
	spawnWave = new Effect(34, e -> {
		float size = (Float)e.data;
		Draw.color(e.color);
		Lines.stroke(size + e.fout() * 2f);
		Lines.circle(e.x, e.y, e.finpow() * e.rotation);
	});
	public Seq<PowerUnitSeq> spawnUnit = new Seq<>();
	public float spawnX, spawnY;

	public FSAircraftCarrier(String name) {
		super(name);
	}

	{
	    lowAltitude = true;
		health = 95000;
		flying = true;
		speed = 0.11f;
		drag = -0.02f;
		hitSize = 145f;
		accel = 0.08f;
		rotateSpeed = 0.14f;
		armor = 85f;
		destructibleWreck = false;
		targetFlag = BlockFlag.core;
		maxPower = 8000f;
		powerProduction = 5f;
		faceTarget = true;
		rotateShooting = true;
		drawShields = false;
		constructor = FSAircraftCarrierEntity::new;
		defaultController = FSACAIController::new;
		spawnUnit.add(new PowerUnitSeq(){{
		    type = (PowerUnit) AsUnits.fship;
		    maxSpawn = 1;
		    spawnTime = 200f;
		}});
		spawnUnit.add(new PowerUnitSeq(){{
		    type = (PowerUnit) AsUnits.fhz;
		    maxSpawn = 5;
		    spawnTime = 100f;
		}});
	}

	@Override
	public void tableBar(Unit unit, Table bars) {
		FSAircraftCarrierEntity fsUnit = (FSAircraftCarrierEntity)unit;
		bars.add(new Bar("build", Pal.accent, fsUnit::unitBuildc));
		bars.row();
		bars.add(new Bar("amount", Color.valueOf("44A9EB"), fsUnit::unitAmountc));
	}

	public int getSuitableType(boolean isFlying) {
	    if(isFlying) return 0;
	    return 1;
	}

	public static class FSAircraftCarrierEntity extends BasePowerEntityUnit {
		public int index = -1;
		public int spawnAmount = 0;
		public static float spawnTimer = 0;
		
		public float unitBuildc() {
		    if(index == -1) return 0;
		    return spawnTimer / ((FSAircraftCarrier)type).spawnUnit.get(index).spawnTime;
		}

		public float unitAmountc() {
			if (index == -1) return 0;
			return spawnAmount / ((FSAircraftCarrier)type).spawnUnit.get(index).maxSpawn;
		}

		public void update() {
			super.update();
			FSAircraftCarrier fstype = (FSAircraftCarrier)type;
			Seq<PowerUnitSeq> spawnUnit = fstype.spawnUnit;
			if (index != -1 && spawnAmount < spawnUnit.get(index).maxSpawn) {
				PowerUnitSeq pseq = spawnUnit.get(index);
				PowerUnit ptype = spawnUnit.get(index).type;
				spawnTimer += Time.delta * Vars.state.rules.unitBuildSpeedMultiplier * realSpeed();
				if (spawnTimer >= pseq.spawnTime && spawnAmount < pseq.maxSpawn) {
					float spawnX = fstype.spawnX, spawnY = fstype.spawnY;
					float xf = x + Angles.trnsx(rotation, spawnY, spawnX), yf = y + Angles.trnsy(rotation, spawnY, spawnX);
					Unit unit = ptype.create(team);
					unit.set(xf, yf);

					Tmp.v2.trns(rotation - 180f, 45f);
					fsUnitSpirit.at(xf + Tmp.v2.x, yf + Tmp.v2.y, 0, unit);
					Time.run(14f, () -> {
						unit.add();
					});

					spawnAmount++;
					spawnTimer = 0f;
					Events.fire(new UnitCreateEvent(unit));
				}
			}
		}

		public void changeType(int index) {
			this.index = index;
			FSAircraftCarrier fstype = (FSAircraftCarrier)type;
			Seq<PowerUnitSeq> spawnUnit = fstype.spawnUnit;
			Events.fire(new ACTypeChangeEvent(id));
			PowerUnitSeq useq = spawnUnit.get(index);
			spawnWave.at(x, y, 0f, Color.valueOf("44A9EB"), bounds());
			for (int i = 0; i < useq.maxSpawn; i++) {
			    int in = i;
				Time.run(in * 3f, () -> {
					float xf = x + Mathf.range(bounds()), yf = y + Mathf.range(bounds());
					Unit unit = useq.type.create(team);
					unit.set(xf, yf);
					fsunitSpawn.at(x, y, 0, unit);
					Time.run(in * 11f, () -> {
						Tmp.v2.trns(rotation - 180f, 45f);
						fsUnitSpirit.at(xf + Tmp.v2.x, yf + Tmp.v2.y, 0, unit);
						Time.run(in * 24f, () -> {
							unit.add();
						});
					});
				});
			}
			resetUnit();
		}


		@Override
		public int classId() {
			return classId;
		}

		public void unitRemoved() {
			spawnAmount--;
		}

		public void resetUnit() {
			spawnTimer = 0f;
			index = -1;
			spawnAmount = 0;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(index);
			write.f(spawnTimer);
			write.i(spawnAmount);
		}

		@Override
		public void read(Reads read) {
			super.read(read);
			index = read.i();
			spawnTimer = read.f();
			spawnAmount = read.i();
		}
	}

	public static class FSACEntity extends BasePowerEntityUnit {
		public boolean isFtoCommand = true;
		public FSAircraftCarrierEntity owner;
		public FSACEntity(FSAircraftCarrierEntity owner) {
			this.owner = owner;
			Events.on(ACTypeChangeEvent.class, e -> {
				if (e.id == owner.id) isFtoCommand = false;
			});
			Events.on(UnitDestroyEvent.class, e -> {
				if (e.unit.id == owner.id) killed();
			});
		}
		
		@Override
		public int classId() {
			return classId_2;
		}
		
		@Override
		public void remove() {
			if (isFtoCommand) owner.unitRemoved();
			super.remove();
		}

		@Override
		public boolean isAI() {
			return false;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			TypeIO.writeEntity(write, owner);
		}

		@Override
		public void read(Reads read) {
			super.read(read);
			owner = TypeIO.readEntity(read);
		}
	}

	public static class ACTypeChangeEvent {
		public final int id;

		public ACTypeChangeEvent(int id) {
			this.id = id;
		}
	}

	public static class FSACAIController extends FlyingAI {
		public int timerChange = 3;
		public float changeTime = 60f;
		
		@Override
        public void updateMovement() {
            if(target != null && command() == UnitCommand.attack){
                moveTo(target, unit.range() * 0.8f);
                unit.lookAt(target);
                
            } else {
                attack(120f);
            }
        }
        
		@Override
		protected void updateTargeting() {
			FSAircraftCarrierEntity entity = (FSAircraftCarrierEntity)unit;
			FSAircraftCarrier type = (FSAircraftCarrier)unit.type;
			boolean ret = retarget();
			Teamc lastTarget = null;
			if (ret) {
				lastTarget = findTarget(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
			}

			if (invalid(target)) {
				target = null;
			}

			if (target == null || lastTarget == target) return;
			target = lastTarget;

			if (timer.get(timerChange, changeTime)) {
				if (target instanceof Unit && ((Unit)target).isFlying()) {
					entity.changeType(type.getSuitableType(true));
				} else {
					entity.changeType(type.getSuitableType(false));
				}

			}
		}
	}

	public class PowerUnitSeq {
		public PowerUnit type;
		public int maxSpawn = 5;
		public float spawnTime = 25f;
	}
}