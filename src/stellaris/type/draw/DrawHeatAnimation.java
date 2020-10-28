package stellaris.type.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.draw.DrawBlock;

public class DrawHeatAnimation extends DrawBlock{
    public int frameCount = 3;
    public float frameSpeed = 5f;
    public boolean sine = true;
    public TextureRegion[] frames;
    public TextureRegion liquid, top;
    public boolean drawLiquid,drawTop, drawHeat, drawBaseAnimation;
    public Color heatColor;
    
    public DrawHeatAnimation(boolean  drawLiquid, boolean drawTop) {
        this.drawLiquid = drawLiquid;
        this.drawTop = drawTop;
    }
    
    public DrawHeatAnimation heatc(Color color) {
        drawHeat = true;
        heatColor = color;
        return this;
    }
    
    public DrawHeatAnimation frame(int count, int speed) {
        frameCount = count;
        frameSpeed = speed;
        return this;
    }
    
    public DrawHeatAnimation sine(boolean b) {
        sine = b;
        return this;
    }
    
    public DrawHeatAnimation base(boolean b) {
        drawBaseAnimation = b;
        return this;
    }
    
    @Override
    public void draw(GenericCrafterBuild entity){
        float s = 0.3f;
        float ts = 0.6f;
        float delta = entity.getDisplayEfficiency();
        Draw.rect(entity.block.region, entity.x, entity.y);
        if (drawHeat) {
            Draw.color(heatColor);
            Draw.alpha(entity.warmup * ts * (1f - s + Mathf.absin(Time.time(), 3f, s)));
            Draw.blend(Blending.additive);
        }
        
        if(entity.totalProgress == 0 && drawBaseAnimation) Draw.rect(frames[0], entity.x, entity.y);
        
        Draw.rect(
            sine ?
                frames[(int)Mathf.absin(entity.totalProgress, frameSpeed * delta, frameCount - 0.001f)] :
                frames[(int)(((entity.totalProgress / frameSpeed) * delta) % frameCount)],
            entity.x, entity.y);
        if (drawHeat) {
            Draw.blend();
            Draw.color();
        }
        
        if (drawLiquid) {
            Draw.color(Color.clear, entity.liquids.current().color, entity.liquids.total() / entity.block.liquidCapacity);
            Draw.rect(liquid, entity.x, entity.y);
        }
        
        if (drawTop) {
            Draw.color();
            Draw.rect(top, entity.x, entity.y);
        }
        
    }

    @Override
    public void load(Block block){
        frames = new TextureRegion[frameCount];
        for(int i = 0; i < frameCount; i++){
            frames[i] = Core.atlas.find(block.name + "-frame" + i);
        }

        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return drawTop ? new TextureRegion[]{block.region, top} : new TextureRegion[]{block.region};
    }
}