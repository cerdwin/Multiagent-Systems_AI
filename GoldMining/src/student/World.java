package student;

public class World {
    public int width;
    public int height;
    public int depo_x_position;
    public int depo_y_position;
    public int time;
    public Tile[][] surroundings;
    public World(int width, int height, int depo_x_position, int depo_y_position, int time){
        this.width = width;
        this.height = height;
        this.time = time;
        this.depo_x_position = depo_x_position;
        this.depo_y_position = depo_y_position;
        this.surroundings = new Tile[this.width][this.height];
        for(int i = 0; i < width; i++){
            for(int y = 0; y < height; y++){
                this.surroundings[i][y] = new Tile( i, y, 0, 0, false);
            }
        }
    }

    public void UpdateTile(PositionMessage m, int current_time){
        // TODO: here we update a single tile with time of last update, its type, whether we sense anything
        surroundings[m.x_pos][m.y_pos].tile_type = m.type;
        //surroundings[m.x_pos][m.y_pos].sensing = m.sense;
        surroundings[m.x_pos][m.y_pos].last_time = current_time;

    }
    public void ChangeTile(int x_pos, int y_pos, int type){
        surroundings[x_pos][y_pos].tile_type = type;
    }


}
