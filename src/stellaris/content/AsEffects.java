package stellaris.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;

import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class AsEffects {
	public final static Effect
	energyMedium = new Effect(15, e -> {
		randLenVectors(e.id, 5, 3f + e.fin() * 8f, (x, y) -> {
			color(Color.valueOf("#74DFC7"));
			Fill.square(e.x + x, e.y + y, e.fout() * 1f + 0.75f, 45);
		});
	}),
	purpledst = new Effect(25, e -> {
		Lines.stroke(e.fout() * 3);
		Angles.randLenVectors(e.id, 1, 1 + 0 * e.fin(), e.rotation, 0, (x, y) -> {
			Draw.color(Color.valueOf("#e4ebff"), Color.valueOf("#7b68ee"), e.fin());
			Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 5);
		});
	});
}