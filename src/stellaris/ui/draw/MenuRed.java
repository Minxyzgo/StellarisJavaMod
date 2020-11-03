package stellaris.ui.draw;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.core.Renderer;
import mindustry.graphics.MenuRenderer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;
//import static arc.Core.*;

public class MenuRed extends MenuRenderer{
    public static final float darkness = 0.3f;
    public final int width = !mobile ? 100 : 60, height = !mobile ? 50 : 40;

    public int cacheFloor, cacheWall;
    public Camera camera = new Camera();
    public Mat mat = new Mat();
    public FrameBuffer shadows;
    public CacheBatch batch;
    public float time = 0f;
    public float flyerRot = 45f;
    public int flyers = Mathf.chance(0.2) ? Mathf.random(35) : Mathf.random(15);
    public UnitType flyerType = Structs.select(UnitTypes.flare, UnitTypes.flare, UnitTypes.horizon, UnitTypes.mono, UnitTypes.poly, UnitTypes.mega, UnitTypes.zenith);
   // public Rend rend;
    
 /*   public class Rend extends Renderer{
        public Rend(){
            super();
            Core.camera = MenuRed.this.camera;
            renderer = this;
        }
    }*/

    public MenuRed(){
        Time.mark();
        generate();
        cache();
        Log.info("Time to generate menu: @", Time.elapsed());
    }

    private void generate(){
        
        world.beginMapLoad();
        Tiles tiles = world.resize(width, height);
        shadows = renderer.effectBuffer ;
        renderer.effectBuffer.resize(width, height);
        //rend = new Rend();
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Block floor = Blocks.space;
                Block ore = Blocks.air;
                Block wall = Blocks.air;

                Tile tile;
                tiles.set(x, y, (tile = new CachedTile()));
                tile.x = (short)x;
                tile.y = (short)y;
                tile.setFloor(floor.asFloor());
                tile.setBlock(wall);
                tile.setOverlay(ore);
            }
        }

        world.endMapLoad();
        
    }

    private void cache(){
        //draw shadows
        Core.camera = this.camera;
        
        Draw.proj().setOrtho(0, 0, shadows.getWidth(), shadows.getHeight());
        
        
       // shadows.begin(Color.clear);
        renderer.draw();
        //Draw.color(Color.black);
        //Draw.color();
       // shadows.end();
        
        
        

        /*for(Tile tile : world.tiles){
            if(tile.block() != Blocks.air){
                Fill.rect(tile.x + 0.5f, tile.y + 0.5f, 1, 1);
            }
        }*/
        
        

        Batch prev = Core.batch;

        Core.batch = batch = new CacheBatch(new SpriteCache(width * height * 6, false));
        batch.beginCache();

        /*for(Tile tile : world.tiles){
            tile.floor().drawBase(tile);
        }

        for(Tile tile : world.tiles){
            tile.overlay().drawBase(tile);
        }*/

        cacheFloor = batch.endCache();
        batch.beginCache();

        /*for(Tile tile : world.tiles){
            tile.block().drawBase(tile);
        }*/

        cacheWall = batch.endCache();

        Core.batch = prev;
    }

    public void render(){
        time += Time.delta;
        float scaling = Math.max(Scl.scl(4f), Math.max(Core.graphics.getWidth() / ((width - 1f) * tilesize), Core.graphics.getHeight() / ((height - 1f) * tilesize)));
        camera.position.set(width * tilesize / 2f, height * tilesize / 2f);
        camera.resize(Core.graphics.getWidth() / scaling,
        Core.graphics.getHeight() / scaling);

        mat.set(Draw.proj());
        Draw.flush();
        Draw.proj(camera);
        batch.setProjection(camera.mat);
        batch.beginDraw();
        batch.drawCache(cacheFloor);
        batch.endDraw();
        Draw.color();
        Draw.rect(Draw.wrap(shadows.getTexture()),
        width * tilesize / 2f - 4f, height * tilesize / 2f - 4f,
        width * tilesize, -height * tilesize);
        Draw.flush();
        batch.beginDraw();
        batch.drawCache(cacheWall);
        batch.endDraw();

        drawFlyers();

        Draw.proj(mat);
        Draw.color(0f, 0f, 0f, darkness);
        Fill.crect(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());
        Draw.color();
    }

    private void drawFlyers(){
        Draw.color(0f, 0f, 0f, 0.4f);

        TextureRegion icon = flyerType.icon(Cicon.full);

        float size = Math.max(icon.width, icon.height) * Draw.scl * 1.6f;

        flyers((x, y) -> {
            Draw.rect(flyerType.region, x - 12f, y - 13f, flyerRot - 90);
        });

        flyers((x, y) -> {
            Draw.rect("circle-shadow", x, y, size, size);
        });
        Draw.color();

        flyers((x, y) -> {
            float engineOffset = flyerType.engineOffset, engineSize = flyerType.engineSize, rotation = flyerRot;

            Draw.color(Pal.engine);
            Fill.circle(x + Angles.trnsx(rotation + 180, engineOffset), y + Angles.trnsy(rotation + 180, engineOffset),
            engineSize + Mathf.absin(Time.time(), 2f, engineSize / 4f));

            Draw.color(Color.white);
            Fill.circle(x + Angles.trnsx(rotation + 180, engineOffset - 1f), y + Angles.trnsy(rotation + 180, engineOffset - 1f),
            (engineSize + Mathf.absin(Time.time(), 2f, engineSize / 4f)) / 2f);
            Draw.color();

            Draw.rect(flyerType.region, x, y, flyerRot - 90);
        });
    }

    private void flyers(Floatc2 cons){
        float tw = width * tilesize * 1f + tilesize;
        float th = height * tilesize * 1f + tilesize;
        float range = 500f;
        float offset = -100f;

        for(int i = 0; i < flyers; i++){
            Tmp.v1.trns(flyerRot, time * (2f + flyerType.speed));

            cons.get((Mathf.randomSeedRange(i, range) + Tmp.v1.x + Mathf.absin(time + Mathf.randomSeedRange(i + 2, 500), 10f, 3.4f) + offset) % (tw + Mathf.randomSeed(i + 5, 0, 500)),
            (Mathf.randomSeedRange(i + 1, range) + Tmp.v1.y + Mathf.absin(time + Mathf.randomSeedRange(i + 3, 500), 10f, 3.4f) + offset) % th);
        }
    }

    @Override
    public void dispose(){
        batch.dispose();
        shadows.dispose();
        //rend.dispose();
    }
    
    
}