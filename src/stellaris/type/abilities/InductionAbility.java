package stellaris.type.abilities;

import arc.*;
import arc.struct.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.input.GestureDetector.GestureListener;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.input.*;
import minxyzgo.mlib.type.*;
import minxyzgo.mlib.*;
import minxyzgo.mlib.input.*;

import static mindustry.game.EventType.*;


import static mindustry.Vars.*;

public class InductionAbility extends Ability {
	/* For Power Unit */

	private static final InputProcessor inputMove;
	private static final GestureListener inputPan;
	private static GestureDetector panDetector;
	private volatile static boolean touched;
	private volatile static InductionAbility innerAbility;

	public final static String type = "Induction";


	public float range = 1200f;
	public float consumePower = 700f;
	public Effect spawnEffect = Fx.none;
	public Effect disappearEffect = Fx.none;
	public Effect orderedEffect = Fx.none;

	static {
		inputPan = new GestureListener() {
			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				if (!touched || player.dead()) return false;
				float panmultipler = 1.25f;
				if (!renderer.isLanding()) {
					//pan player
					Core.camera.position.x += deltaX * panmultipler;
					Core.camera.position.y += deltaY * panmultipler;

				}
				return false;
			}
		};

		inputMove = new InputProcessor() {
			//	final float playerSelectRange = mobile ? 17f : 11f;

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, KeyCode button) {

				if (!touched || player.dead()) return false;
				//	InputHandler Ihandler = getInput();
				// Tile tile = tileAt(screenX, screenY);
				float worldx = Core.input.mouseWorld(screenX, screenY).x, worldy = Core.input.mouseWorld(screenX, screenY).y;
				if (tapPlayer(worldx, worldy, innerAbility.range)) {

					Call.effect(innerAbility.disappearEffect, player.getX(), player.getY(), player.unit().rotation, Color.white);


					DataSkill data = Tool.skills.getType(type);
					data.sendSkill(tileX(screenX) * tilesize, tileY(screenY) * tilesize);

					Call.effect(innerAbility.spawnEffect, player.getX(), player.getY(), player.unit().rotation, Color.white);

					touched = false;
				}

				return false;
			}

			InputHandler getInput() {
				return control.input;
			}

			boolean tapPlayer(float x, float y, float range) {
				return player.within(x, y, range);
			}

			/*Tile tileAt(float x, float y) {
				return world.tile(tileX(x), tileY(y));
			}*/

			int tileX(float cursorX) {
				InputHandler Ihandler = getInput();

				Vec2 vec = Core.input.mouseWorld(cursorX, 0);

				if (Ihandler.selectedBlock()) {
					vec.sub(Ihandler.block.offset, Ihandler.block.offset);
				}
				return World.toTile(vec.x);
			}

			int tileY(float cursorY) {
				InputHandler Ihandler = getInput();
				Vec2 vec = Core.input.mouseWorld(0, cursorY);
				if (Ihandler.selectedBlock()) {
					vec.sub(Ihandler.block.offset, Ihandler.block.offset);
				}
				return World.toTile(vec.y);
			}
		};
		Events.on(EventType.ClientLoadEvent.class, e -> {
			/*
			ui.hudGroup.fill(t -> {
				t.left();
				t.marginTop(15f);
				button = t.button(Icon.admin, () -> {
					InductionAbility.touched = true;
					Powerc c = (Powerc)player.unit();

					c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
					//c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
					//Time.run(160f, () -> touched = false);
				}).disabled(tri -> player.unit() instanceof Powerc && ((Powerc)player.unit()).conPower(consumePower)).get();
				t.visible(() -> player != null && player.unit() instanceof Powerc && player.unit().abilities().contains(this));

			});
			*/
			//	Core.input.addProcessor(new GestureDetector(inputPan));
			//	Core.input.addProcessor(inputMove);
		});

		Events.on(UnitChangeEvent.class, e -> {
			if (e.player == player) {
				if (player.unit().type == null) return;
				player.unit().type.abilities.each(ability -> {
					touched = false;
					if (ability.getClass() == InductionAbility.class) {
						innerAbility = (InductionAbility)ability;
						Core.input.addProcessor(panDetector = new GestureDetector(inputPan));
						Core.input.addProcessor(inputMove);
						return;
					}
					reset();
				});
			}
		});
		Events.on(ResetEvent.class, e -> {
			touched = false;
			reset();
		});
	}


	public static void reset() {
		if (Core.input.getInputProcessors().contains(panDetector)) Core.input.removeProcessor(panDetector);
		if (Core.input.getInputProcessors().contains(inputMove)) Core.input.removeProcessor(inputMove);
		panDetector = null;
		innerAbility = null;
	}

	@Override
	public String localized() {
		return "Induction";
	}


	public static void setTouched(boolean tou) {
		touched = tou;
	}

	@Override
	public void draw(Unit unit) {

		if (touched /*&& innerAbility == this*/) {
			InputHandler input = control.input;
			Vec2 v = Core.input.mouseWorld(input.getMouseX(), input.getMouseY());

			float sin = Mathf.absin(Time.time, 5f, 1f);
			boolean checkR = player.within(v.x, v.y, range);
			float px = player.getX(), py = player.getY();
			Color checkColor = checkR ? Pal.accent : Color.red;
			Draw.z(Layer.light);
			Draw.color(Pal.accent);
			Drawf.circles(px, py, player.unit().hitSize() * 1.5f + sin - 2f, Pal.accent);
			Drawf.dashCircle(px, py, range * 2, Pal.accent);
			Draw.color(checkColor);
			Drawf.circles(v.x, v.y, player.unit().hitSize() * 1.5f + sin - 2f, checkColor);
			Lines.stroke(15f);
			int segs = (int)Math.floor(unit.dst(v.x, v.y) / tilesize);
			Lines.dashLine(px, py, v.x, v.y, segs);
			Draw.reset();
		}
	}

	public static class InductionSkill extends SkillButton {
		{
			clearChildren();
			clicked(() -> {
				InductionAbility.setTouched(true);
			});

		}

		@Override
		public void callSkill(Player pl, Object... objects) {
			pl.unit().set((Integer)objects[0], (Integer)objects[1]);
		}

		@Override
		public String getType() {
			return InductionAbility.type;
		}
	}
}