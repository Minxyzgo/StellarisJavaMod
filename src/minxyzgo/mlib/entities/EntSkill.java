package minxyzgo.mlib.entities;

import arc.util.*;
import arc.util.io.*;

public class EntSkill {
    public float reload = 0;
    
    public void update() {
        
    }
    
    public void write(Writes write) {
        write.f(reload);
    }
    
    public void read(Reads read) {
        reload = read.f();
    }
}