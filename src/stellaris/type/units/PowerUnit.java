package stellaris.type.units;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import stellaris.type.intf.Powerc;

public abstract class PowerUnit extends UnitType{
	public float maxPower = 100f;
	public float powerProduction = 1f;
	public float forceConsumePower = 2f;
	public float maxShield = 150000f;
	
	public PowerUnit(String name){
	    super(name);
	    
	}
	
	@Override
	public void display(Unit unit, Table table){
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
            if(unit instanceof Powerc) {
                Powerc pu = (Powerc)unit;
                bars.add(new Bar("power", Pal.powerBar, pu::powerc));
            }
            bars.row();
            if(abilities.contains(a -> a instanceof ForceFieldAbility)) bars.add(new Bar("shield", unit.team.color, () -> unit.shield / maxShield).blink(Color.white));
            bars.row();
            if(Vars.state.rules.unitAmmo){
                bars.add(new Bar(ammoType.icon + " " + Core.bundle.get("stat.ammo"), ammoType.barColor, () -> unit.ammo / ammoCapacity));
                bars.row();
            }
        }).growX();

        if(unit.controller() instanceof LogicAI){
            table.row();
            table.add(Blocks.microProcessor.emoji() + " " + Core.bundle.get("units.processorcontrol")).growX().left();
            table.row();
            table.label(() -> Iconc.settings + " " + (long)unit.flag + "").color(Color.lightGray).growX().wrap().left();
        }
        
        table.row();
	}
	
	
	
	
	
}