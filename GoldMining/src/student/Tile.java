package student;


public class Tile {
    public int x_pos;
    public int y_pos;
    public int tile_type; // 0 - unknown, 1 - obstacle, 2 - free, 3 - depo, 4 - gold
    public int last_time; // -1 - on initialisation
    public boolean sensing; // false on initialisation
    public Tile(int x_pos, int y_pos, int tile_type, int last_time, boolean sensing){
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.tile_type = tile_type;
        this.last_time = last_time;
        this.sensing = sensing;
    }
    public int getX_pos(){
        return this.x_pos;
    }
    public int getY_pos(){
        return this.y_pos;
    }
    public int getTile_type(){
        return this.tile_type;
    }
    public int getLast_time(){
        return this.last_time;
    }
    public boolean isSensing(){
        return this.sensing;
    }
}
