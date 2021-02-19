package stellaris.archeology;

import arc.*;
import arc.util.*;
import mindustry.type.*;

import java.util.*;

public class ArcheologyEvent {
    public static final int
        /* 该方块的坐标 */
        BLOCK_POC = -1,
        /* 敌人出生点坐标 */
        SPAWNER_POC = -2,
        /* 最近核心坐标 */
        CORE_POC = -3;

    /* 事件的名称 */
    public String name = "";
    /* 事件是否是正面事件 */
    public boolean gain = true;
    /* 事件所 增加/减少 的进度 */
    public int schedule = 1;
    /* 对于该事件的介绍 */
    public String info;
    /**
     * 该事件的类型 用于判断TypeCheck
     * 请注意，该设置对应文件夹名，手动设置无用
    */
    public ArcheologyType type = ArcheologyType.begin;
    /* 该事件的符号 */
    public String region;
    /* 该事件所消耗的资源 */
    @Nullable
    public ItemStack[] requirements;

    @Nullable
    @TypeCheck(type = ArcheologyType.begin)
    public int difficulty;

    @Nullable
    public ItemStack[] rewardItem;
    /* 该事件奖励的物品 */
    @Nullable
    public ReqUnit rewardUnit;
    /* 该事件所增加的单位， 可为敌对@link{ReqUnit#hostile} */
    
    @Nullable
    @TypeCheck(type = ArcheologyType.intermediate)
    public float chance = 0.25f;
    /* 事件的触发几率 */

    public String buttonName = Core.bundle.get("continue");
    /* 该事件触发时 继续按钮的名称 */

    @Nullable
    @TypeCheck(type = ArcheologyType.begin)
    public String finalEventName;
    /* 应当执行的最终事件#名称 */
    
    public String localized() {
        return Core.bundle.get("ArcheologyEvent." + name, name);
    }
    
    public String info() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(getColor() + localized());
        joiner.add(Core.bundle.get("ArcheologyEvent" + "." + name + "." + "info", info));
        if(rewardItem != null)
            for(ItemStack stack : rewardItem)
                joiner.add("[green]You get " + stack.amount + " " + stack.item.localizedName);
        if (rewardUnit != null)
            joiner.add(getColor() + rewardUnit.amount + " " + rewardUnit.type.localizedName + " generated");
        return  joiner.toString();
    }

    public String getColor() {
        return gain ? "[green]" : "[red]";
    }
    
    public void init() {
        if(!gain && schedule > 0) {
            /* 当是负面事件时， 应使进度减少 */
            schedule = -schedule;
        }
    }
    
    class ReqUnit {
        public UnitType type;
        /* 单位所生成的坐标 (由于地图不可控因素不推荐更改) 推荐采用顶上常量处理. (若必须使用，则默认已乘tilesize)*/
        public float x = BLOCK_POC, y = BLOCK_POC;
        public int amount;
        /* 该单位是否敌对 (wave队伍) */
        public boolean hostile = false;
    }
}