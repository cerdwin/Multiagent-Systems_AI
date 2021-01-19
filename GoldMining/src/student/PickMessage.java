package student;

import mas.agents.Message;

public class PickMessage extends Message {
    int x_pos;
    int y_pos;
    public PickMessage(int x_pos, int y_pos){
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }
}
