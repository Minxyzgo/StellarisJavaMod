package stellaris.archeology;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.*;
import arc.util.*;
import arc.util.serialization.*;
import arc.util.serialization.Jval.*;

import java.lang.reflect.*;
import java.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.game.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.*;
import minxyzgo.mlib.*;

import stellaris.*;
import stellaris.archeology.ArcheologyEvent.*;

import static stellaris.archeology.ArcheologyEvent.*;

@SuppressWarnings("unchecked")
public class Archeology {
	private static final int max = 10;
	private ArcheologyData[] map = new ArcheologyData[256];
	private Seq<ArcheologyEvent>[] events = new Seq[ArcheologyType.values().length];
	private boolean loaded = false;
	private BaseDialog dialog;
	private Table infoTable, toolTable, imageTable;
	private TextButton conButton;
	private ArcheologyEvent lastEvent;

	{
		Time.mark();
		loadEvent();
		Log.info("load archeology. time: @", Time.elapsed());
		Tool.onLoad(() -> {
			initDialog();
			for (Seq<ArcheologyEvent> seq : events) {
				seq.each(this::reflectEvent);
			}
		});
		Events.on(EventType.ResetEvent.class, e -> {
			map = new ArcheologyData[256];
		});
	}

	public void reflectEvent(ArcheologyEvent event) {
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

	public void update(Building ent) {
		ArcheologyData data = getData(ent.team);
		data.build = ent;
		if(data.beginEvent == null) return;
		boolean consume = true;
		for (ItemStack stack : lastEvent.requirements) {
			consume = ent.items.has(stack.item, stack.amount);
			if (!consume || data.finish) break;
		}
		conButton.setDisabled(!consume || data.finish);
		if (data.crafting && !data.finish) {
			data.progress += Time.delta;
			if (data.progress >= 5f * Time.toMinutes) {
				lastEvent = nextEvent();
				fireEvent(lastEvent, ent);
				data.schedule += lastEvent.schedule;
				data.crafting = false;
				data.progress = 0;
				data.events += 1;
			}
		}
		
		if(data.events >= max || data.schedule >= data.beginEvent.difficulty && !data.finish) {
		    if(data.schedule >= data.beginEvent.difficulty) {
		        fireFinalEvent(ent.team, ent);
		    } else {
		        print("what f**k", false);
		        newArListenerButton("holy shit!");
		    }
		    
		    data.finish = true;
		}
	}
	
	public void showDialog() {
	    dialog.show();
	}

	public void invalidate() {
	    infoTable.invalidate();
		toolTable.invalidate();
		imageTable.invalidate();
		dialog.cont.invalidate();
	}

	public void fireEvent(ArcheologyEvent event, Building ent) {
		if (event.rewardItem != null) {
			for (ItemStack stack : event.rewardItem) {
				for (int i = stack.amount; i > 0 && ent.items.get(stack.item) < ent.getMaximumAccepted(stack.item); i--) {
					ent.handleStack(stack.item, stack.amount, ent);
				}
				
				print("You get " + stack.amount + " " + stack.item.localizedName, Color.green);
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
			
			print(req.amount + " " + req.type.localizedName + " generated", req.hostile);
		}
		
		newArListenerButton(event.buttonName);
	}
	
	public void forLoopType(ReqUnit req, Team team, float x, float y) {
	    for(int i = 0; i < req.amount; i++) {
	        req.type.spawn(team, x, y);
	    }
	}

	public void image(String region) {
		imageTable.image(Core.atlas.find(Main.transform(region)));
		invalidate();
	}

	public void print(String info, boolean gain) {
		infoTable.add(info).left().pad(3).padLeft(6).padRight(6).color(gain ? Color.blue : Color.red);
		infoTable.row();
		invalidate();
	}
	
	public void print(String info, Color color) {
	    String[] str = info.split("\\n");
	    for(String st : str) 
		    infoTable.add(st).left().pad(3).padLeft(6).padRight(6).color(color);
		infoTable.row();
		invalidate();
	}

	public ArcheologyEvent newBeginEvent(Building ent) {
		Seq<ArcheologyEvent> seq = events[ArcheologyType.begin.ordinal()];
		ArcheologyEvent event = seq.get(Mathf.random(seq.size - 1));
		fireEvent(event, ent);
		print(event.info(), event.gain);
		image(event.region);
		newArListenerButton(event.buttonName);
		return event;
	}
	
	public void fireFinalEvent(Team team, Building ent) {
		Seq<ArcheologyEvent> seq = events[ArcheologyType.finalEvent.ordinal()];
		ArcheologyData data = getData(team);
		ArcheologyEvent event = null;
		for(ArcheologyEvent e : seq) {
		    if(e.name.equals(data.beginEvent.finalEventName)) event = e;
		}
		
		if(event == null) throw new IllegalArgumentException("No finalEvent name like " + data.beginEvent.finalEventName);
		fireEvent(event, ent);
		print(event.info(), event.gain);
		image(event.region);
		newArListenerButton(event.buttonName);
	}

	public void newArListenerButton(String text) {
		if (conButton != null) {
			toolTable.removeChild(conButton);
			conButton = null;
		}

		TextButton button = Elem.newButton(text, () -> {
			ArcheologyData data = getData(Vars.player.team());
			Building ent = data.build;
			boolean check = true;
			if(data.beginEvent == null) {
			    Team team = Vars.player.team();
			    data.beginEvent = newBeginEvent(ent == null ? team.data().core() : ent);
			    data.events += 1;
			    check = false;
			}
			if (lastEvent != null && ent != null) {
				for (ItemStack stack : lastEvent.requirements) {
					ent.items.remove(stack.item, stack.amount);
				}
			}
			if(check) data.crafting = true;
		});
		toolTable.add(button);
		invalidate();
	}

	public ArcheologyData getData(Team team) {
		if (map[team.id] == null) map[team.id] = new ArcheologyData();
		return map[team.id];
	}

	public ArcheologyEvent nextEvent() {
		ArcheologyEvent event = null;
		Seq<ArcheologyEvent> seq = events[ArcheologyType.intermediate.ordinal()];
		for (ArcheologyEvent e : seq) {
			if (Mathf.chance(e.chance)) {
				event = e;
				break;
			}
		}

		if (event == null) event = seq.get(Mathf.random(seq.size - 1));
		image(event.region);
		print(event.info(), event.gain);
		newArListenerButton(event.buttonName);
		return event;
	}

	private void loadEvent() {
		if (loaded) {
			throw new IllegalAccessError("could't load ArcheologyEvent again.");
		}

		Arrays.fill(events, new Seq<>());
		LoadedMod mod = Vars.mods.locateMod(Main.modName);
		Fi archeologyRoot = mod.root.child("archeology");
		for (ArcheologyType type : ArcheologyType.values()) {
			Fi folder = archeologyRoot.child(type.toString());
			if (folder.exists()) {
				for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson"))) {
					parse(mod, file.nameWithoutExtension(), file.readString("UTF-8"), file, type);
				}
			}
		}

		loaded = true;
	}

	private void initDialog() {
		dialog = new BaseDialog("Archeology");
		int width = Core.graphics.getWidth(), height = Core.graphics.getHeight();
		Log.info("width: " + width + " height: " + height);
		ArcheologyData data = getData(Vars.player.team());
		dialog.cont.add(
		    new Bar("progress", Pal.accent, () -> data.progress / (5f * Time.toMinutes))
		);
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			infoTable = table;
		}).top().size(width, height / 3f);
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			table.table(t -> {
				imageTable = t;
			});
			table.row();
			toolTable = table;
			newArListenerButton(Core.bundle.get("continue"));
		}).size(width, height / 4f);
		dialog.addCloseButton();
	}

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