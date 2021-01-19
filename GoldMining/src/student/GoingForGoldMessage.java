package student;

import mas.agents.Message;

import java.util.List;

public class GoingForGoldMessage extends Message {
    public List<Pair<Integer, Integer>> goldenpath;
    public GoingForGoldMessage(List<Pair<Integer, Integer>> goldenpath){
        this.goldenpath = goldenpath;
    }
}
