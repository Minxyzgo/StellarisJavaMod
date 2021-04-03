package minxyzgo.mlib.input;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.gen.*;
import minxyzgo.mlib.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.type.*;

public class SkillButtonStack {
	public float cooldown = 21.5f;
	public SkillButton button;
	public TextureRegion region;
	
	public SkillButtonStack(TextureRegion region, float cooldown) {
	    this.region = region;
	    this.cooldown = cooldown;
	}	
	public abstract static class SkillButton extends DataSkill {
	    protected final SkillButtonStack owner;
		public EntSkill ent = new EntSkill();
		public Boolp disabledBoolp = () -> false;
		public SkillButton(SkillButtonStack owner, TextureRegion region, ImageButton.ImageButtonStyle style) {
			super(region, style);
			this.owner = owner;
			this.cooldown = owner.cooldown;
		}

		@Override
		public EntSkill getEnt() {
			return ent;
		}

		@Override
		public void update() {
			super.update();
			ent.reload = Math.min(ent.reload + Time.delta, Tool.skills.dataCooldown);
			setDisabled(ent.reload < (Tool.skills.dataCooldown == 0f ? cooldown : Tool.skills.dataCooldown) || disabledBoolp.get());
		}


		@Override
		public void build(Table parent) {
			parent.add(this).width(40).height(40);//.update(v -> {
			// v.setChecked(Skills.dataSkill == entity.getSkill());

			//v.setDisabled(getEnt().reload < cooldown);
			//});
			parent.row();
		}
	}

	public class SkillStyle extends ImageButton.ImageButtonStyle {


		{

			down = Styles.flatDown;
			up = Styles.black;
			over = Styles.flatOver;
			imageDisabledColor = Color.HSVtoRGB(1, 1, 1, 0.4f);
			imageUpColor = Color.white;
			disabled = new SkillDrawable();
			checked = Styles.flatDown;
		}
	}

	public class SkillDrawable extends TextureRegionDrawable {

		{
			region = Tex.whiteui.getRegion();
		}


		@Override
		public void draw(float x, float y, float width, float height) {
			float cooldownProgress = button.getEnt().reload / cooldown;
			float cooldownProgressNega = (1 - cooldownProgress);
			Draw.color(Tmp.c1.set(Color.HSVtoRGB(0, 0, 0, 0.3f)).toFloatBits());
			Draw.rect(region, x + width / 2.0f, y + height - height * cooldownProgressNega / 2f, width, height * cooldownProgressNega);
			Draw.color(Tmp.c1.set(Color.HSVtoRGB(0, 0, 0, 0.8f)).toFloatBits());
			Draw.rect(region, x + width / 2.0f, y + height * cooldownProgress / 2f, width, height * cooldownProgress);
		}
	}
}