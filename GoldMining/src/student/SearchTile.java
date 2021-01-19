package student;

public class SearchTile {
    public int x_pos;
    public int y_pos;
    public int tile_type; // 0 - unknown, 1 - obstacle, 2 - free, 3 - depo, 4 - gold
    public int f;
    public int g;
    public int h;
    public SearchTile parent;

    public SearchTile(int x_pos, int y_pos, int tile_type, int f, int g, int h, SearchTile parent){
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.tile_type = tile_type;
        this.f = f;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public SearchTile() {
        this.x_pos = -1;
        this.y_pos = -1;
        this.tile_type = 0;
        this.f = 0;
        this.g = 0;
        this.h = 0;
        this.parent = null;
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
    public int getF(){
        return this.f;
    }
    public int getH(){
        return this.h;
    }
    public int getG(){
        return this.g;
    }

}
