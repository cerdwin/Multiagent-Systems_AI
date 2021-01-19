package student;

import mas.agents.AbstractAgent;
import mas.agents.Message;
import mas.agents.SimulationApi;
import mas.agents.StringMessage;
import mas.agents.task.mining.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Agent extends AbstractAgent {
    public Agent(int id, InputStream is, OutputStream os, SimulationApi api) throws IOException, InterruptedException {
        super(id, is, os, api);
    }

    // See also class StatusMessage
    public static String[] types = {
            "", "obstacle", "depot", "gold", "agent"
    };
    // map of current agent
    World my_world;

    // agent's status telling us what he wants to do
    StatusMessage status;

    int time = 0;
    // if this is the agent in charge of carrying gold or not
    boolean carrier = false;
    // position of the depo
     Pair<Integer, Integer> depo = null;

    // my current location
     Pair<Integer, Integer> my_position;
    // my previous position
     Pair<Integer, Integer> my_previous;
    // my next position
     Pair<Integer, Integer> my_next_position;
    // my target position
     Pair<Integer, Integer> my_target_position = null;

    // positions where help is wanted
     HashSet<Pair<Integer,Integer>> help_wanted = new HashSet<>();

    // positions where gold is located
     HashSet<Pair<Integer,Integer>> gold_positions = new HashSet<>();

    // my path....is a list of steps which I am to take towards my goal
     List<Pair<Integer, Integer>> my_path;

     // Useful for communicating who's the best agent to come and help
    boolean aid_request_sent = false;
    boolean aid_response_sent = false;
    int best_helper = -1;
    int best_helper_distance = Integer.MAX_VALUE;
    boolean best_uninterfered = false;
    int my_boss = -1;
    int my_slave = -1;


    // my current role, which determines what I shall do
     AgentRole role;
     boolean has_gold = false;
     boolean someone_coming = false;

    @Override
    public void act() throws Exception {

        if( getAgentId() == 1)carrier = true;
        role = new ExploringRole(); // by default, agent starts off exploring his/her surroundings

        // TODO: inicializuj si mapu int width, int height, int depo_x_position, int depo_y_position, int time
        status = sense();
        time = 0; // we are starting the clock....consider changing this to system time
        my_world = new World(status.width,status.height, -1, -1, time);
        while(true) {
            if(carrier)log(String.format("//////////////It is my %d. day//////////////", time++));
            process_messages(); // takes messages from other agents and takes note of current state of the World




            //if( getAgentId() == 1)log(String.format("Done processing messages, now onto deciding my actions"));
            acting(); // this function determines what is going to happen next for said agent
           // if( getAgentId() == 1)log(String.format("Done acting, now I am going to sense"));
            see(); // updates information about the environment and sends it out to others
            //if( getAgentId() == 1)log(String.format("Finished sensing"));
            ///// end of my code /////
           // if( getAgentId() == 1)log(String.format("I am now on position [%d,%d] of a %dx%d map", status.agentX, status.agentY, status.width, status.height));
            //for(StatusMessage.SensorData data : status.sensorInput) {
                //if( getAgentId() == 1)log(String.format("I see %s at [%d,%d]", types[data.type], data.x, data.y));
            //}
            //////////
            // REMOVE THIS BEFORE SUBMITTING YOUR SOLUTION TO BRUTE !!
            //   (this is meant just to slow down the execution a bit for demonstration purposes)
            try {
                Thread.sleep(1);
            } catch(InterruptedException ie) {}
        }
    }
    public void process_messages() throws Exception {
        while (messageAvailable()) {
            Message m = readMessage();
            if(m instanceof HelpArrivedMessage){
                someone_coming = true;
                my_slave = m.getRecipient();
            }
            else if(m instanceof GoExploreMessage){
                log(String.format("AS ADVISED, Im off exploring"));
                if(my_boss == m.getSender()){
                    role = new ExploringRole();
                    my_target_position = closest_unknown(my_world);
                    my_path = a_star(status, my_world, my_target_position);
                }
            }
            else if(m instanceof PositionMessage){
                if(((PositionMessage) m).type == 3){ // Depo
                    depo = Pair.of(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos);
                    alter_map(((PositionMessage) m).x_pos,((PositionMessage) m).y_pos, 3);
                }else if (((PositionMessage) m).type == 4){ // Gold
                    update_gold(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos);
                    alter_map(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos, 4);
                }else if(((PositionMessage) m).type == 1){ // Obstacle
                    alter_map(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos, 1);
                    if(my_path != null && my_path.size() != 0){
                        if(my_path.contains(Pair.of(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos))){
                            // if we find an obstacle on our path, we recalculate our tracks
                            my_path = a_star(status, my_world, my_target_position);
                        }
                    }
                }else if(((PositionMessage) m).type == 2){ // Free
                    alter_map(((PositionMessage) m).x_pos, ((PositionMessage) m).y_pos, 2);
                }
            }
            else if(m instanceof AidMessage ){ // so I added here the condition that a carrier cannot answer
                if(((AidMessage) m).help_lift){
                    log("I received a message asking for help, so I send my coordinates");
                    if((!(role instanceof GoingHelpRole)) && !carrier){
                        sendMessage(m.getSender(), new AidResponseMessage(status.agentX, status.agentY));
                        aid_response_sent = true;
                    }
                }else{
                    if(carrier)log(String.format("ERROR: I received an Aid message in spite of being a carrier, this should not happen"));
                    log(String.format("I just received, that at the position [%d][%d], help is no longer needed, so Im off exploring", ((AidMessage) m).x_pos, ((AidMessage) m).y_pos));
                    if((role instanceof GoingHelpRole && my_target_position.first == ((AidMessage) m).x_pos && ((AidMessage) m).y_pos == my_target_position.second) || role instanceof ExploringRole || manhattan(my_target_position, Pair.of(status.agentX, status.agentY))==1){
                        log(String.format("Luckily, my role is either Going Help and at the right place, or an Explorer or I helped by accident. I should be good to leave now"));
                        aid_response_sent = false;
                        role = new ExploringRole();
                        my_target_position = closest_unknown(my_world);
                        if(my_target_position == null){
                            log(String.format("ERROR: Nowhere to go apparently, have to reset the world"));
                            broadcast(-1, new MapResetMessage());
                        }else{
                            my_path = a_star(status, my_world, my_target_position);
                            if(my_path.size() == 0){
                                status = showdirection(my_target_position, status);
                            }else{
                                my_next_position = my_path.get(0);
                                status = showdirection(my_next_position, status);
                                my_path.remove(0);
                            }
                        }
                    }
                }
            }
            else if(m instanceof AidResponseMessage){
                int temporary_distance = manhattan(Pair.of(status.agentX, status.agentY), Pair.of(((AidResponseMessage) m).x_pos, ((AidResponseMessage) m).y_pos));
                temporary_distance = bfs_distance(Pair.of(my_target_position.first, my_target_position.second), Pair.of(((AidResponseMessage) m).x_pos, ((AidResponseMessage) m).y_pos));
                if(temporary_distance == -1){
                    log(String.format("ERROR: APPARENTLY HE CANNOT GET HERE"));
                    continue;

                }

                if(getAgentId() == m.getSender() || !carrier){
                    log(String.format("ERROR: Someone is coming to help me, BUT I SHOULDNT NEED HELP or Somehow I wanted to help myself"));
                }else if( best_helper == -1 ||(best_helper_distance > temporary_distance)){
                    best_helper = m.getSender();
                    best_helper_distance = temporary_distance;
                }
            }
            else if (m instanceof RoleswapMessage){ // Only can be initiated by a carrier in a deadlock
                if(!carrier && status.agentX != ((RoleswapMessage) m).x_pos && status.agentY != ((RoleswapMessage) m).y_pos)continue;
                // TODO: message informs you someone wants to swap roles
                if(carrier && has_gold){
                    status = drop();
                    broadcast(getAgentId(), new PositionMessage(status.agentX, status.agentY, 4));
                    alter_map(status.agentX, status.agentY, 4);
                    gold_positions.add(Pair.of(status.agentX, status.agentY));
                }
                sendMessage(m.getSender(), new RoleswapMessage(status.agentX, status.agentY, carrier));
                role = new ExploringRole();
                aid_request_sent = false;
                aid_response_sent = false;
                best_uninterfered = false;
                someone_coming = false;
                my_target_position = null;
                my_path = null;
                carrier = ((RoleswapMessage) m).carrier;
            }
            else if ( m instanceof MapResetMessage){
                if(closest_unknown(my_world) == null){
                    empty_world();
                }
            }
            else if(m instanceof PickMessage){ // We know gold has been picked at this position, so we remove it from our records
                alter_map(((PickMessage) m).x_pos, ((PickMessage) m).y_pos, 0);
                gold_positions.remove(Pair.of(((PickMessage) m).x_pos, ((PickMessage) m).y_pos));
            }
            else if(m instanceof ComeHelpMessage){ // this is a command, where a carrier tells its closest colleague to help him
                my_target_position = Pair.of(((ComeHelpMessage) m).x_position, ((ComeHelpMessage) m).y_position);
                my_path = a_star(status, my_world, my_target_position);
                role = new GoingHelpRole();
                my_boss = m.getSender();
                aid_request_sent = false;
                log(String.format(">>>>>>>>>>>>>>>> I have been commanded to go help at %d %d, so I shall go", my_target_position.first, my_target_position.second));
            }
            else{
               log("ERROR: Some weird message found");
            }
        }
        // This is done to determine which of agents is closest
        if((role instanceof  GoingForGoldRole) && best_helper != -1 && carrier && !aid_request_sent){ // only a carrier can do this...I worked out who's coming to help, so I'm calling him
            //someone_coming = true;
            // send message to the one who you have chosen
            log(String.format("My best helper is coming %d, and I am %d ",best_helper, getAgentId() ));
            sendMessage(best_helper, new ComeHelpMessage(my_target_position.first, my_target_position.second));
            aid_request_sent = true;
            best_uninterfered = false;
            best_helper_distance = Integer.MAX_VALUE;

        }
    }
    public void see()throws Exception{
        status = sense();
        if(my_world.surroundings[status.agentX][status.agentY].tile_type == 0){ // if I didn't know it was free where I'm standing
            my_world.surroundings[status.agentX][status.agentY].tile_type = 2;
            my_world.ChangeTile(status.agentX, status.agentY, 2);
            broadcast(getAgentId(), new PositionMessage(status.agentX, status.agentY, 2));
        }
        ArrayList<Pair<Integer, Integer>> directions = new ArrayList<>(8);
        directions.add(0, Pair.of(status.agentX-1, status.agentY));
        directions.add(1, Pair.of(status.agentX+1, status.agentY));
        directions.add(2, Pair.of(status.agentX, status.agentY-1));
        directions.add(3, Pair.of(status.agentX-1, status.agentY+1));
        directions.add(4, Pair.of(status.agentX-1, status.agentY-1));
        directions.add(5, Pair.of(status.agentX+1, status.agentY-1));
        directions.add(6, Pair.of(status.agentX-1, status.agentY+1));
        directions.add(7, Pair.of(status.agentX+1, status.agentY+1));

        for(StatusMessage.SensorData info : status.sensorInput){
            /*
             OBSTACLE = 1;
             DEPOT = 2;
             GOLD = 3;
             AGENT = 4;
             */
            // checking where are information coming from
            for(int i = 0; i < 8; i++){
                if(info.x == directions.get(i).first && info.y == directions.get(i).second){
                    directions.set(i, Pair.of(-1, -1));
                    //if(carrier)log(String.format("ok..."));
                }
            }
            if(info.type == 1){
                if(my_world.surroundings[info.x][info.y].tile_type != 1){ // if I didn't know it is an obstacle
                    broadcast(getAgentId(), new PositionMessage(info.x, info.y, 1));
                    alter_map(info.x, info.y, 1);
                }
                if(my_path != null && my_path.contains(Pair.of(info.x, info.y))){ // if this obstacle stands in my way
                    //TODO: recalculate your path, reconsider target
                    my_path = a_star(status, my_world, my_target_position);
                    if(my_path.size() == 0 || my_path == null){
                        if(carrier)log("Error: cannot find an alternative path");
                    }
                }
            }
            else if(info.type == 2 && my_world.surroundings[info.x][info.y].tile_type != 2){ // DEPO
                if (depo == null) {
                    broadcast(getAgentId(), new PositionMessage(info.x, info.y, 3));
                    // 1 - obstacle, 2 - free, 3 - depo, 4 - gold, 0 is unknown
                    alter_map(info.x, info.y, 3);
                    depo = Pair.of(info.x, info.y);
                }
            }
            else if(info.type == 3 && my_world.surroundings[info.x][info.y].tile_type != 3){ // gold on an undiscovered position
                if(!gold_positions.contains(Pair.of(info.x, info.y))){ // hash set doesn't already include this gold
                    broadcast(getAgentId(), new PositionMessage(info.x, info.y, 4));
                    gold_positions.add(Pair.of(info.x, info.y));
                }
                alter_map(info.x, info.y, 4);
            }
            else if(info.type == 4){ // agent
                if(my_path == null || my_target_position == null){
                    log(String.format("ERROR: DODGING"));
                    status = random_direction();
                }
                else if(my_path.size() == 0 || (my_target_position.first == info.x && my_target_position.second == info.y)){
                   if((!(role instanceof GoingHelpRole)) && !carrier){
                       log(String.format("ERROR: DODGING"));
                       if(my_target_position.first == info.x && my_target_position.second == info.y)log(String.format("I AM GOING OFF OF MY TARGET HERE"));
                       status = random_direction();
                   }
                } // that person stands in my way
                else if((my_path.get(0).first == info.x && my_path.get(0).second == info.y) && ((!(role instanceof GoingHelpRole)) && !carrier)){ // no point of looking whether this tile appears at a later point or now, since we should be moving right to it
                    log(String.format("THERE IS SOMEONE IN MY WAY"));
                    int temp_tile = my_world.surroundings[info.x][info.y].tile_type; // we shall save whatever tile type the agent is standing on
                    alter_map(info.x, info.y, 1);/// let us pretend this is an obstacle
                    List<Pair<Integer, Integer>> alternative_path  = a_star(status, my_world, my_target_position);
                    alter_map(info.x, info.y, temp_tile); // change the tile type back
                    if(alternative_path == null)log(String.format("So I added an obstacle at position %d %d and it is impossible to get to %d %d?", info.x, info.y, my_target_position.first, my_target_position.second));
                    if(alternative_path != null && manhattan(Pair.of(status.agentX, status.agentY), Pair.of(my_target_position.first, my_target_position.second)) >1){
                        if(carrier)log(String.format("Luckily, I found an Alternative Path"));
                        Random rand = new Random();
                        int random_int = rand.nextInt(1000);
                        if(random_int%4 == 0){
                            my_path = alternative_path;
                            my_next_position = my_path.get(0);
                            status = showdirection(my_next_position, status);
                            my_path.remove(0);
                        }else{
                            status = random_direction();
                        }
                    }
                    else if (carrier){ // I cannot get to my goal otherwise, so I must swap roles
                        log("SWAPPING ROLES");
                        //TODO: if that doesn't work out, try to see if there arent' any different places you could go to avoid the other person, and with 50percent chance go there...
                        my_world.surroundings[info.x][info.y].tile_type = 1; // let us pretend this is an obstacle
                        my_world.ChangeTile(info.x, info.y, 1);
                        List<Pair<Integer, Integer>> escaping_places = free_neighbouring_positions(status, my_world);
                        my_world.surroundings[info.x][info.y].tile_type = 2; // let us stop pretending
                        my_world.ChangeTile(info.x, info.y, 2);
                        if(escaping_places.size() <= 1){ // we're probably in a tunnel and can only go back.
                            // change status to roleswap
                            role = new RoleswapRole();
                            // send a message, an instance of roleswap .... lateron, if you receive a roleswap message from another agent right next to you, you swap my_world, status, role, target, target path, carrier status....before that you drop your gold if you have it and change your status to searching
                            broadcast(getAgentId(), new RoleswapMessage(info.x, info.y, carrier));
                        }else{
                            // TODO: My last resort to solve a collision is to swap roles with the agent colliding and in case of holding a piece of gold, drop it.
                            Random rand = new Random();
                            int random_int = rand.nextInt(1000);
                            my_target_position = escaping_places.get(random_int%escaping_places.size()); // move to random position
                            my_path = a_star(status, my_world, my_target_position);
                        }
                    }
                }
            }
        }
        for(int i = 0; i < 8; i++){
            if(directions.get(i).first != -1 && directions.get(i).first >= 0 && directions.get(i).first < status.width && directions.get(i).second >= 0 && directions.get(i).second<status.height){
                if(my_world.surroundings[directions.get(i).first][directions.get(i).second].tile_type == 0){ // if this part was unknown
                    my_world.surroundings[directions.get(i).first][directions.get(i).second].tile_type = 2;
                }
            }
        }
        reset_gold();
    }

    private void acting() throws Exception{

        if(role instanceof ExploringRole){
            log(String.format("Iam an explorer at [%d,%d]", status.agentX, status.agentY));
            if(carrier && has_gold && depo != null){ // A carrier, carrying gold, who can finally go to depo to drop it off
                //log(String.format("We finally found depo and I can carry my gold there"));
                role = new GoingToDepotRole();
                my_target_position = depo;
                my_path = a_star(status, my_world, depo);
                if (my_target_position == null) {
                    log(String.format("ERROR: DEPO IS NULL"));
                }
                if(my_path == null){
                    log(String.format("ERROR: CANNOT FIND A PATH FROM (%d, %d) TO DEPO AT (%d, %d)", status.agentX, status.agentY, depo.first, depo.second));
                }else if(my_path.size() == 0){
                    status = showdirection(my_target_position, status);
                }else{
                    my_next_position = my_path.get(0);
                    my_path.remove(0);
                    status = showdirection(my_next_position, status);
                }
            }
            else if(carrier && !gold_positions.isEmpty()){ // i.e. I have nowhere particular to go yet, but there is gold to be picked
                my_target_position = closest_gold();
                if(my_target_position == null){ // we have a problem, cannot reach gold by any means
                    log(String.format("ERROR: I would like to reach gold, but I cannot"));// shouldn't happen...let's see
                    gold_positions = new HashSet<>();
                    int x_gold = Integer.MAX_VALUE;
                    int y_gold = Integer.MAX_VALUE;
                    int distance = Integer.MAX_VALUE;
                    for(int i = 0; i < my_world.width; i++){
                        for(int x = 0; x < my_world.height; x++){
                            if(my_world.surroundings[i][x].tile_type == 4){
                                gold_positions.add(Pair.of(i, x));
                                if(x_gold == Integer.MAX_VALUE || (manhattan(Pair.of(i, x), Pair.of(status.agentX, status.agentY))< distance)){
                                    distance = manhattan(Pair.of(x_gold, y_gold), Pair.of(status.agentX, status.agentY));
                                    x_gold = i;
                                    y_gold = x;
                                }
                            }
                        }
                    }
                    my_target_position =  Pair.of(x_gold, y_gold);
                    if(my_target_position == null)log(String.format("ABORT ABORT ABORT"));
                }// I have a new target and want to find best way towards it and let others know
                if(carrier) log(String.format("Im becoming a gold digger and I have gold to dig"));
                role = new GoingForGoldRole();
                log(String.format("Im a_staring to my gold... from [%d, %d] to [%d, %d]", status.agentX, status.agentY, my_target_position.first, my_target_position.second));
                if(null == ( my_path = a_star(status, my_world, my_target_position)))status = random_direction();
                else if(my_path.size() != 0){ // i.e. uz tam jsem
                    // nasla jsem si nove zlato a jdu tam
                    my_next_position = my_path.get(0);
                    my_path.remove(0);
                    status = showdirection(my_next_position, status); // decide my next step
                }
            }else{
                if(my_target_position == null){ // have to get a new target position...because Im helping only when immediately asked ...so I deal with helping in the message handling section
                    my_target_position = closest_unknown(my_world);
                    if(my_target_position == null){ // we have a problem, cannot reach unexplored position by any means
                        broadcast(-1, new MapResetMessage()); // I think it is better for all to do it at once
                    }
                    else{
                        my_path = a_star(status, my_world, my_target_position);
                        if(my_path == null){
                            log(String.format("ERROR: NO PATH LEADS TO AN UNKNOWN"));
                        }
                        else if(my_path.size() == 0){
                            my_target_position = null;
                            my_path = null;
                            status = random_direction();
                        }
                        else{
                            my_next_position = my_path.get(0);
                            my_path.remove(0);
                            status = showdirection(my_next_position, status); // decide my next step
                        }

                    }
                }else if(my_target_position.first == status.agentX && my_target_position.second == status.agentY || my_path.size() == 0){/* I arrived at my target position */
                    if(carrier)log(String.format("I have arrived at my new target position, setting my new target to random unknown"));
                    my_target_position = closest_unknown(my_world);
                    if(my_target_position == null){
                        log(String.format("WE HAVE A NULL TARGET HERE"));
                        status = random_direction();
                        broadcast(-1, new MapResetMessage());
                    }
                    else{
                        my_path = a_star(status, my_world, my_target_position);
                        if(my_path == null){
                            log(String.format("ERROR: DIDNT FIND ANY PATHS LEADING TO AN UNKNOWN"));
                        }else if (my_path.size() == 0){
                            my_target_position = null;
                            my_path = null;
                            status = showdirection(my_target_position, status);
                        }else{
                            my_next_position = my_path.get(0);
                            my_path.remove(0);
                            status = showdirection(my_next_position, status); // decide my next step
                        }
                    }
                }else{ // I have a target position and Im going towards it
                    if( getAgentId() == 1)log(String.format("I havent got a null target, nor am I at my desired target...so I must be going towards that place"));
                    my_next_position = my_path.get(0);
                    my_path.remove(0);
                    status = showdirection(my_next_position, status); // decide my next step
                }

            }
        }
        else if(role instanceof DroppingRole){
            if( getAgentId() == 1)log(String.format(" ***Im ready to drop ***"));
            if(has_gold){
                has_gold = false;
                someone_coming = false;
                aid_request_sent = false;
                status = drop();
                role = new ExploringRole();
                my_target_position = null;
                my_path = null;
            }else{
                if(carrier)log(String.format("ERROR: Why are you dropping things, if you're not holding anything?"));
            }
        }
        else if(role instanceof HelpingRole){
            log(String.format("***I want to go help someone at [%d, %d]***", my_target_position.first, my_target_position.second));
            // pouzij astar aby nasel jeden ze sousednich policek u goldu...zkontroluj, ze nikdo nezavazi
            if(my_target_position == null){
                log(String.format("I'm supposed to be helping but forgot where"));
            }else if(my_path.size() == 0){
                log(String.format("Im A*** my path to help..."));
                my_path = a_star(status, my_world, my_target_position);
            }else { // Im on my way
                    my_next_position = my_path.get(0);
                    my_path.remove(0);
                    status = showdirection(my_next_position, status);
            }

        }
        else if(role instanceof GoingForGoldRole){
            /***** I make sure I'm pursuing the closest Gold *****/
            Pair<Integer, Integer> temp = closest_gold();
            if(temp != null){
                if(temp.first != my_target_position.first && temp.second != my_next_position.second){
                    log(String.format("Changing goal from %d, %d to %d %d, because I'm at %d %d", my_target_position.first, my_target_position.second, temp.first, temp.second, status.agentX, status.agentY));
                    my_target_position = temp;
                    my_path = a_star(status, my_world, my_target_position);
                }
            }
            else{
                log(String.format("ERROR: NO GOLD AVAILABLE"));
                boolean gold_found = false;
                for(int i = 0; i < status.height; i++){
                    for(int x = 0; x < status.width; x++){
                        if(my_world.surroundings[i][x].tile_type == 4){
                            gold_found = true;
                            break;
                        }
                    }
                }
                if(gold_found){
                    log(String.format("BUT WE KNOW THE MAP CONTAINS GOLD"));
                }else{
                    log(String.format("IT SEEMS TO NOT BE IN THE MAP EITHER"));
                }
                my_world.surroundings[13][13].tile_type = -2;
                return;
            }
            /******************************************************/

            log(String.format("*****I want to get gold at [%d][%d] from [%d][%d] *****", my_target_position.first, my_target_position.second, status.agentX, status.agentY));
            if((my_target_position.first == status.agentX && my_target_position.second == status.agentY) && my_world.surroundings[status.agentX][status.agentY].tile_type == 4){/*uz jsem tam*/
                log(String.format("*****Im already at the place of gold, ready to pick it up when my boy best helper %d comes", best_helper));
                if(has_gold){
                    role = new ExploringRole();
                    return;
                }
                if(someone_coming){
                    log(String.format("OFF PICKING"));
                    role = new PickingUpRole();
                    return;
                }else if(best_helper != -1){
                    //sendMessage(best_helper, new ComeHelpMessage(status.agentX, status.agentY));
                    //return;
                }
                if(!status.isAtGold()){
                    broadcast(getAgentId(), new PickMessage(status.agentX, status.agentY));
                    alter_map(status.agentX, status.agentY, 2);
                    gold_positions.remove(Pair.of(status.agentX, status.agentY));
                }
                else if(!aid_request_sent) { // if nobody is coming, call them
                    log(String.format("ERROR:*****Aid Message sent"));
                    //aid_request_sent = true;
                    broadcast(getAgentId(), new AidMessage(status.agentX, status.agentY, true));
                }
                else{
                    log(String.format("ERROR: UNEXPECTED RESULT"));
                }

                // changing role to picking up and trying to pick up until someone comes
            }else if(my_target_position != null && my_path.size() != 0 && my_world.surroundings[my_target_position.first][my_target_position.second].tile_type == 4){ // I haven't reached gold yet
                //if( carrier)log(String.format("*****I am approaching gold"));
                status = showdirection(my_path.get(0), status); // progress towards next step on your target
                my_path.remove(0);
            }
            else{
                if(carrier)log(String.format("ERROR: Im going for gold but don't know where? That's sus"));
                    my_target_position = closest_gold();
                    if(my_target_position == null){
                        if(carrier)log(String.format("ERROR: COULD NOT FIND A VIABLE GOAL"));
                    }
                if(my_target_position != null){
                    my_path = a_star(status, my_world, my_target_position);
                    if(my_path == null){
                        if(carrier)log(String.format("ERROR: NO WAY FOUND TOWARDS GOLD FROM %d %d to %d, %d", status.agentX, status.agentY, my_target_position.first, my_target_position.second));
                    }else if(manhattan(Pair.of(my_target_position.first, my_target_position.second), Pair.of(status.agentX, status.agentY)) == 1 ){
                        status = showdirection(my_target_position, status);
                    }
                    else {
                        my_next_position = my_path.get(0);
                        status = showdirection(my_next_position, status); // progress towards next step on your target
                        my_path.remove(0);
                    }
                }
            }
        }
        else if(role instanceof GoingHelpRole){
            log(String.format("*****Im going help sb at [%d][%d], my current is [%d][%d]*****", my_target_position.first, my_target_position.second, status.agentX, status.agentY));
            //log(String.format("My path there is:"));
            for(int i = 0; i < my_path.size(); i++){
                //log(String.format("%d %d ", my_path.get(i).first, my_path.get(i).second));
            }
            if(my_target_position == null){/* nemam target...i.e. nevim kam jdu */
                log(String.format("ERROR: This is a problem, Im supposed to help and dont know where"));
            }else if((Math.abs(status.agentX-my_target_position.first)+Math.abs(status.agentY-my_target_position.second)) == 1) {//mam target a uz jsem na spravny pozici aby druhy mohl zvedat
                //log(String.format("I came here to (%d %d )be close to (%d %d)help and will stay here until %d let me know  you do not need me", status.agentX, status.agentY, my_target_position.first, my_target_position.second, my_boss));

                //log(String.format("MY BOSS IS:%d", my_boss));
                if(my_boss != -1){
                    log(String.format("My boss is %d and I informed him im here", my_boss));
                    sendMessage(my_boss, new HelpArrivedMessage());
                    //my_boss = -1;
                }

                if(status.isAtGold() && ((my_target_position.first != status.agentX)|| (my_target_position.second != status.agentY))){
                   // status = random_direction(); I'm standing on the tile...I should back off zkontroluj jestli vedle taky neni zlato a ty pomahac
                }
                if(my_world.surroundings[my_target_position.first][my_target_position.second].tile_type != 4){ // I guess the gold has been picked and I'm not needed
                    //role = new ExploringRole();
                }
            }else if(my_target_position.first != status.agentX || my_target_position.second != status.agentY) {//mam target a jeste tam nejsem...jdu dal
                my_path = a_star(status, my_world, my_target_position);
                my_next_position = my_path.get(0);
                my_path.remove(0);
                status = showdirection(my_next_position, status);
            }
        }
        else if(role instanceof PickingUpRole){
            if(!status.isAtGold()){  // no gold here...
                role = new GoingForGoldRole();
                gold_positions.remove(Pair.of(status.agentX, status.agentY));
                broadcast(getAgentId(), new PickMessage(status.agentX, status.agentY));
                return;
            }
            log(String.format("***I want to pick up...***"));
            /*boolean someone_to_help = false;
            for(StatusMessage.SensorData info : status.sensorInput){
                if(info.type == 4 && ((Math.abs(info.x-status.agentX)+Math.abs(info.y-status.agentY)) == 1)){
                    someone_to_help = true;
                }
            }*/
            if(/*someone_to_help &&*/ someone_coming){
                if(carrier)log(String.format("***I should be able to pick up and send explore msg to %d", best_helper));
                status = pick();
                // send helper packing
                sendMessage(best_helper, new GoExploreMessage());
                best_helper = -1;
                has_gold = true;
                role = new GoingToDepotRole();
                my_target_position = depo;
                someone_coming = false;
                aid_request_sent = false;
                // broadcast to others help is no longer needed
                log(String.format("***I have picked gold up, no need for more help"));

                if(!status.isAtGold()){
                    broadcast(getAgentId(), new PositionMessage(status.agentX, status.agentY, 2));
                    gold_positions.remove(Pair.of(status.agentX, status.agentY));
                    alter_map(status.agentX, status.agentY, 2);
                    broadcast(getAgentId(), new PickMessage(status.agentX, status.agentY));
                }

                if(depo == null){ // depo not yet discovered
                    role = new ExploringRole();
                    my_target_position =  closest_unknown(my_world);
                    my_path = a_star(status, my_world, my_target_position);
                }else{
                    my_path = a_star(status, my_world, depo);
                }
            }else{
                // nobody is next to you, so I guess you chill
                if( carrier)log(String.format("I will try to chill here until someone comes and helps me"));
                //role = new GoingForGoldRole();
            }
            // pick up and remove from gold array

    }
        else if (role instanceof GoingToDepotRole){
            if( carrier)log(String.format("Im off to depo"));
            if((status.agentX == depo.first && status.agentY == depo.second) || !has_gold ){ // I have arrived at depo or if I do not have gold
                status = drop(); // I drop my gold
                has_gold = false;
                my_target_position = null;
                my_path = null;
                role = new ExploringRole();
                someone_coming = false;
                aid_request_sent = false;

            } else if(status.agentX != depo.first || status.agentY != depo.second){ // I haven't reached depo yet
                if(my_target_position.first != depo.first || my_target_position.second != depo.second){
                    my_target_position = depo;
                    my_path = a_star(status, my_world, depo);
                }
                if(my_path == null || my_path.size() == 0){
                    if( carrier)log(String.format("My path is zero"));
                    my_path = a_star(status, my_world, depo);
                    my_next_position = my_path.get(0);
                    status = showdirection(my_path.get(0), status);
                }else{
                    my_next_position = my_path.get(0);
                    status = showdirection(my_next_position, status);
                    my_path.remove(0);
                }
            }else{
                if( getAgentId() == 1)log(String.format("A bit weird to go to depo, yet not go there"));
            }

    }
        else if (role instanceof WaitingRole){
            if(carrier)log(String.format("Some kind of a weird ..... WAITING ROLE???"));
        }
        else if(role instanceof WaitingForHelpRole){
            if(carrier)log(String.format("Some kind of a weird ......WAITING FOR HELP ROLE???"));
        }
        else{
            if(carrier)log(String.format("I GIVE UP..."));
        }


    }
    ///////////// Helper Functions /////////////
    public void broadcast(long id, Message msg) throws Exception{
        /**
         * Sends a particular message to all agents except himself
         */
        for(int i = 1; i < 5; i++){
            if(id != i){
                sendMessage(i, msg);
            }
        }
    }
    public StatusMessage random_direction() throws Exception {
        boolean left = false;
        boolean right = false;
        boolean up = false;
        boolean down = false;
        if(status.agentX-1 > -1 && my_world.surroundings[status.agentX-1][status.agentY].tile_type != 1){
            left = true;
        }if(status.agentY-1 > -1 && my_world.surroundings[status.agentX][status.agentY-1].tile_type != 1){
            up = true;
        }if(status.agentY+1 < status.height && my_world.surroundings[status.agentX][status.agentY+1].tile_type != 1){
            down = true;
        }if(status.agentX+1 < status.width && my_world.surroundings[status.agentX+1][status.agentY].tile_type != 1){
            right = true;
        }
        Random rand = new Random();
        int random_int = rand.nextInt(1000);
        if(random_int % 4 == 1 && left){
            log(String.format("Randomly going left"));
            return left();
        }else if(random_int % 4 == 2 && right){
            log(String.format("Randomly going right"));
            return right();
        }else if(random_int % 4 == 3 && up){
            log(String.format("Randomly going up"));
            return up();
        }else if(random_int % 4 == 0 && down){
            log(String.format("Randomly going down"));
            return down();
        }else{
            return random_direction();
        }

    }
    public void alter_map(int x_pos, int y_pos, int tile_type){
        my_world.surroundings[x_pos][y_pos].tile_type = tile_type;
        my_world.ChangeTile(x_pos, y_pos, tile_type);
    }
    public void update_gold(int x_pos, int y_pos){
        if(gold_positions.contains(Pair.of(x_pos, y_pos)))return;
        gold_positions.add(Pair.of(x_pos, y_pos));
    }
    public void print_world()throws Exception{
        for(int x = 0; x < status.height; x++){
            for(int i = 0; i < status.width; i++){
                log(String.format("%d ", my_world.surroundings[i][x].tile_type));
            }
            log('\n');
        }
    }
    public void reset_gold(){
        if(true)return;
        HashSet<Pair<Integer,Integer>> NewGold = new HashSet<>();
        /*for(int i = 0; i < status.width; i++){
            for (int w = 0; w < status.height; w++){
                //if(my_world.surroundings[i][w].tile_type == 4)NewGold.add(Pair.of(i, w));

            }
        }
        gold_positions = NewGold;*/
        for(int i = 0; i < gold_positions.size(); i++){
            Pair<Integer,Integer> next_position = gold_positions.iterator().next();
            if(my_world.surroundings[my_next_position.first][my_next_position.second].tile_type != 4){
                my_world.surroundings[my_next_position.first][my_next_position.second].tile_type = 4;
            }
        }
        return;
    }
    public int manhattan (Pair<Integer,Integer> origin, Pair<Integer,Integer> destination){
        // Just a function working out Manhattan distance between two pairs of coordinates
        int result = Math.abs(origin.first-destination.first) + Math.abs(origin.second-destination.second);
        return result;
    }
    public Queue <Pair<Integer,Integer>> update_neighbours( Queue <Pair<Integer,Integer>> my_queue, World my_world, int current_X, int current_Y, int [][] map){
        /**
         This method takes in a queue of neighbouring tiles discovered by bfs, expands head of the queue and adds new ones

         */
        if(current_X-1>-1 ){ // left
            if(my_world.surroundings[current_X-1][current_Y].tile_type != 1 && map[current_X-1][current_Y] != -1){
                my_queue.add(Pair.of(current_X-1,current_Y));
            }
        }if(current_X+1< status.width ){ //right
            if(my_world.surroundings[current_X+1][current_Y].tile_type != 1 && map[current_X+1][current_Y] != -1){
                my_queue.add(Pair.of(current_X+1,current_Y));
            }
        }if(current_Y-1> 0 ){ //up
            if(my_world.surroundings[current_X][current_Y-1].tile_type != 1 && map[current_X][current_Y-1] != -1){
                my_queue.add(Pair.of(current_X,current_Y-1));
            }
        }if(current_Y+1< status.height ){ //down
            if(my_world.surroundings[current_X][current_Y+1].tile_type != 1 && map[current_X][current_Y+1] != -1){
                my_queue.add(Pair.of(current_X,current_Y+1));
            }
        }
        return my_queue;
    }
    public int bfs_distance(Pair<Integer, Integer> target, Pair<Integer, Integer> helper) throws Exception {
        World temp_world = my_world;
        for(int i = 0; i < my_path.size(); i++){
            temp_world.surroundings[my_path.get(i).first][my_path.get(i).second].tile_type = 1;
        }
        int steps = 1;
        Queue <Pair<Integer,Integer>> my_queue = new ArrayDeque<> ();
        int [][] map = new int[status.width][status.height];
        for(int i = 0; i < status.width; i++){
            for(int y = 0; y < status.height; y++){
                map[i][y] = 0;
            }
        }
        my_queue = update_neighbours(my_queue, temp_world, target.first, target.second, map);
        while(!my_queue.isEmpty()){
            Pair<Integer,Integer> new_position = my_queue.remove();
            steps++;
            map[new_position.first][new_position.second] = -1; // mark as expanded
            if(new_position.first == helper.first && new_position.second == helper.second){
                return steps;
            }
            my_queue = update_neighbours(my_queue, my_world, new_position.first, new_position.second, map);
        }
        log(String.format("ERROR WITH BFS DISTANCE %d %d to %d %d", target.first, target.second, helper.first, helper.second));
        return -1;
    }
    public void empty_world(){
        /**
         * Function used to mark all free discovered positions in map as empty to encourage further exploration
         */
        for(int i = 0; i < my_world.width; i++){
            for(int x = 0; x < my_world.height; x++){
                if(my_world.surroundings[i][x].tile_type == 2)my_world.surroundings[i][x].tile_type = 0;
            }
        }
    }
    public Pair<Integer, Integer> closest_gold() throws Exception {
        /**
         * This function takes in agent's position and through BFS finds him the closest gold position
         */

        if( my_world.surroundings[status.agentX][status.agentY].tile_type == 4){
            log(String.format("YOU ARE ALREADY ON GOLD"));
            return Pair.of(status.agentX, status.agentY);
        }
        Queue <Pair<Integer,Integer>> my_queue = new ArrayDeque<> ();
        int current_X = status.agentX;
        int current_Y = status.agentY;
        int [][] map = new int[status.width][status.height];
        for(int i = 0; i < status.width; i++){
            for(int y = 0; y < status.height; y++){
                map[i][y] = 0;
            }
        }
        map[current_X][current_Y] = -1;
        my_queue = update_neighbours(my_queue, my_world, current_X, current_Y, map);
        while(!my_queue.isEmpty()){
            Pair<Integer,Integer> new_position = my_queue.remove();
            current_X = new_position.first;
            current_Y = new_position.second;
            map[current_X][current_Y] = -1; // mark as expanded
            if(my_world.surroundings[current_X][current_Y].tile_type == 4 ){
                //print_world();
                return new_position;
            }
            my_queue = update_neighbours(my_queue, my_world, current_X, current_Y, map);
        }
        log(String.format("ERROR: Sorry mate, but a path towards any gold has not been found. You are at [%d][%d] and want to get to [%d][%d]", status.agentX, status.agentY, gold_positions.iterator().next().first,gold_positions.iterator().next().second ));
        //print_world();
        if (carrier) {
            log(String.format("ERROR: NOT Sending whichever gold remains in our records"));
            //return gold_positions.iterator().next();
        }


        return null;
    }
    public Pair<Integer, Integer> closest_unknown( World my_world) throws IOException{
        /**
         * A function similar to closest_gold, generates position of the closest unexplored tile
         **/
        Queue <Pair<Integer,Integer>> my_queue = new ArrayDeque<Pair<Integer,Integer>>();
        int current_X = status.agentX;
        int current_Y = status.agentY;
        int [][] map = new int[status.width][status.height];
        for(int i = 0; i < status.width; i++){
            for(int y = 0; y < status.height; y++){
                map[i][y] = 0;
            }
        }
        my_queue = update_neighbours(my_queue, my_world, current_X, current_Y, map);
        while(!my_queue.isEmpty()){
            Pair<Integer,Integer> new_position = my_queue.remove();
            current_X = new_position.first;
            current_Y = new_position.second;
            map[current_X][current_Y] = -1;
            if(my_world.surroundings[current_X][current_Y].tile_type == 0){// if the tile is unknown
                return new_position;
            }
            my_queue = update_neighbours(my_queue, my_world, current_X, current_Y, map);
        }
        return null;
    }
    public SearchTile update_offspring(int x_diff, int y_diff, SearchTile current_position, Pair<Integer, Integer> my_target_position)throws Exception{
        /***
         * Helper function for A* algorithm, updating tiles with heuristics
         *
         */
        SearchTile temp = new SearchTile();
        temp.x_pos = current_position.x_pos + x_diff;
        temp.y_pos = current_position.y_pos + y_diff;
        temp.parent = current_position;
        temp.tile_type = my_world.surroundings[temp.x_pos][temp.y_pos].tile_type;

        temp.g = temp.parent.g+manhattan(Pair.of(temp.x_pos,temp.y_pos), Pair.of(temp.parent.x_pos, temp.parent.y_pos));
        temp.h = manhattan(Pair.of(temp.x_pos,temp.y_pos), my_target_position);
        temp.f = temp.g+temp.h;
        return temp;
    }
    public List <Pair<Integer, Integer>> backtrace2gold( StatusMessage status, Pair<Integer, Integer> my_target_position, Pair<Integer, Integer> [][] parents) throws Exception {
        /**
         returns a list of steps towards a goal
         **/
        Pair<Integer, Integer> curr = my_target_position;
        ArrayList <Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
        while(curr.first != status.agentX || curr.second != status.agentY){
            result.add(0, new Pair<Integer, Integer>(curr.first, curr.second));
            curr = parents[curr.first][curr.second];
        }
        return result;
    }
    public List<Pair<Integer, Integer>> free_neighbouring_positions(StatusMessage status, World my_world) throws Exception{
        /**
         *Function returning pairs consisting of coordinates an agent can immediately move to
         **/
        List<Pair<Integer, Integer>> neighbours = new ArrayList<>();

        if(status.agentX+1 < status.width){
            if(my_world.surroundings[status.agentX+1][status.agentY].tile_type != 1)neighbours.add(Pair.of(status.agentX+1, status.agentY));
        }
        if(status.agentY+1 < status.height){
            if(my_world.surroundings[status.agentX][status.agentY+1].tile_type != 1)neighbours.add(Pair.of(status.agentX, status.agentY+1));
        }
        if(status.agentX-1 >= 0){
            if(my_world.surroundings[status.agentX-1][status.agentY].tile_type != 1)neighbours.add(Pair.of(status.agentX-1, status.agentY));
        }
        if(status.agentY-1 >= 0){
            if(my_world.surroundings[status.agentX][status.agentY-1].tile_type != 1)neighbours.add(Pair.of(status.agentX, status.agentY-1));
        }
        return neighbours;
    }
    public List <Pair<Integer, Integer>> a_star(StatusMessage status, World my_world, Pair<Integer, Integer> my_target_position) throws Exception {
        /**
         * Rather self-explanatory, an A* algorithm function, returning a list of steps agent has to take to reach its goal
         **/
        if(true)return new_astar(status, my_world, my_target_position);
        else{
            //log(String.format("My target is :[%d,%d]", my_target_position.first, my_target_position.second));
        }
        List <SearchTile> gold_path = null; // first pair are coordinates, second are g and h
        // initialise open List
        Comparator<SearchTile> comparator = new SearchTileComparator();
        PriorityQueue <SearchTile> open_list = new PriorityQueue<SearchTile>(comparator);
        PriorityQueue <SearchTile> closed_list = new PriorityQueue<SearchTile>(comparator);
        SearchTile current_position = new SearchTile(status.agentX, status.agentY, my_world.surroundings[status.agentX][status.agentY].tile_type, 0, 0, 0, null);
        open_list.add(current_position);
        Pair<Integer, Integer> [][] parents = new Pair [status.width][status.height];
        int [][] open_map = new int[status.width][status.height];
        int [][] closed_map = new int[status.width][status.height];
        for(int i = 0; i < status.width; i++){
            for(int y = 0; y < status.height; y++){
                open_map[i][y] = Integer.MAX_VALUE;
                closed_map[i][y] = Integer.MAX_VALUE;
                parents[i][y] = Pair.of(-1,-1);
            }
        }
        open_map[current_position.x_pos][current_position.y_pos] = current_position.f;
        while (open_list.size() != 0) {
            //TODO: check your comparator, if it sorts from smallest and not biggest
            current_position = open_list.remove();
            open_map[current_position.x_pos][current_position.y_pos] = -1;
            SearchTile temp = new SearchTile();
            if(current_position.x_pos == my_target_position.first && current_position.y_pos == my_target_position.second){
                //if( getAgentId() == 1) log(String.format("My target is :[%d][%d] and my position is:[%d][%d], time to backtrace, parents[%d][%d] are:[%d][%d] ", my_target_position.first, my_target_position.second, status.agentX, status.agentY, my_target_position.first, my_target_position.second, parents[my_target_position.first][my_target_position.second].first, parents[my_target_position.first][my_target_position.second].second));
                return backtrace2gold(status, my_target_position, parents);
            }
            for(int i = 0; i < 4; i++){
                // if this position isn't already in in open list with a lower f, add to
                // x-1
                if(current_position.x_pos-1>-1 && i%4 == 0){ // left
                    if(my_world.surroundings[current_position.x_pos-1][current_position.y_pos].tile_type != 1){
                        //if( getAgentId() == 1) log(String.format("Before, My x is: %d and y %d map.", current_position.x_pos, current_position.y_pos));
                        temp = update_offspring(-1, 0, current_position, my_target_position);
                        //if( getAgentId() == 1) log(String.format("My x is: %d and y %d map and my f:%d.", temp.x_pos, temp.y_pos, temp.f));
                        if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                            parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                            open_list.add(temp);
                        }
                    }
                }else if(current_position.x_pos+1< status.width && i%4 == 1){ //right
                    if(my_world.surroundings[current_position.x_pos+1][current_position.y_pos].tile_type != 1){
                        temp = update_offspring(1, 0, current_position, my_target_position);
                        if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                            parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                            open_list.add(temp);
                        }
                    }
                }else if(current_position.y_pos-1> 0 && i%4 == 2){ //up
                    if(my_world.surroundings[current_position.x_pos][current_position.y_pos-1].tile_type != 1){
                        temp = update_offspring( 0, -1, current_position, my_target_position);
                        if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                            parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                            open_list.add(temp);
                        }
                    }

                }else if(current_position.y_pos+1< status.height && i%4 == 3){ //down
                    if(my_world.surroundings[current_position.x_pos][current_position.y_pos+1].tile_type != 1){
                        temp = update_offspring(0, 1, current_position, my_target_position);
                        if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                            parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                            open_list.add(temp);
                        }
                    }
                }
            }
            closed_list.add(current_position);
            closed_map[current_position.x_pos][current_position.y_pos] = current_position.f;

        }
        return null;

    }
    public List <Pair<Integer, Integer>> new_astar(StatusMessage status, World my_world, Pair<Integer, Integer> my_target_position) throws Exception {
        /**
         * Rather self-explanatory, an A* algorithm function, returning a list of steps agent has to take to reach its goal
         **/
        if(my_target_position == null)log(String.format("So my target is null? how?"));
        List <SearchTile> gold_path = null; // first pair are coordinates, second are g and h
        // initialise open List
        Comparator<SearchTile> comparator = new SearchTileComparator();
        PriorityQueue <SearchTile> open_list = new PriorityQueue<SearchTile>(comparator);
        PriorityQueue <SearchTile> closed_list = new PriorityQueue<SearchTile>(comparator);

        SearchTile current_position = new SearchTile(status.agentX, status.agentY, my_world.surroundings[status.agentX][status.agentY].tile_type, 0, 0, 0, null);
        open_list.add(current_position);
        Pair<Integer, Integer> [][] parents = new Pair [status.width][status.height];
        int [][] open_map = new int[status.width][status.height];
        int [][] closed_map = new int[status.width][status.height];
        for(int i = 0; i < status.width; i++){
            for(int y = 0; y < status.height; y++){
                open_map[i][y] = Integer.MAX_VALUE;
                closed_map[i][y] = Integer.MAX_VALUE;
                parents[i][y] = Pair.of(-1,-1);
            }
        }
        open_map[current_position.x_pos][current_position.y_pos] = current_position.f;
        while (!open_list.isEmpty()) {
            //TODO: check your comparator, if it sorts from smallest and not biggest
            current_position = open_list.remove();
            open_map[current_position.x_pos][current_position.y_pos] = -1; // marking as visited
            SearchTile temp = new SearchTile();
            if(current_position.x_pos == my_target_position.first && current_position.y_pos == my_target_position.second){
                log(String.format("SO I FOUND A PATH FROM :[%d,%d] to [%d, %d] based off of this map of parents:", status.agentX, status.agentY, my_target_position.first, my_target_position.second));
                return backtrace2gold(status, my_target_position, parents);
            }
            if(current_position.x_pos-1>-1 ){ // left
                if(my_world.surroundings[current_position.x_pos-1][current_position.y_pos].tile_type != 1){
                    //if( getAgentId() == 1) log(String.format("Before, My x is: %d and y %d map.", current_position.x_pos, current_position.y_pos));
                    temp = update_offspring(-1, 0, current_position, my_target_position);
                    //if( getAgentId() == 1) log(String.format("My x is: %d and y %d map and my f:%d.", temp.x_pos, temp.y_pos, temp.f));
                    if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                        parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                        open_list.add(temp);
                    }
                }
            }if(current_position.x_pos+1< status.width ){ //right
                if(my_world.surroundings[current_position.x_pos+1][current_position.y_pos].tile_type != 1){
                    temp = update_offspring(1, 0, current_position, my_target_position);
                    if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                        parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                        open_list.add(temp);
                    }
                }
            }if(current_position.y_pos-1> 0 ){ //up
                if(my_world.surroundings[current_position.x_pos][current_position.y_pos-1].tile_type != 1){
                    temp = update_offspring( 0, -1, current_position, my_target_position);
                    if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                        parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                        open_list.add(temp);
                    }
                }
            }if(current_position.y_pos+1< status.height){ //down
                if(my_world.surroundings[current_position.x_pos][current_position.y_pos+1].tile_type != 1){
                    temp = update_offspring(0, 1, current_position, my_target_position);
                    if(open_map[temp.x_pos][temp.y_pos] > temp.f && closed_map[temp.x_pos][temp.y_pos] > temp.f){
                        parents[temp.x_pos][temp.y_pos] = Pair.of(temp.parent.x_pos,temp.parent.y_pos);
                        open_list.add(temp);
                    }
                }
            }
            closed_list.add(current_position);
            closed_map[current_position.x_pos][current_position.y_pos] = current_position.f;
        }
        log(String.format("ERROR: A* FAILURE, cannot get from (%d, %d) to (%d, %d)", status.agentX, status.agentY, my_target_position.first, my_target_position.second));
        return null;
    }
    public StatusMessage showdirection(Pair<Integer, Integer> my_next_position, StatusMessage status) throws Exception {
        if(my_next_position.first < status.agentX){//left
            return left();
        }else if(my_next_position.first > status.agentX){ // right
            return right();
        }else if(my_next_position.second < status.agentY){ //up
            return up();
        }else if(my_next_position.second>status.agentY){
            return down();
        }else{ // we're probably on the same spot, so we take another "step"
            if( carrier)log(String.format("Dumb edgecase"));
        }
        return null;
    }

}
