package stellaris.type;

import arc.struct.Seq;
import mindustry.type.Category;
import stellaris.content.AsCat;
//it doesn't work now.
public class CategoryComp{
	private Enum<?> cat;
	private boolean isCategory;
	private static final Seq<CategoryComp> list = new Seq<>();
	
	{
	    for(Category catg : Category.values()){
	        list.add(new CategoryComp(catg));
	    }
	    
	    for(AsCat cata : AsCat.values()){
	        list.add(new CategoryComp(cata));
	    }
	}
	
	public boolean isCat(){
	    return isCategory;
	}
	
	public CategoryComp(Enum<?> kt){
	    if(cat instanceof Category) isCategory = true;
	        cat = kt;
	}
	
	public void set(CategoryComp comp){
	    set(comp.get());
	}
	
	public void set(Enum<?> cat){
	    this.cat = cat;
	}
	
	public Enum<?> get(){
	    return isCategory ? (Category)cat : (AsCat)cat;
	}
	
	@Override
	public boolean equals(Object obj){
	    if(obj instanceof CategoryComp){
	        CategoryComp comp = (CategoryComp)obj;
	        if(comp.isCat() && isCategory) return (Category)cat == (Category)comp.get();
	        return (AsCat)cat == (AsCat)comp.get();
	    }else{
	        return false;
	    }
	}
	public static CategoryComp[] allComp(){
	    return list.toArray(CategoryComp.class);
	}

	public static int all(){
	    return Category.all.length + AsCat.all.length;
	}
}