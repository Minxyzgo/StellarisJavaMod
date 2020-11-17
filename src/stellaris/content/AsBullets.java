package stellaris.content;

import mindustry.ctype.ContentList;
import mindustry.entities.bullet.BulletType;
import stellaris.type.units.FSalPixShip;

public class AsBullets implements ContentList{
    public static BulletType smallLaser;
    @Override
	public void load() {
	    smallLaser = new FSalPixShip.SmallLaser();
	}
}