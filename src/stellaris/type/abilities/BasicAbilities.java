package stellaris.type.abilities;

import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import stellaris.type.intf.Powerc;

public class BasicAbilities {

	public static class PowerAbility extends Ability {
		public PowerAbility(float maxPower, float powerProduction, float consumePower) {
			this.maxPower = maxPower;
			this.powerProduction = powerProduction;
			this.consumePower = consumePower;
		}
		public Seq<ConPowerWeapon> conWeapons = new Seq<>();
		public ForceFieldAbility sability;
		public float conShieldPower;

		public float maxPower = 100f;
		public float powerProduction = 1f;
		public float consumePower = 0f;

		public void addConWeapon(Weapon weapon, float consumePower) {
			conWeapons.add(new ConPowerWeapon(weapon, consumePower));
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
			if (conWeapons.size > 0) {
				conWeapons.each(cw -> {
					for(WeaponMount mount : unit.mounts) {
					    if(mount.weapon == cw.weapon) {
					        boolean conWe = c.conPower(cw.consumePower);
					        if(mount.reload == cw.weapon.reload && conWe) {
					            c.status(Math.max(c.status() - cw.consumePower * Time.delta, 0f));
					        } else if(!conWe) mount.reload = cw .weapon .reload;
					        
					    }
					}
				});
			}
			powerAct(unit);
		}

		public boolean con(Unit unit) {
			Powerc iunit = (Powerc)unit;
			return consumePower > 0f && iunit.conPower(consumePower);
		}

		public void powerAct(Unit c) {

		}
	}

	public static class PowerForceAbility extends PowerAbility {
		public PowerForceAbility(float maxPower, float powerProduction, float consumePower) {
			super(maxPower, powerProduction, consumePower);
		}

		public float maxShield = 150000f;

		@Override
		public boolean con(Unit unit) {
			return super.con(unit) && unit.shield < maxShield;
		}

		@Override
		public void powerAct(Unit unit) {
			Powerc p = (Powerc)unit;
			if (!p.conPower(consumePower)) unit.shield = -maxShield * 0.75f;
		}
	}

	public static class ConPowerWeapon {
		public Weapon weapon;
		public float consumePower;
		ConPowerWeapon(Weapon weapon, float consumePower) {
			this.weapon = weapon;
			this.consumePower = consumePower;
		}
	}
}