package stellaris.type.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.JsonIO;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import mindustry.world.meta.values.*;
import minxyzgo.mlib.Tool;

import static mindustry.Vars.*;

import stellaris.type.abilities.BasicAbilities.PowerAbility;
import stellaris.type.intf.Powerc;

public abstract class PowerUnit extends UnitType {
	private static int classId = Tool.nextClassId(BasePowerMechUnit::new);
	private static int classId_2 = Tool.nextClassId(BasePowerEntityUnit::new);

	public @Nullable PowerAbility Pability;
	public ObjectMap<String, MountWeaponAct> weaponacts = new ObjectMap<>();
	public Seq<PowerWeapon> powerWeapons = new Seq<>();
	/* for Json */

	public boolean outputPower = true;
	public ForceFieldAbility sability;
	public float conShieldPower;
	public float maxPower = 100f;
	public float powerProduction = 1f;
	public float consumePower = 0f;

	public PowerUnit(String name) {
		super(name);
		constructor = BasePowerEntityUnit::new;
	}

	@Override
	public void update(Unit unit) {
		if (!(unit instanceof Powerc)) return;

		Powerc c = (Powerc)unit;
		if (c.powerc() <= 0.99f) {
			c.status(Math.min(c.status() + powerProduction * Time.delta, c.maxPower()));
		}

		if (con(unit)) {
			if (c.conPower(consumePower)) c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
		}

		if (sability != null) {
			if (!c.conPower(consumePower)) unit.shield = -sability.max * 0.75f;

			if (unit.shield < sability.max) {
				if (c.conPower(conShieldPower)) c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
			}
		}
		if (weapons.size > 0) {

			for (WeaponMount mount : unit.mounts) {
			    PowerWeapon cw = (PowerWeapon)mount.weapon;
				boolean conWe = c.conPower(cw.consumePower);
				if (mount.reload == cw.reload && conWe) {
				    MountWeaponAct ac = null;
				    if(!cw.typeId.equals("")) ac = weaponacts.get(cw.typeId);
				    boolean check = true;
				    if(ac != null) {
				        check = ac.get(mount, cw, unit);
				    }
				    
					if(check) c.status(Math.max(c.status() - cw.consumePower * Time.delta, 0f));
				} else if (!conWe) {
				    mount.reload = cw .reload;
				    unConPower(cw, mount, unit);
				}
			}
		}
	}

	public boolean con(Unit unit) {
		Powerc iunit = (Powerc)unit;
		return consumePower > 0f && iunit.conPower(consumePower);
	}
	
	public void unConPower(PowerWeapon weapon, WeaponMount mount, Unit unit) {
	    
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
			abilities.each(a -> {
				if (a instanceof ForceFieldAbility) {

					bars.add(new Bar("shield", unit.team.color, () -> unit.shield / ((ForceFieldAbility)a).max).blink(Color.white));
					bars.row();
				}
			});

			bars.row();
			if (state.rules.unitAmmo) {
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
	
	@Override
	public void init() {
	    for(PowerWeapon w : powerWeapons) {
	        weapons.add((Weapon)w);
	    }
	    
	    if(sability != null && !abilities.contains(sability)) abilities.add(sability);
	    super.init();
	}
	
	@Override
	public void setStats() {
		Unit inst = constructor.get();

		stats.add(Stat.health, health);
		stats.add(Stat.speed, speed);
		stats.add(Stat.itemCapacity, itemCapacity);
		stats.add(Stat.range, (int)(maxRange / tilesize), StatUnit.blocks);
		stats.add(Stat.commandLimit, commandLimit);

		if (abilities.any()) {
			var unique = new ObjectSet<String>();

			for (Ability a : abilities) {
				if (unique.add(a.localized())) {
					stats.add(Stat.abilities, a.localized());
				}
			}
		}

		stats.add(Stat.flying, flying);

		if (!flying) {
			stats.add(Stat.canBoost, canBoost);
		}

		if (mineTier >= 1) {
			stats.addPercent(Stat.mineSpeed, mineSpeed);
			stats.add(Stat.mineTier, new BlockFilterValue(b -> {
				if (b instanceof Floor) {
					Floor f = (Floor)b;
					return f != null && f.itemDrop != null && f.itemDrop.hardness <= mineTier && !f.playerUnmineable;
				}

				return false;
			}));
		}
		if (buildSpeed > 0) {
			stats.addPercent(Stat.buildSpeed, buildSpeed);
		}
		if (inst instanceof Payloadc) {
			stats.add(Stat.payloadCapacity, (payloadCapacity / (tilesize * tilesize)), StatUnit.blocksSquared);
		}

		if (weapons.any()) {
			stats.add(Stat.weapons, new PowerWeaponListValue(this, powerWeapons));
		}
	}

	public static class PowerWeaponListValue implements StatValue {
		private final Seq<PowerWeapon> weapons;
		private final UnitType unit;

		public PowerWeaponListValue(UnitType unit, Seq<PowerWeapon> weapons) {
			this.weapons = weapons;
			this.unit = unit;
		}

		@Override
		public void display(Table table) {
			table.row();
			for (int i = 0; i < weapons.size; i ++) {
				PowerWeapon weapon = weapons.get(i);

				if (weapon.flipSprite) {
					//flipped weapons are not given stats
					continue;
				}

				TextureRegion region = !weapon.name.equals("") && weapon.outlineRegion.found() ? weapon.outlineRegion : unit.icon(Cicon.full);

				table.image(region).size(60).scaling(Scaling.bounded).right().top();

				table.table(Tex.underline,  w -> {
					w.left().defaults().padRight(3).left();

					if (weapon.inaccuracy > 0) {
						sep(w, "[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int)weapon.inaccuracy + " " + StatUnit.degrees.localized());
					}

					if (weapon.consumePower != 0) {
						sep(w, "[lightgray]" + Stat.powerUse.localized() + ": " + weapon.consumePower);
					}

					sep(w, "[lightgray]" + Stat.reload.localized() + ": " + (weapon.mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / weapon.reload * weapon.shots, 1));

					var bullet = new AmmoListValue<UnitType>(OrderedMap.of(unit, weapon.bullet));
					bullet.display(w);
				}).padTop(-9).left();
				table.row();
			}
		}

		void sep(Table table, String text) {
			table.row();
			table.add(text);
		}
	}

	public static class BasePowerMechUnit extends MechUnit implements Powerc {

		public float power;
		
		@Override
		public void add() {
		    super.add();
		    power = ((PowerUnit)type).maxPower;
		}
		
		@Override
		public int classId() {
			return classId;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(power);

		}

		@Override
		public void read(Reads read) {
			super.read(read);
			power = read.f();

		}

		@Override
		public float powerc() {
			return power / maxPower();
		}

		@Override
		public float maxPower() {
			return ((PowerUnit)type).maxPower;
		}

		@Override
		public float status() {
			return power;
		}

		@Override
		public void status(float value) {
			power = value;
		}

		@Override
		public boolean conPower(float value) {
			return power >= (value * Time.toSeconds);
		}
	}
	
	public static class BasePowerEntityUnit extends UnitEntity implements Powerc {

		public float power;
		
		@Override
		public void add() {
		    super.add();
		    power = ((PowerUnit)type).maxPower;
		}

		@Override
		public int classId() {
			return classId_2;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(power);

		}

		@Override
		public void read(Reads read) {
			super.read(read);
			power = read.f();

		}

		@Override
		public float powerc() {
			return power / maxPower();
		}

		@Override
		public float maxPower() {
			return ((PowerUnit)type).maxPower;
		}

		@Override
		public float status() {
			return power;
		}

		@Override
		public void status(float value) {
			power = value;
		}

		@Override
		public boolean conPower(float value) {
			return power >= (value * Time.toSeconds);
		}
	}

	public interface MountWeaponAct {
		boolean get(WeaponMount mount, Weapon weapon, Unit unit);
	}

	public static class PowerWeapon extends Weapon {
		public float consumePower;
		public String typeId = "";

		public PowerWeapon() {
			super();
		}

		public PowerWeapon(String name) {
			super(name);
		}
		
		@Override
		public PowerWeapon copy() {
		   PowerWeapon out = new PowerWeapon();
            JsonIO.json().copyFields(this, out);
            return out;
		}
	}
}