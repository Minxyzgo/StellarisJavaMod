package minxyzgo.mlib.entities;

import arc.util.io.*;

public class EntSkill {
    public float reload;
    
    public void update() {
        
    }
    
    public void write(Writes write) {
        write.f(reload);
    }
    
    public void read(Reads read) {
        reload = read.f();
    }
}