package student;

import mas.agents.Message;

public class FoundMessage extends Message {
    public int x_pos;
    public int y_pos;
    public int discovery_type; // 1 - obstacle, 2 - free, 3 - depo, 4 - gold, 0 is unknown


    public FoundMessage(int x, int y, int discovery_type){
        this.x_pos = x;
        this.y_pos = y;
        this.discovery_type = discovery_type;
    }
}
