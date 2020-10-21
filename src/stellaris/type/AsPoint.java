package stellaris.type;

import mindustry.type.Item;
import mindustry.Vars;
import mindustry.ctype.ContentType;

public class AsPoint {
    
    
    
    public enum PointStack {
        copper(1),
        lead(2),
        metaglass(4),
        graphite(4),
        sand(2),
        coal(3),
        titanium(5),
        thorium(9),
        scrap(3),
        silicon(5),
        plastanium(16),
        phasefabric(25, "phase-fabric"),
        surgealloy(41, "surge-alloy"),
        sporePod(4, "spore-pod"),
        blastCompound(11, "blast-compound"),
        pyratite(7);
        
        public int point;
        private String realname;
        private boolean isModlue;
        
        public static int getPointByName(String name) {
        	PointStack p = valueOf(name);
        	if(p == null) return -1;
        	return p.getPoint();
        }
        
        public static boolean checkPoint(Item item, int point) {
        	if(valueOf(item.toString()).point >= point) return true;
        	return false;
        }
        
        PointStack(int point) {
        	this(point, null);
        }
        
        PointStack(int point, String realname) {
        	this(point, realname, false);
        }
        
        PointStack(int point, String realname, boolean isModlue) {
        	this.point = point;
        	this.realname = realname;
        	this.isModlue = isModlue;
        }
        
        
        
        public Item get() {
        	if(isModlue) return null;
        	return getItem();
        }
        
        public Item getItem() {
        	return Vars.content.getByName(ContentType.item, realname == null ? toString() : realname);
        }
        
        public int getPoint() {
        	return point;
        }
        
    }
}
