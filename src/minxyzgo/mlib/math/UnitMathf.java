package minxyzgo.mlib.math;

import arc.math.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;

public class UnitMathf {
	public static float[] getShootXY(Unit unit, WeaponMount mount) {
		Weapon weapon = mount.weapon;
		float rotation = unit.rotation - 90;
		float weaponRotation  = rotation + (weapon.rotate ? mount.rotation : 0);
		float recoil = -((mount.reload) / weapon.reload * weapon.recoil);
		float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0, recoil),
			  wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0, recoil);
		float shootX = wx + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY),
			  shootY = wy + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
		return new float[] {shootX, shootY, wx, wy, weaponRotation};
	}
}