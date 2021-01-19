package student;

import mas.agents.Message;

import java.util.List;

public class RoleswapMessage extends Message {
    public int x_pos;
    public int y_pos;
    public boolean carrier;

    public RoleswapMessage(int x_pos, int y_pos, boolean carrier){
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.carrier = carrier;
    }
}
