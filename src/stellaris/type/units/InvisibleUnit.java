package stellaris.type.units;


import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.UnitEntity;
import stellaris.type.abilities.BasicAbilities.PowerAbility;
import stellaris.type.intf.Powerc;

public class InvisibleUnit extends UnitEntity implements Powerc {
    public float visDuction;
    public float power;
	public boolean isVisible;
	private float maxPower;

	@Override
	public void hitbox(Rect rect) {
		if (isVisible) {
			rect.setCentered(0, 0, 0, 0);
		}else{
	    	super.hitbox(rect);
		}
	}

	@Override
	public void write(Writes write) {
		super.write(write);
		write.f(power);
		write.bool(isVisible);
	}

	@Override
	public void read(Reads read) {
		super.read(read);
		power = read.f();
		isVisible = read.bool();
	}

	@Override
	public float powerc() {
		return power / maxPower();
	}

	@Override
	public float maxPower() {
		abilities().forEach(a -> {
		    if(a instanceof PowerAbility) {
		        PowerAbility ab = (PowerAbility)a;
		        if(maxPower < ab.maxPower) maxPower = ab.maxPower;
		    }
		});
		return maxPower;
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