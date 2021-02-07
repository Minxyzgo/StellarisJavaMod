package stellaris.core;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import arc.*;
import arc.audio.Music;
import arc.math.Mathf;
import arc.scene.ui.Slider;
import arc.scene.ui.TextField;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Cicon;
import mindustry.ui.MobileButton;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.MenuFragment;
import stellaris.Main;
import stellaris.ui.draw.MenuRed;
//import stellaris.ui.frg.MenuFrg;


import static mindustry.Vars.*;

public class Ui {
	public MobileButton ste;
	public PrintStream stream;
	public int spawnNum = 1, touchCount = 0;
	public Team spawnTeam;
	public ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	public CommandTermanal test = new CommandTermanal();

	public Ui() {
		ste = new MobileButton(Icon.menu, "stellaris", () -> ui.showCustomConfirm("tool", "[green]test" + Main.test, "test-button", "Termanal-test", () -> Main.test = Main.test ? false : true,
							   test::show));
		Events.on(ClientLoadEvent.class, a -> {
			menu();
			//place();
		});
	}
	
	public void play(Music music) {
	    try {
	        Method m2 = SoundControl.class.getDeclaredMethod("play", Music.class);
	        m2.setAccessible(true);
	        m2.invoke(Vars.control.sound, music);
	        Log.info("Start playing " + music);
	    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
	        Log.err(e);
	    }
	}

	private void menu() {
		try {
			Field A = MenuFragment.class.getDeclaredField("renderer");
			A.setAccessible(true);
			A.set(ui.menufrag, new MenuRed());

		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ui.showException(ex);
		}
		/*
		Table t = ui.menuGroup.<Table>find("buttons");
		if(t == null) {
		    Seq<String> s = new Seq<>();
		    ui.menuGroup.forEach(e -> s.add(e.name));


		    ui.showInfo(Strings.join(",", s.toArray(String.class)));


		    //ui.showException(new NullPointerException("t is null"));
		    return;
		}

		t.table(table -> {
		    table.defaults().set(t.defaults());
		    table.row();
		    table.add(ste);
		    //ui.showInfo("over.");
		    table.visible = true;
		}).colspan(2);
		*/
		// t.reset();

		ui.menuGroup.fill(t -> {
			t.clear();
			t.setSize(Core.graphics.getWidth(), Core.graphics.getHeight());
			float size = 120f;
			t.defaults().size(size).pad(5).padTop(4f);
			t.marginTop(60f);
			t.left();
			rows(t, 15);
			t.add(ste);
			t.visible = true;
		});


		ui.hudGroup.fill(t -> {
			t.right();
			t.marginTop(15f);
			BaseDialog dialog = unitSpawnDialog();
			t.button(Icon.admin, dialog::show).update(b -> dialog.invalidate());
			t.row();
			t.button(Icon.crafting, () -> Main.archeology.showDialog()).update(b -> {
			    Building ent = player.team().data().core();
			    if(ent != null) Main.archeology.update(ent);
			});
			t.visible(() -> Main.test && !Vars.net.active());
		});


	}

	public BaseDialog unitSpawnDialog() {
		BaseDialog dialog = new BaseDialog("SetUnitType @author Yuria");
		dialog.cont.add("[" + (spawnTeam == null ? Team.derelict.color.toString() : spawnTeam.toString()) + "]<<-Spawns: " + spawnNum + " ->>").row();
		Player player = Vars.player;
		dialog.cont.pane(t -> {
			int num = 0;
			for (UnitType type : Vars.content.units()) {
				if (type.isHidden())continue;
				num++;
				if (!(num == 0) && num % 5 == 0)t.row();

				t.button(new TextureRegionDrawable(type.icon(Cicon.medium)), () -> {
					
					
					for (int i = 0; i < spawnNum; i++) {
					    Unit unit = type.create(spawnTeam == null ? player.team() : spawnTeam);
						float spread = 40f;
						unit.set(player.getX() + Mathf.range(spread), player.getY() + Mathf.range(spread));
						Time.run(50, () -> {
							Fx.unitSpawn.at(unit.x, unit.y, 0, unit);
							Time.run(30, () -> {
								unit.add();
								Fx.spawn.at(unit);
							});
						});
					}

				}).size(80f);
			}
		}).size(5 * 80f, 4 * 80f);
		dialog.cont.row();
		dialog.cont.pane(t -> {
			Slider slider = new Slider(1, 100, 1, false);
			slider.setStyle(Styles.vSlider);
			slider.changed(() -> {
				spawnNum = (int)slider.getValue();
			});
			slider.change();
			t.left().defaults().left();
			t.add(slider).width(100);
		}).left().padTop(3);
		dialog.cont.pane(t -> {
		    t.right().defaults().right();
		    t.button(Icon.add, () -> {
			    touchCount++;
			    if(touchCount > 6) touchCount = 0;
			    spawnTeam = Team.all[touchCount];
			});
			t.button(Icon.commandRally, () -> {
			    player.team(spawnTeam);
			});
			t.button(Icon.android, this::showLogDialog);
		}).right().padTop(3);
		dialog.cont.row();
		dialog.addCloseButton();
		
		return dialog;
	}

	public void showLogDialog() {
		BaseDialog dialog = new BaseDialog("Last Log");
		dialog.addCloseButton();
		dialog.cont.top();
		dialog.cont.row();
	//	dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
		dialog.cont.row();
		dialog.cont.add("[red]all log");
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			if (stream == null) stream = new PrintStream(out);
			System.setOut(stream);
			byte[] b = out.toByteArray();
			//		ByteArrayInputStream in = new ByteArrayInputStream(b);
			//	System.setOut(old);
			//	byte[] bytes = in.readAllBytes();
			StringBuffer buffer = new StringBuffer();
			for (byte bb : b) {
				buffer.appendCodePoint(bb);
			}

			String[] msg = buffer.toString().split("\\n");
			for (String s : msg) {
				table.add("[lightgray]" + s).left().pad(3).padLeft(6).padRight(6);
				table.row();
			}
		});
		
		dialog.show();
	}


	public static void rows(Table t, int times) {
		for (int x = 0; x < times; x++) {
			t.row();
		}
	}

	/*private void place(){
	    try{
	        Field nameField = HudFragment.class.getDeclaredField("blockfrag");

	        nameField.setAccessible(true);
	        nameField.set(ui.hudfrag, new PlaceFrg());
	        if(!(ui.hudfrag.blockfrag instanceof PlaceFrg)) throw new Error("it isn't PlaceFrg");
	    }catch(NoSuchFieldException | IllegalAccessException ex) {
	        throw new Error(ex);
	    }
	}*/
}