package stellaris.archeology;

import arc.*;
import arc.files.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Jval.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import minxyzgo.mlib.*;
import stellaris.*;
import stellaris.archeology.ArcheologyEvent.*;

import java.lang.reflect.*;

import static arc.util.Log.*;
import static stellaris.archeology.ArcheologyEvent.*;

@SuppressWarnings("unchecked")
public class Archeology {
	public static final int max = 10;
	public ArcheologyData[] map = new ArcheologyData[256];
	public final Seq<ArcheologyEvent>[] events = new Seq[ArcheologyType.values().length];

	private boolean loaded = false;
	private BaseDialog dialog;
//	private Table infoTable, toolTable, imageTable;
//	private TextButton conButton;

	{
		Time.mark();
		loadEvent();
		info("load archeology. time: @", Time.elapsed());
		Tool.onLoad(() -> {
			for (Seq<ArcheologyEvent> seq : events) {
				seq.each(ArcheologyEvent::init);
				seq.each(this::reflectEvent);
			}
		});
		Events.on(EventType.ResetEvent.class, e -> {
			map = new ArcheologyData[256];
		});
	}

	public void reflectEvent(ArcheologyEvent event) {
		info(event.name + " " + event.type.localized());
		try {
			for (Field field : ArcheologyEvent.class.getFields()) {
				if (!field.isAnnotationPresent(TypeChecks.class)) {
					continue;
				}
				TypeChecks checkers = field.getAnnotation(TypeChecks.class);

				reflectEventField(field, checkers, event);
			}
		} catch (Exception e) {
			modError(e);
		}
	}

	public void reflectEventField(Field field, TypeChecks checkers, ArcheologyEvent event) throws IllegalArgumentException, IllegalAccessException {
		boolean throwErr = true;
		TypeCheck check = null;
		ArcheologyType type = event.type;
		for (TypeCheck check_ : checkers.value()) {
			if (check_.type() == type && throwErr) {
				throwErr = false;
			} else {
				check = check_;
			}
		}

		if (throwErr) throw new IllegalArgumentException("You cannot use " + type.toString() + " on " + field.getName() + ". You should use " + check.type().toString());
		if (field.get(event) == null) throw new IllegalArgumentException("type: " + event.type.toString() + ", " + field.getName() + " cannot be null");
	}

	public void modError(Throwable error) {
		if (Strings.getCauses(error).contains(t -> t.getMessage() != null && (t.getMessage().contains("trust anchor") || t.getMessage().contains("SSL") || t.getMessage().contains("protocol")))) {
			Vars.ui.showErrorMessage("@feature.unsupported");
		} else {
			Vars.ui.showException(error);
		}

		Log.err(error);
	}

//	public void update(Building ent) {
//
//	}
//
//	public void showDialog() {
//	    dialog.show();
//	}

	public void fireEvent(ArcheologyEvent event, Building ent) {
		if (event.rewardItem != null) {
			for (ItemStack stack : event.rewardItem) {
				for (int i = stack.amount; i > 0 && ent.items.get(stack.item) < ent.getMaximumAccepted(stack.item); i--) {
					ent.handleStack(stack.item, stack.amount, ent);
				}
			}
		}

		if (event.rewardUnit != null) {
			ReqUnit req = event.rewardUnit;
			Team team = req.hostile ? Vars.state.rules.waveTeam : ent.team;
			if (req.x >= 0) {
				forLoopType(req, team, req.x, req.y);
			} else {
				switch ((int)req.x) {
				case BLOCK_POC:
					forLoopType(req, team, ent.x, ent.y);
					break;
				case SPAWNER_POC:
					if (Vars.state.hasSpawns()) {
						Tile tile = Vars.spawner.getFirstSpawn();
						forLoopType(req, team, tile.worldx(), tile.worldy());
					}
					break;
				case CORE_POC:
					Building build = Vars.state.teams.closestCore(ent.x, ent.y, ent.team);
					forLoopType(req, team, build.x, build.y);
					break;
				}
			}
		}
	}
	
	public void forLoopType(ReqUnit req, Team team, float x, float y) {
	    for(int i = 0; i < req.amount; i++) {
	        req.type.spawn(team, x, y);
	    }
	}

//	public void image(String region) {
//		imageTable.image(Core.atlas.find(Main.transform(region)));
//		invalidate();
//	}
//
//	public void print(String info, boolean gain) {
//		print(info, gain ? Color.blue : Color.red);
//	}
//
//	public void print(String info, Color color) {
//	    String[] str = info.split("\\n");
//	    for(String st : str)
//		    infoTable.add(st).left().pad(3).padLeft(6).padRight(6).color(color);
//		infoTable.row();
//		invalidate();
//	}

	public ArcheologyEvent newBeginEvent(Building ent) {
		Seq<ArcheologyEvent> seq = events[ArcheologyType.begin.ordinal()];
		ArcheologyData data = getData(ent.team);
		ArcheologyEvent event;
		if(data.beginEvent == null) {
			event = seq.get(Mathf.random(seq.size - 1));
			fireEvent(event, ent);
			data.totalEvents.add(event);
			data.beginEvent = event;
		} else {
			event = data.beginEvent;
		}
		return event;
	}
	
	public void fireFinalEvent(Team team, Building ent) {
		Seq<ArcheologyEvent> seq = events[ArcheologyType.finalEvent.ordinal()];
		ArcheologyData data = getData(team);
		ArcheologyEvent event = null;
		for(ArcheologyEvent e : seq) {
		    if(e.name.equals(data.beginEvent.finalEventName)) event = e;
		}
		if(event == null) throw new IllegalArgumentException("No finalEvent name equals " + data.beginEvent.finalEventName);
		data.totalEvents.add(event);
		fireEvent(event, ent);
	}



	public ArcheologyData getData(Team team) {
		if (map[team.id] == null) map[team.id] = new ArcheologyData();
		return map[team.id];
	}

	public ArcheologyEvent nextEvent(Team team) {
		ArcheologyEvent event = null;
		Seq<ArcheologyEvent> seq = events[ArcheologyType.intermediate.ordinal()];
		while(event == null) {
			for (ArcheologyEvent e : seq) {
				if (Mathf.chance(e.chance)) {
					event = e;
					break;
				}
			}
		}
		getData(team).totalEvents.add(event);
		return event;
	}

	private void loadEvent() {
		if (loaded) {
			throw new IllegalAccessError("could't load ArcheologyEvent again.");
		}

		for(int i = 0; i < ArcheologyType.values().length; i++)
			events[i] = new Seq<>();
		LoadedMod mod = Vars.mods.locateMod(Main.modName);
		Fi archeologyRoot = mod.root.child("archeology");
		for (ArcheologyType type : ArcheologyType.values()) {
			Fi folder = archeologyRoot.child(type.toString().toLowerCase());
			if (folder.exists()) {
				for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson"))) {
					parse(mod, file.nameWithoutExtension(), file.readString("UTF-8"), file, type);
				}
			}
		}

		loaded = true;
	}

//	private void initDialog() {
//		dialog = new BaseDialog("Archeology");
//		int width = Core.graphics.getWidth(), height = Core.graphics.getHeight();
//		Log.info("width: " + width + " height: " + height);
//		ArcheologyData data = getData(Vars.player.team());
//		dialog.cont.row();
//		dialog.cont.pane(table -> {
//			table.left();
//			infoTable = table;
//		}).size(width, height / 3f);
//		dialog.cont.row();
//		dialog.cont.add(
//		    new Bar("progress", Pal.accent, () -> data.progress / (5f * Time.toMinutes))
//		).row();
//		dialog.cont.row();
//		dialog.cont.pane(table -> {
//			table.table(t -> {
//			    t.left();
//				imageTable = t;
//			});
//			table.right();
//			table.row();
//			toolTable = table;
//			newArListenerButton(Core.bundle.get("continue"));
//		}).left().padTop(3);
//		dialog.addCloseButton();
//	}

	private void parse(LoadedMod mod, String name, String json, Fi file, ArcheologyType type) {
		try {
			Json parser = Tool.parser.parser;
			JsonValue value = parser.fromJson(null, Jval.read(json).toString(Jformat.plain));
			ArcheologyEvent event = new ArcheologyEvent();
			event.type = type;
			Tool.parser.readFields(event, value);
			events[type.ordinal()].add(event);

		} catch (Exception e) {
			Log.err(e);
		}
	}
}