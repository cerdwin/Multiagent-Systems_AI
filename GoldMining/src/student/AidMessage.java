package student;

import mas.agents.Message;

import java.util.List;

public class AidMessage extends Message {
    public boolean help_lift;
    public int x_pos;
    public int y_pos;
    //public List<Pair<Integer, Integer>> goldenpath;
    //public int[] x_coords;
    //public int[] y_coords;
    public AidMessage(int x, int y, boolean help_lift){
        this.x_pos = x; // coordinates of the position help is needed
        this.y_pos = y;
        this.help_lift = help_lift;
        //this.x_coords = x_coords;
        //this.y_coords = y_coords;
        //this.goldenpath = goldenpath;
    }

}
