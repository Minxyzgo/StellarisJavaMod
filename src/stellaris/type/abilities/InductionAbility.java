package stellaris.type.abilities;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
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
import mindustry.world.*;
import stellaris.Main;
import stellaris.type.intf.*;

import static mindustry.Vars.*;

public class InductionAbility extends Ability {
	/* For Power Unit */
	private ImageButton button;
	private final InputProcessor inputMove;
	private boolean touched, Bdisabled;
	{

		inputMove = new InputProcessor() {
			final float playerSelectRange = mobile ? 17f : 11f;

			{
				Events.on(EventType.ClientLoadEvent.class, e -> {
					ui.hudGroup.fill(t -> {
						t.left();
						t.marginTop(15f);
						button = t.button(Icon.admin, () -> {
							touched = true;
							Powerc c = (Powerc)player.unit();
							c.status(Math.max(c.status() - consumePower * Time.delta, 0f));
							Time.run(160f, () -> touched = false);
						}).disabled(b -> Bdisabled).get();
						t.visible(() -> true);

					});
				});
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, KeyCode button) {

				if (InductionAbility.this.button.isDisabled() || !touched || player.dead() || player.unit().getClass() != type) return false;
				//	InputHandler Ihandler = getInput();
				Tile tile = tileAt(screenX, screenY);
				float worldx = Core.input.mouseWorld(screenX, screenY).x, worldy = Core.input.mouseWorld(screenX, screenY).y;
				if (tapPlayer(worldx, worldy, playerSelectRange) && tapPlayer(worldx, worldy, range)) {
					disappearEffect.at(player.getX(), player.getY(), player.unit().rotation, player);
					Time.run(delay, () -> {
						player.set(tile.worldx(), tile.worldy());
						spawnEffect.at(player.getX(), player.getY(), player.unit().rotation, player);
					});
					touched = true;
					return true;
				}

				return false;
			}

			InputHandler getInput() {
				return control.input;
			}

			boolean tapPlayer(float x, float y, float range) {
				return player.within(x, y, range);
			}

			Tile tileAt(float x, float y) {
				return world.tile(tileX(x), tileY(y));
			}

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
		Core.input.addProcessor(inputMove);
	}
	public Class<? extends Unit> type;
	public float range = 200f;
	public float consumePower = 700f;
	public float delay = 64f;
	public Effect spawnEffect = Fx.none;
	public Effect disappearEffect = Fx.none;
	public Effect orderedEffect = Fx.none;

	public InductionAbility(Class<? extends  Unit> type) {
		this.type = type;
	}

	InductionAbility() {}

	@Override
	public String localized() {
		return "Induction";
	}

	@Override
	public void update(Unit unit) {
		Powerc c = (Powerc)unit;
		Bdisabled = c.conPower(consumePower);
		/*  */
		if(Main.test) ui.showInfoToast("boolean: " + Bdisabled, Time.delta);
	}

	@Override
	public void draw(Unit unit) {

		if (touched && !button.isDisabled()) {
			InputHandler input = control.input;
			Vec2 v = Core.input.mouseWorld(input.getMouseX(), input.getMouseY());

			float sin = Mathf.absin(Time.time, 5f, 1f);
			boolean checkR = player.within(v.x, v.y, range);
			float px = player.getX(), py = player.getY();
			Color checkColor = checkR ? Pal.accent : Color.red;
			Draw.color(Pal.accent);
			Drawf.circles(px, py, player.unit().hitSize() * 1.5f + sin - 2f, Pal.accent);
			Drawf.dashCircle(px, py, range, Pal.accent);
			Draw.color(checkColor);
			Drawf.circles(v.x, v.y, player.unit().hitSize() * 1.5f + sin - 2f, checkColor);
			Drawf.tri(px, py, Mathf.dst(px, py, v.x, v.y), 2.75f, Angles.angle(px, py, v.x, v.y));
			Draw.reset();
		}
	}
}