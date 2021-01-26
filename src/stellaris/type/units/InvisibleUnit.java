package stellaris.type.units;


import minxyzgo.mlib.Tool;

import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import stellaris.type.intf.Powerc;
import stellaris.type.units.PowerUnit.BasePowerEntityUnit;

public class InvisibleUnit extends BasePowerEntityUnit implements Powerc {
    private static int classId = Tool.nextClassId(InvisibleUnit::new);
    public float visDuction;
	public boolean isVisible;

	@Override
	public void hitbox(Rect rect) {
		if (isVisible) {
			rect.setCentered(0, 0, 0, 0);
		}else{
	    	super.hitbox(rect);
		}
	}
	
	@Override
	public int classId() {
	    return classId;
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
}