package student;

import mas.agents.Message;

public class ComeHelpMessage extends Message {
    public int x_position;
    public int y_position;
    public ComeHelpMessage(int x_position, int y_position){
        this.x_position = x_position;
        this.y_position = y_position;
    }
}
