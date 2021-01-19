package student;

import mas.agents.Message;

public class AidResponseMessage extends Message {
   // public boolean willing_2_help;
    //public boolean on_my_way;
    //public int path_lenght;
    //public boolean without_interference;
    public int x_pos;
    public int y_pos;
    public AidResponseMessage(int x_pos, int y_pos){
        /*this.willing_2_help = willing_2_help;
        this.on_my_way = on_my_way;
        this.path_lenght = path_lenght;
        this.without_interference = without_interference;*/
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }
}
