package student;

import mas.agents.Message;
import mas.agents.task.mining.Position;

import java.io.IOException;

public class PositionMessage extends Message {
    //public boolean sense;
    public int x_pos;
    public int y_pos;
    public int type;

    public PositionMessage (int x, int y, int type){
        this.x_pos = x;
        this.y_pos = y;
        this.type = type;
        //this.sense = sense;

    }
}
