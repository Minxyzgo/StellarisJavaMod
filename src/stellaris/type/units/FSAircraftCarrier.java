package stellaris.type.units;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;

import java.util.*;

import mindustry.*;
import mindustry.ai.formations.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import minxyzgo.mlib.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.input.*;
import minxyzgo.mlib.type.*;
import stellaris.content.*;

public class FSAircraftCarrier extends PowerUnit implements Skillc {
	private static int classId = Tool.nextClassId(FSAircraftCarrierEntity::new);
	private static int classId_2 = Tool.nextClassId(FSACEntity::new);
	public static Effect
	fsunitSpawn = new Effect(55f, e -> {
		Unit data = (Unit)e.data();
		Draw.alpha(e.fin() * Mathf.absin(Time.time, 4f, 1.5f));
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
	public DataSkill[] skills;
	public float spawnX = 12f, spawnY = 32f;
	
	public static SpawnerBulletType spawner = new SpawnerBulletType() {{
	    shootEffect = smokeEffect = trailEffect = Fx.none;
	    lightColor = Color.valueOf("44A9EB");
	    despawnEffect = Fx.none;
	}};

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
		trailLength = 120;
		trailX = 25f;
		trailY = -64f;
		trailScl = 5.5f;
		faceTarget = true;
		rotateShooting = true;
		drawShields = false;
		constructor = FSAircraftCarrierEntity::new;
		defaultController = FSACAIController::new;
		engineSize = 8f;
		
		
		spawnUnit.add(new PowerUnitSeq() {
			{
				type = (PowerUnit) AsUnits.fship;
				maxSpawn = 1;
				spawnTime = 200f;
				skillRegion = "AcType-0";
			}
		});
		spawnUnit.add(new PowerUnitSeq() {
			{
				type = (PowerUnit) AsUnits.fhz;
				maxSpawn = 5;
				spawnTime = 100f;
				skillRegion = "AcType-1";
			}
		});
		
		powerWeapons.add(new PowerWeapon() {{
		    typeId = "spawner";
		    xRand = 360f;
		    shootCone = 360f;
		    shootSound = Sounds.none;
		    top = false;
		    x = y = 0f;
		    rotate = true;
		    mirror = false;
		    reload = Float.MAX_VALUE - 1f;
		    consumePower = 1000f;
		    bullet = spawner;
		}});
		
		weaponacts.put("spawner", (mount, weapon, unit) -> {
			mount.reload = weapon.reload;
			return false;
		});
	}
	
	@Override
	public void init() {
	    super.init();
	    skills = new DataSkill[spawnUnit.size];
	    for(int i = 0; i < spawnUnit.size; i++) {
	        PowerUnitSeq pseq = spawnUnit.get(i);
	        skills[i] = SpawnerSkill.create(pseq.spawnTime, pseq.skillRegion, i);
	    }
	}
	
	@Override
	public DataSkill[] getSkill() {
		return skills;
	}
	
	@Override
	public boolean hasWeapons(){
        return true;
    }
    
    @Override
    public void drawWeapons(Unit unit) {
        Draw.draw(Draw.z(), () -> {
            Color c = Color.valueOf("8ADAFF");
            c.lerp(Color.white, Mathf.clamp(Time.delta * 0.04f));
            Shaders.build.region = Core.atlas.find(name + "-heat");
            Shaders.build.progress = 1f;
            Shaders.build.color.set(c);
            Shaders.build.color.a = Mathf.absin(Time.time, 3f, 0.3f);
            Shaders.build.time = -Time.time / 20f;

            Draw.shader(Shaders.build);
            Draw.rect(Core.atlas.find(name + "-heat"), unit.x, unit.y, unit.rotation - 90f);
            Draw.shader();

            Draw.reset();
        });
    }

	@Override
	public void tableBar(Unit unit, Table bars) {
		FSAircraftCarrierEntity fsUnit = (FSAircraftCarrierEntity)unit;
		bars.add(new Bar("build", Pal.accent, fsUnit::unitBuildc));
		bars.row();
		bars.add(new Bar("amount", Color.valueOf("44A9EB"), fsUnit::unitAmountc));
	}

	public int getSuitableType(boolean isFlying) {
		if (isFlying) return 0;
		return 1;
	}

	public static class FSAircraftCarrierEntity extends BasePowerEntityUnit {
	 //   private transient Trail[] trails = new Trail[6];
	 //  private transient Color trailColor = Color.valueOf("44A9EB").cpy().mul(1.2f);
	    private transient volatile boolean changing = false;
		public int index = 1;
		public ObjectSet<Unit> spawnUnits = new ObjectSet<>();
		public static float spawnTimer = 0;
		
		public int spawnAmount() {
		    return spawnUnits.size;
		}

		public float unitBuildc() {
			//if (index == -1) return 0;
			return spawnTimer / ((FSAircraftCarrier)type).spawnUnit.get(index).spawnTime;
		}

		public float unitAmountc() {
			//if (index == -1) return 0;
			float a = spawnAmount() / ((FSAircraftCarrier)type).spawnUnit.get(index).maxSpawn;
			Vars.ui.showInfoToast("amount: " + a, Time.delta);
			return a;
			
		}
		
		@Override
		public void update() {
			super.update();
			FSAircraftCarrier fstype = (FSAircraftCarrier)type;
			Seq<PowerUnitSeq> spawnUnit = fstype.spawnUnit;
		//	Vars.ui.showInfoToast("amount: " + spawnAmount() + " timer: " + spawnTimer, Time.delta);
			PowerUnitSeq pseq = spawnUnit.get(index);
			PowerUnit ptype = spawnUnit.get(index).type;
			spawnUnits.each(u -> {
			    if(u.dead) spawnUnits.remove(u);
			});
			/*
			for(int i = 0; i < trails.length; i++){
                Trail t = trails[i];
                int sign = i % 2 == 0 ? -1 : 1;
                float scale = elevation();
			    float offset = 72f / 2f + 72f / 2f * scale;
                float cx = Angles.trnsx(rotation + 180, offset * sign * i) + x, cy = Angles.trnsy(rotation + 180, offset * sign * i) + y;
                t.update(cx, cy);
            }
			*/
			if (spawnAmount() < pseq.maxSpawn) {
				spawnTimer = Math.min(spawnTimer + Time.delta * Vars.state.rules.unitBuildSpeedMultiplier, pseq.spawnTime);
				if (spawnTimer >= pseq.spawnTime && (ptype instanceof FSACUnitType || Units.canCreate(team, ptype))) {
					float spawnX = fstype.spawnX, spawnY = fstype.spawnY;
					float xf = x + Angles.trnsx(rotation, spawnY, spawnX), yf = y + Angles.trnsy(rotation, spawnY, spawnX);
					spawner.create(this, team, xf, yf, rotation, 120f, 1f, 1f, ptype);
					spawnTimer = 0f;
				}
			}
		}
		
		
		public boolean canBuild() {
		    return spawnAmount() < ((FSAircraftCarrier)type).spawnUnit.get(index).maxSpawn;
		}
		/*
		@Override
		public void draw() {
		    for(Trail t : trails) {
		        t.draw(trailColor, (type.engineSize + Mathf.absin(Time.time, 2f, type.engineSize / 4f) * elevation) * type.trailScl);
		    }
		    super.draw();
		}*/
		
		@Override
		public void commandNearby(FormationPattern pattern, Boolf<Unit> include) {
		    Formation formation = new Formation(new Vec3(x, y, rotation), pattern);
            formation.slotAssignmentStrategy = new DistanceAssignmentStrategy(pattern);

            units.clear();

            Units.nearby(team, x, y, 150f, u -> {
                if(u.isAI() && include.get(u) && u != self() && u.type.flying == type.flying && u.hitSize <= hitSize * 1.1f){
                    units.add(u);
                }
            });
            //sort by hitbox size, then by distance
            for(Unit u2 : spawnUnits) {
                if(!units.contains(u2)) units.add(u2);
            }
            
            if(units.isEmpty()) return;

            units.sort(Structs.comps(Structs.comparingFloat(u -> -u.hitSize), Structs.comparingFloat(u -> u.dst2(this))));
            units.truncate(type.commandLimit);

            command(formation, units);
		}
		
		public synchronized void changeType(int index) {
			this.index = index;
			resetUnit();
			FSAircraftCarrier fstype = (FSAircraftCarrier)type;
			Seq<PowerUnitSeq> spawnUnit = fstype.spawnUnit;
			PowerUnitSeq useq = spawnUnit.get(index);
			//spawnWave.at(x, y, 0f, Color.valueOf("44A9EB"), bounds());
			boolean check = changing;
			int i = 0;
			boolean canCreate = true;
			do {
			    canCreate = i < useq.maxSpawn && (useq.type instanceof FSACUnitType || Units.canCreate(team, useq.type));
			    if(canCreate) {
			        changing = true;
		        	Time.run(i * 10f, () -> {
			         	float xf = x + Mathf.range(bounds()), yf = y + Mathf.range(bounds());
			    	    spawner.create(this, team, xf, yf, rotation, 120f, 1f, 1f, useq.type);
		     	    });
			    } else {
			        Time.run(i * 10f, () -> {
			            changing = false;
			        });
			    }
			    
		    	i++;
			} while(!check && canCreate);
		}

		@Override
		public void add() {
			super.add();
			changeType(index);
			/*Arrays.fill(trails, new Trail(type.trailLength));
			
			for(Trail t : trails) {
		        t.clear();
		    }*/
		}


		@Override
		public int classId() {
			return classId;
		}

		public void resetUnit() {
			spawnTimer = 0f;
			for(Unit u : spawnUnits) {
			    if(u instanceof FSACEntity) {
			        ((FSACEntity)u).isFtoCommand = false;
			    } else {
			        Fx.unitDespawn.at(u.x, u.y, 0, u);
			        u.remove();
			    }
			}
			
			spawnUnits.clear();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(index);
			write.f(spawnTimer);
			write.i(spawnAmount());
			
			for(Unit u : spawnUnits) {
			    TypeIO.writeEntity(write, u);
			}
		}

		@Override
		public void read(Reads read) {
			super.read(read);
			index = read.i();
			spawnTimer = read.f();
			for(int i = 0; i < read.i(); i++) {
			    spawnUnits.add(TypeIO.readEntity(read));
			}
		}
	}

	public static class FSACEntity extends BasePowerEntityUnit {
		public boolean isFtoCommand = true;
		public FSAircraftCarrierEntity owner;
		/*{
			Events.on(ACTypeChangeEvent.class, e -> {
				if (e.id == owner.id) isFtoCommand = false;
			});
			Events.on(UnitDestroyEvent.class, e -> {
				if (e.unit.id == owner.id) killed();
			});
		}*/

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

	public static class SpawnerBulletType extends BulletType {
	    
	    {
	        splashDamage = 250f;
	        splashDamageRadius = 60f;
	        hitSize = 4;
	        drawSize = 60f;
	        drag = 0.02f;
	        speed = 5f;
	        lifetime = 70f;
	        hittable = false;
            absorbable = false;
	        collidesTiles = false;
            collides = false;
            collidesAir = false;
            scaleVelocity = true;
	    }
	    
	    @Override
	    public void init(Bullet b) {
	        super.init(b);
	        if(b.data instanceof UnitType) {
	            FSAircraftCarrierEntity owner = (FSAircraftCarrierEntity)b.owner;
	            float xf = b.x + Angles.trnsx(b.rotation(), b.vel.x*lifetime*0.75f), yf = b.y + Angles.trnsy(b.rotation(), b.vel.y*lifetime*0.75f);
	            Unit unit = ((UnitType)b.data).create(b.team);
				unit.set(xf, yf);
				unit.rotation(b.rotation());
				Fx.unitDespawn.at(xf, yf, 0, unit);
				owner.spawnUnits.add(unit);
				if(unit instanceof FSACEntity) {
				   ((FSACEntity)unit).owner = (FSAircraftCarrierEntity)b.owner;
				}
				
				b.data = unit;
	        }
	    }
	    
	    @Override
	    public void update(Bullet b) {
	        super.update(b);
	        Unit to = (Unit)b.data();
	        if(to == null) return;
	        int scl = (int)(Math.sqrt(to.hitSize()) + b.fout() * 2f);
	        if (Mathf.chance(Time.delta * 1.2)) {
                for (int i = 0; i < Mathf.random(4); i++) {
                    Lightning.create(b.team, Color.valueOf("44A9EB"), lightningDamage < 0 ? 25f : lightningDamage, b.x, b.y, Mathf.random(360f), scl);
                }
            }
	    }
	    
	    @Override
	    public void despawned(Bullet b) {
	        super.despawned(b);
	        fsunitSpawn.at(b.x, b.y, 0, b.data());
	        ((Unit)b.data()).add();
	    }
	    
		@Override
		public void draw(Bullet b) {
			Unit to = (Unit)b.data();
			if(to == null) return;
			Draw.color(Color.valueOf("44A9EB"), Color.white, b.fin());
			float scl = 1f + b.fout() * 2f;
			float x = b.x, y = b.y;
			float size = 2.5f * b.fin();
			TextureRegion region = to.type.icon(Cicon.full);
			Draw.alpha(b.fin() * Mathf.absin(Time.time, 4f, 1.5f));
			Draw.rect(region, x, y,
					  region.width * Draw.scl * scl, region.height * Draw.scl * scl, to.rotation() - 90f);
			Lines.stroke(size);
			Lines.line(x, y, to.x, to.y, false);
			Drawf.tri(to.x, to.y, Lines.getStroke() * 1.25f, size * 1.875f + size / 1.25f, to.rotation() - 90f);
			Fill.circle(x, y, 15 * 0.75f * b.fout());
			Lines.circle(x, y, b.fin() * 60f);
			Lines.stroke(b.fin() * 15f);
			Angles.randLenVectors(b.id, 2, 1 + 40f * b.fin(), b.rotation(), 360f, (x2, y2) -> {
				Lines.lineAngle(x + x2, y + y2, Mathf.angle(x2, y2), b.fslope() * 12f + 1);
			});
			Draw.reset();
		}
	}
	
	public static class FSACUnitType extends PowerUnit {
	    
	    {
	        constructor = FSACEntity::new;
	    }
	    
	    public FSACUnitType(String name) {
		    super(name);
	    }
	}

	public static class FSACAIController extends FlyingAI {
		public int timerChange = 3;
		public float changeTime = 60f;

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
			boolean check_2 = lastTarget == null || lastTarget == target;
			/*
			if(check) {
			    Vars.ui.showInfoToast("timer: true", 50f);
			} else {
			    Vars.ui.showInfoToast("check targrt: " + check_2, Time.delta);
			}
			*/

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
	
	public static class SpawnerSkill extends SkillButtonStack.SkillButton {
	    
	    private int index;
	    
	    {
			disabledBoolp = () -> !(((FSAircraftCarrierEntity)Vars.player.unit()).canBuild());
			changed(() -> {
				sendSkill(index);
			});
	    }
	    
	    public SpawnerSkill(SkillButtonStack owner, TextureRegion region, ImageButton.ImageButtonStyle imageStyle, int index) {
            super(owner, region, imageStyle);
            this.index = index;
        }
        
        public static SpawnerSkill create(float cooldown, String region, int index) {
            SkillButtonStack stack = new SkillButtonStack(Core.atlas.find(Vars.content.transformName(region)), cooldown) {{
			    button = new SpawnerSkill(this, region, new SkillButtonStack.SkillStyle(), index);
			}};
			
            return (SpawnerSkill)stack.button;
        }
        
        @Override
		public void callSkill(Player pl, Object... objects) {
			((FSAircraftCarrierEntity)pl.unit()).changeType((Integer)objects[0]);
		}
        
	    @Override
		public String getType() {
			return "Spawner$" + id;
		}
		
	}

	public class PowerUnitSeq {
		public PowerUnit type;
		public int maxSpawn = 5;
		public float spawnTime = 25f;
		public String skillRegion = "";
	}
}