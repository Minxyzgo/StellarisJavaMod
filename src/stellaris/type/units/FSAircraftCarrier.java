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
import mindustry.core.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
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
	private static int classId_2 = Tool.nextClassId(FSACEntity::new);
	public static Effect fsUnitSpirit = new Effect(50f, e -> {
		Unit to = (Unit)e.data();
		Draw.color(Color.valueOf("44A9EB"), Color.white, e.fin());
		Tmp.v1.set(e.x, e.y).interpolate(Tmp.v2.set(to), e.fin() * 0.875f, Interp.pow2In).add(Tmp.v2.sub(e.x, e.y).nor().rotate90(1).scl(Mathf.randomSeedRange(e.id, 1f) * e.fslope() * 10f));
		float x = Tmp.v1.x, y = Tmp.v1.y;
		float size = 2.5f * e.fin();
		TextureRegion region = to.type.icon(Cicon.full);
		Draw.alpha(e.fin() * Mathf.absin(Time.time, 4f, 1.5f));
		Draw.rect(region, e.x, e.y,
				  region.width * Draw.scl * scl, region.height * Draw.scl * scl, to.rotation() - 90f);
		Lines.stroke(size);
		Lines.line(x, y, to.x, to.y, false);
		Drawf.tri(to.x, to.y, Lines.getStroke() * 1.25f, size * 1.875f + size / 1.25f, to.rotation() - 90f);
		Fill.circle(x, y, 15 * 0.75f * e.fout());
		Lines.circle(x, y, e.fin() * 60f);
		Lines.stroke(e.fin() * 15f);
		Angles.randLenVectors(e.id, 2, 1 + 40f * e.fin(), e.rotation, 360f, (x2, y2) -> {
			Lines.lineAngle(x + x2, y + y2, Mathf.angle(x2, y2), e.fslope() * 12f + 1);
		});
	}),
	fsunitSpawn = new Effect(55f, e -> {
		Unit data = (Unit)e.data();
		Draw.alpha(e.fin() * Mathf.absin(Time.time, 4f, 1.5f));
		float scl = 1f + e.fout() * 2f;
		Draw.color(Color.valueOf("44A9EB"), Color.white, e.fin());
		Lines.stroke(data.hitSize() * 0.125f * e.fout());
		Lines.circle(e.x, e.y, e.fin() * 52);
		for (int i : Mathf.signs) {
			Drawf.tri(e.x, e.y, data.hitSize() * 0.125f * e.fout(), 11f, e.rotation + 360f * i);
		}
		Angles.randLenVectors(e.id, 45, 65 * e.fin(), e.rotation, 360f, (x, y) -> {
			Lines.stroke(e.fout() * 2);
			Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 8 + 1);
		});
	});
	/*spawnWave = new Effect(34, e -> {
		float size = (Float)e.data;
		Draw.color(e.color);
		Lines.stroke(size + e.fout() * 2f);
		Lines.circle(e.x, e.y, e.finpow() * e.rotation);
	});*/
	public Seq<PowerUnitSeq> spawnUnit = new Seq<>();
	public float spawnX, spawnY;

	public FSAircraftCarrier(String name) {
		super(name);
	}

	{
	    lowAltitude = true;
	    range = 200f;
	    maxRange = 400f;
		health = 95000;
		flying = true;
		speed = 0.11f;
		drag = 0.02f;
		hitSize = 145f;
		accel = 0.08f;
		rotateSpeed = 0.14f;
		armor = 45f;
		destructibleWreck = false;
		targetFlag = BlockFlag.core;
		maxPower = 10000f;
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
		public int index = 1;
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
				spawnTimer += Time.delta * Vars.state.rules.unitBuildSpeedMultiplier;
				if (spawnTimer >= pseq.spawnTime && spawnAmount < pseq.maxSpawn) {
					float spawnX = fstype.spawnX, spawnY = fstype.spawnY;
					float xf = x + Angles.trnsx(rotation, spawnY, spawnX), yf = y + Angles.trnsy(rotation, spawnY, spawnX);
					Unit unit = ptype.create(team);
					unit.set(xf, yf);

					Tmp.v2.trns(rotation - 180f, 45f);
					fsUnitSpirit.at(xf + Tmp.v2.x, yf + Tmp.v2.y, 0, unit);
					Time.run(45f, () -> {
						unit.add();
					});

					spawnAmount++;
					spawnTimer = 0f;
				}
			}
		}

		public void changeType(int index) {
			this.index = index;
			FSAircraftCarrier fstype = (FSAircraftCarrier)type;
			Seq<PowerUnitSeq> spawnUnit = fstype.spawnUnit;
			Events.fire(new ACTypeChangeEvent(id));
			PowerUnitSeq useq = spawnUnit.get(index);
			//spawnWave.at(x, y, 0f, Color.valueOf("44A9EB"), bounds());
			for (int i = 0; i < useq.maxSpawn; i++) {
			    int in = i;
				Time.run(in * 3f, () -> {
					float xf = x + Mathf.range(bounds()), yf = y + Mathf.range(bounds());
					Unit unit = useq.type.create(team);
					unit.set(xf, yf);
					fsunitSpawn.at(x, y, 0, unit);
					Fx.unitDespawn.at(xf, yf, 0, unit);
					Time.run(in * 11f, () -> {
						Tmp.v2.trns(rotation - 180f, 145f);
						fsUnitSpirit.at(xf + Tmp.v2.x, yf + Tmp.v2.y, 0, unit);
						Time.run(in * 45f, () -> {
							unit.add();
						});
					});
				});
				spawnAmount++;
			}
			resetUnit();
		}
		
		@Override
		public void add() {
		    super.add();
		    changeType(index);
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
		public void add() {
            if (added) {
                return;
            }
            Groups.all.add(this);
            Groups.unit.add(this);
            Groups.sync.add(this);
            Groups.draw.add(this);
            updateLastPosition();
            added = true;
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
			
			boolean check = timer.get(timerChange, changeTime);
			boolean check_2 = target == null || lastTarget == target;
			if(check) {
			    Vars.ui.showInfoToast("timer: true", 50f);
			} else {
			    Vars.ui.showInfoToast("check targrt: " + check_2, Time.delta);
			}

			if (check_2) return;
			target = lastTarget;

			if (check) {
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