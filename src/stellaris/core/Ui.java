package stellaris.core;

import java.io.*;
import java.lang.reflect.Field;


import arc.*;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.ui.Dialog;
import arc.scene.ui.Slider;
import arc.scene.ui.Label.LabelStyle;
import arc.scene.ui.SettingsDialog.SettingsTable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Cicon;
import mindustry.ui.MobileButton;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.MenuFragment;
import stellaris.Main;
import stellaris.content.AsUnits;
import stellaris.ui.draw.MenuRed;
//import stellaris.ui.frg.MenuFrg;


import static mindustry.Vars.*;

public class Ui {
	public MobileButton ste;
	public int spawnNum = 1;

	public Ui() {
		ste = new MobileButton(Icon.menu, "stellaris", () -> ui.showCustomConfirm("tool", "[green]test" + Main.test, "test-button", "log-test", () -> Main.test = Main.test ? false : true,
							   this::showLogDialog));
		Events.on(ClientLoadEvent.class, a -> {
			menu();
			//place();
		});
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

			t.button(Icon.admin, () -> {
				Player player = Vars.player;
				ui.showCustomConfirm("", "test", "fship", "fhz", () -> AsUnits.fship.spawn(player.team(), player.getX(), player.getY()), () -> AsUnits.fhz.spawn(player.team(), player.getX(), player.getY()));
			});
			t.visible(() -> Main.test && !Vars.net.active());
		});


	}

	public void unitSpawnDialog() {
		BaseDialog dialog = new BaseDialog("SetUnitType @author Yuria");
		dialog.cont.add("<<-Spawns: " + spawnNum + " ->>").row();
		dialog.cont.pane(t -> {
			int num = 0;
			for (UnitType type : Vars.content.units()) {
				if (type.isHidden())continue;
				num++;
				if (!(num == 0) && num % 5 == 0)t.row();

				t.button(new TextureRegionDrawable(type.icon(Cicon.medium)), () -> {
					Player player = Vars.player;
					Unit unit = type.create(player.team());
					unit.set(player.getX(), player.getY());
					unit.add();

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
		dialog.cont.row();
		dialog.addCloseButton();
		dialog.show();
	}

	public void showLogDialog() {


		BaseDialog dialog = new BaseDialog("System");
		dialog.addCloseButton();
		dialog.cont.add("------------------------").fillX().wrap().get().setAlignment(Align.center);
		dialog.cont.row();

		dialog.cont.image().color(Pal.accent).fillX().height(3f).pad(3f);
		dialog.cont.row();
		dialog.cont.add("[red]all log");
		dialog.cont.row();
		dialog.cont.pane(table -> {
			table.left();
			PrintStream old = System.out;
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			PrintStream stream = new PrintStream(out);
			System.setOut(stream);
			byte[] b = out.toByteArray();
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			System.setOut(old);
			byte[] bytes = in.readAllBytes();
			StringBuffer buffer = new StringBuffer();
			for (byte bb : bytes) {
				buffer.append(Character.toString(Integer.valueOf(String.valueOf(bb))));
			}
			in.reset();
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