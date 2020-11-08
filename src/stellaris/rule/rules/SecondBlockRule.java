package stellaris.rule.rules;

import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import stellaris.rule.Rule;
import stellaris.rule.RuleEnum;
import stellaris.type.units.BaseSecondUnitType;

import static mindustry.game.EventType.*;

import arc.Events;
import arc.func.Cons;
import arc.math.geom.Rect;

public class SecondBlockRule extends Rule{
    public EntityGroup<Unit> group;
    
    @Override
    public void loadRule(){
        group = new EntityGroup<>(Unit.class, true, true);
        Rect rect = Groups.unit.tree().bounds;
        float
        x = rect.x,
        y = rect.y,
        w = rect.width,
        h = rect.height;
        group.resize(x, y, w, h);
        Events.on(UnitCreateEvent.class, u -> {
            Unit unit = u.unit;
            if(unit.type instanceof BaseSecondUnitType) group.add(unit);
        });
        Events.on(UnitDestroyEvent.class, e -> {
            Unit unit = e.unit;
            if(group.contains(u -> u == unit)) group.remove(unit);
        });
        super.loadRule();
    }
	
	@Override
	public void remote(){
	    group.clear();
	    group = null;
	}

	@Override
	public void add() {
		
	}

	@Override
	public void afterRead() {
		
	}

	@Override
	public <T> T as() {
		return null;
	}

	@Override
	public int classId() {
		return 0;
	}

	@Override
	public int id() {
		return 0;
	}

	@Override
	public void id(int arg0) {
		
	}

	@Override
	public boolean isAdded() {
		return false;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public void remove() {
		
	}

	@Override
	public <T extends Entityc> T self() {
		return null;
	}

	@Override
	public boolean serialize() {
		return false;
	}

	@Override
	public void update() {
		
	}

	@Override
	public <T> T with(Cons<T> arg0) {
		return null;
	}

	@Override
	public RuleEnum type() {
		return null;
	}
}