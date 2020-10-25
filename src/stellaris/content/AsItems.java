package stellaris.content;

import mindustry.ctype.ContentList;
import mindustry.type.Item;

public class AsItems implements ContentList{
	public static Item 
	energyUnit, mineral, neutronMaterial, industryProduction, food;
	
	@Override
	public void load() {
    	energyUnit = new Item("energy-unit");
		mineral = new Item("mineral");
		neutronMaterial = new Item("neutron-material");
		industryProduction = new Item("industry-production");
		food = new Item("food");
	}
	
	
}