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
        });
}