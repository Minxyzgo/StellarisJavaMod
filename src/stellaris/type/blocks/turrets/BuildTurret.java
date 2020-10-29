package stellaris.type.blocks.turrets;


import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ChargeTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import stellaris.type.blocks.turrets.interfaces.BuildT;

public final class BuildTurret {
    
    
    
	static boolean basePlaceOn(Turret turret, Tile tile, Team team){
        if(tile == null || !(tile.block() instanceof BuildT)) return false;
        CoreBuild core = team.core();
        if(!Vars.state.rules.infiniteResources && !core.items.has(turret.requirements)) return false;
        
        return  turret.size >= tile.block().size && ((BuildT)turret).orLevel(((BuildT)tile.block()).getLevel());
    }
    public static class ItemTurretBuildT extends ItemTurret implements BuildT{
        public ItemTurretBuildT(String name){super(name);}
        @Override
        public boolean canPlaceOn(Tile tile, Team team){
            return basePlaceOn(this, tile, team);
        }
    }
    public static class ChargeTurretBuildT extends ChargeTurret implements BuildT{
        public ChargeTurretBuildT(String name){super(name);}
        @Override
        public boolean canPlaceOn(Tile tile, Team team){
            return basePlaceOn(this, tile, team);
        }
    }
	
	
}