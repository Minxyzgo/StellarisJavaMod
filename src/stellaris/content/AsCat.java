package stellaris.content;

import mindustry.type.Category;

public enum AsCat{
    
    turret,

    production,
    
    distribution,

    liquid,

    power,

    defense,

    crafting,

    units,

    effect,

    logic,
    
    testc;

    public static final AsCat[] all = values();
    
    public static AsCat toAs(Category cat){
        return valueOf(cat.toString());
    }

    public AsCat prev(){
        return all[(ordinal() - 1 + all.length) % all.length];
    }

    public AsCat next(){
        return all[(ordinal() + 1) % all.length];
    }
    
    public Category toCat(){
        return Category.valueOf(toString());
    }
}
