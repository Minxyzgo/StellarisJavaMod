package minxyzgo.mlib.input;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.gen.*;
import minxyzgo.mlib.entities.*;
import minxyzgo.mlib.type.*;
import minxyzgo.mlib.type.Skills.DataFireEvent;

public abstract class SkillButton extends DataSkill {
    public EntSkill ent = new EntSkill();
    public final ImageButton.ImageButtonStyle imageStyle = new ImageButton.ImageButtonStyle() {{
        down = Styles.flatDown;
        up = Styles.black;
        over = Styles.flatOver;
        imageDisabledColor = Color.HSVtoRGB(1, 1, 1, 0.4f);
        imageUpColor = Color.white;
        this.disabled = SkillButton.this.disabled;
        checked = Styles.flatDown;
    }};
    public float cooldown = 21.5f;
    public final TextureRegionDrawable disabled = new TextureRegionDrawable() {
        @Override
        public void draw(float x, float y, float width, float height) {
            float cooldownProgress = getEnt().reload / cooldown;
            float cooldownProgressNega = (1 - cooldownProgress);
            Draw.color(Tmp.c1.set(Color.HSVtoRGB(0, 0, 0, 0.3f)).toFloatBits());
            Draw.rect(region, x + width / 2.0f, y + height - height * cooldownProgressNega / 2f, width, height * cooldownProgressNega);
            Draw.color(Tmp.c1.set(Color.HSVtoRGB(0, 0, 0, 0.8f)).toFloatBits());
            Draw.rect(region, x + width / 2.0f, y + height * cooldownProgress / 2f, width, height * cooldownProgress);
        }
    };
    
    @Override
    public EntSkill getEnt() {
        return ent;
    }
    
    @Override
    public void build(Table parent) {
        parent.add(this).update(v -> {
           // v.setChecked(Skills.dataSkill == entity.getSkill());

           //v.setDisabled(getEnt().reload < cooldown);
        });
        parent.row();
    }
} 