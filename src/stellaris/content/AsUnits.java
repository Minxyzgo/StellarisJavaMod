package stellaris.content;

import mindustry.ctype.ContentList;
import mindustry.type.UnitType;
import stellaris.type.units.FSalPixShip;

public class AsUnits implements ContentList{
    public static UnitType fship;
    @Override
	public void load(){
	    fship = new FSalPixShip("fs");
	}
}