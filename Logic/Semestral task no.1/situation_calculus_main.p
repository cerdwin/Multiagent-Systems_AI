% Inequalities to substitute for missing UNA
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(diff_room_ab, axiom, (room_a != room_b)).
fof(diff_room_ac, axiom, (room_a != room_c)).
fof(diff_room_ad, axiom, (room_a != room_d)).
fof(diff_room_bc, axiom, (room_b != room_c)).
fof(diff_room_bd, axiom, (room_b != room_d)).
fof(diff_room_cd, axiom, (room_c != room_d)).
 
fof(diff_alice_bob, axiom, (alice != bob)).
fof(diff_alice_key_ba, axiom, (alice != key_ba)).
fof(diff_bob_key_ba, axiom, (bob != key_ba)).
fof(diff_alice_treasure, axiom, (alice != treasure)).
fof(diff_bob_treasure, axiom, (bob != treasure)).
fof(diff_key_ba_treasurea, axiom, (key_ba != treasure)).
 
fof(diff_door_ab_bc, axiom, (door_ab != door_bc)).
fof(diff_door_bc_cd, axiom, (door_bc != door_cd)).
fof(diff_door_ab_cd, axiom, (door_ab != door_cd)).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Defintions of doors connecting the rooms and the transitions between rooms
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(door_ab, axiom,
    (door(door_ab, room_a, room_b, closed, s0))
).
fof(door_bc, axiom,
    (door(door_bc, room_b, room_c, open, s0))
).
fof(door_cd, axiom,
    (door(door_cd, room_c, room_d, open, s0))
).
 
fof(bidirectional_doors, axiom,
    ( ! [D, X, Y, DS, S]:
        (door(D, X, Y, DS, S) => door(D, Y, X, DS, S))
    )
).
 
fof(reachable, axiom,
    ( ! [D, X, Y, S]:
        (door(D, X,Y,open, S) => reachable(X, Y, S))
    )
).
 
fof(reachable_tranzitivity, axiom,
    ( ! [X, Y, Z, S]:
        (
            (reachable(X,Y, S) & reachable(Y, Z, S))
            =>
            (reachable(X,Z, S))
        )
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Define abilities
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(alice_can_move, axiom,
    can_move(alice, s0)
).
 
fof(bob_can_move, axiom,
    can_move(bob, s0)
).
 
fof(alice_can_pick, axiom,
    can_pick(alice, s0)
).
 
fof(bob_can_pick, axiom,
    can_pick(bob, s0)
).
 
% key 'key_ba' can open the door 'door_ab' from 'room_b'
fof(key_ba_rooms, axiom,
    can_open_lock(key_ba, door_ab, room_b, room_a)
).
fof(key_ba_canbepicked, axiom,
    (can_be_picked(key_ba))
).
 
fof(treasure_canbepicked, axiom,
    (can_be_picked(treasure))
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Initial states
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% initial positions
fof(alice_initial_position, axiom,
    location(alice, room_a, s0)
).
fof(bob_initial_position, axiom,
    location(bob, room_b, s0)
).
fof(key_ba_initial_position, axiom,
    location(key_ba, room_b, s0)
).
fof(treasure_initial_position, axiom,
    location(treasure, room_d, s0)
).
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Define actions
 
% THIS IS YOUR ASSIGNMENT, TO COMPLETE THE ACTIONS
% THE LIST OF FRAMES AXIOMS IS NOT COMPLETE - YOU HAVE TO COMPLETE THEM
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Move action
 
% can move only from current location to reachable one and only if the agent is
% able to move
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% effect axiom
fof(move_effect, axiom,
    ( ![A,X,Y,S]:
        (
            % here fill the conditions for the move action
            (can_move(A, S) & location(A, X, S) & reachable(X, Y, S))
            =>
            (location(A, Y, result(move(A,X,Y,S))))
        )
    )
).
 
 
% here come the frame axioms
%%%%%%%% doesnt affect location %%%%%%%%%
fof(move_frame_location_effect, axiom,
    (
    ! [A,X,Y,S,A2,X2]:
        (
        (can_move(A, S) & location(A, X, S) & reachable(X, Y, S) & location(A2, X2, S) & (A2 != A))
        =>
        (location(A2, X2, result(move(A,X,Y,S))))
        )
    )
).
%%%%%%%% doesnt affect ability to move %%%%%%%%%
fof(move_frame_can_move_effect, axiom,
    (
    ! [A,X,Y,S,A2]:
        (
        (can_move(A, S) & location(A, X, S) & reachable(X, Y, S) & can_move(A2, S))
        =>
        (can_move(A2, result(move(A,X,Y,S))))
        )
    )
).
 
%%%%%%%% doesnt affect ability to pick up %%%%%%%%%
fof(move_frame_can_pick_effect, axiom,
    (
    ! [A,X,Y,S,A2]:
        (
        (can_move(A, S) & location(A, X, S) & reachable(X, Y, S) & can_pick(A2, S))
        =>
        (can_pick(A2, result(move(A,X,Y,S))))
        )
    )
).
%%%%%%%% doesnt affect doors %%%%%%%%%
fof(move_frame_door_effect, axiom,
    (
    ! [A,X,Y,S,D,R1,R2,F]:
        (
        (can_move(A, S) & location(A, X, S) & reachable(X, Y, S) & door(D, R1, R2, F, S))
        =>
        (door(D, R1, R2, F, result(move(A,X,Y,S))))
 
        )
    )
).
 
%%%%%%%% doesnt affect possessions %%%%%%%%%
 
fof(move_frame_possession_effect, axiom,
    (
    ! [A,X,Y,S,A2,K]:
        (
        (can_move(A, S) & location(A, X, S) & reachable(X, Y, S) & has(A2, K, S))
        =>
        (has(A2, K, result(move(A,X,Y,S))))
        )
    )
).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
 
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Picking up an object
 
% Object can be picked-up by an agent only if both are at the same location and
% the agent can pick up and the object can be picked up.
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% effect axiom
fof(pick_up_effect, axiom,
    ( ![A,K,X,S]:
        (
            % here fill the conditions for the pick up action
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K) )
            =>
            (has(A, K, result(pick_up(A,K,X,S))))
        )
    )
).
 
% here come the frame axioms
%%%%%%%%%%%%%%% doesnt affect doors %%%%%%%%%%%%%%%%%%%%%
fof(pick_up_frame_door_effect, axiom,
    ( ![A,K,X,S,D,R1,R2,F]:
        (
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K) &  door(D,R1,R2,F,S))
            =>
 
            (door(D, R1, R2, F, result(pick_up(A,K,X,S))))
        )
    )
).
%%%%%%%%%%%%%%% doesnt affect ability to pick up objects %%%%%%%%%%%%%%%%%%%%%
fof(pick_up_frame_can_pick_effect, axiom,
    ( ![A,K,X,S,A2]:
        (
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K) &  can_pick(A2, S))
            =>
            (can_pick(A2, result(pick_up(A,K,X,S))))
        )
    )
).
%%%%%%%%%%%%%%% doesnt affect ability to move %%%%%%%%%%%%%%%%%%%%%
fof(pick_up_frame_can_move_effect, axiom,
    ( ![A,K,X,S,A2]:
        (
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K)  &  can_move(A2, S))
            =>
            (can_move(A2, result(pick_up(A,K,X,S))))
        )
    )
).
%%%%%%%%%%%%%%% doesnt affect location %%%%%%%%%%%%%%%%%%%%%
fof(pick_up_frame_location_effect, axiom,
    ( ![A,K,X,S,A2,X2]:
        (
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K) & (A2 != K) & location(A2, X2, S))
            =>
            (location(A2, X2, result(pick_up(A,K,X,S))))
        )
    )
).
%%%%%%%%%%%%%%% doesnt affect possessions %%%%%%%%%%%%%%%%%%%%%
fof(pick_up_frame_possession_effect, axiom,
    ( ![A,K,X,S,A2,W]:
        (
            (location(A, X, S) & location(K, X, S) & can_pick(A, S) & can_be_picked(K) &  has(A2, W, S))
            =>
            (has(A2, W, result(pick_up(A,K,X,S))))
        )
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Open door
 
% One can only open a door if he has the key from the door (note that the keys
% have defined side of the door where they can be used), the door are closed
% and the agent is at the correct location.
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(open_door_effect, axiom,
    ( ! [D,A,R1,R2,K,S]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) & has(A, K, S) & can_open_lock(K, D, R1, R2) &
            door(D, R1, R2, closed, S))
            =>
            (door(D, R1, R2, open, result(open_door(D,A,R1,R2,K,S))))
        )
    )
).
 
% here come the frame axioms
%%%%%%%%%%%%%%% doesnt affect move %%%%%%%%%%%%%%%%%%%%%
fof(open_door_frame_move_effect, axiom,
    ( ! [D,A,R1,R2,K,S,B,X]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) &
            has(A, K, S) &
            door(D, R1, R2, closed, S) &
            can_open_lock(K, D, R1, R2)  &
            can_move(B, S))
            =>
            (can_move(B, result(open_door(D,A,R1,R2,K,S))))
        )
    )
 
).
%%%%%%%%%%%%%%% doesnt affect pick-up %%%%%%%%%%%%%%%%%%%%%
fof(open_door_frame_pickup_effect, axiom,
    ( ! [D,A,R1,R2,K,S,B,X]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) &
            has(A, K, S) &
            door(D, R1, R2, closed, S) &
            can_open_lock(K, D, R1, R2)  &
            can_pick(B, S))
            =>
            (can_pick(B, result(open_door(D,A,R1,R2,K,S))))
        )
    )
 
).
%%%%%%%%%%%%%%% doesnt affect has %%%%%%%%%%%%%%%%%%%%%
fof(open_door_frame_has_effect, axiom,
    ( ! [D,A,R1,R2,K,S,X,V]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) &
            has(A, K, S) &
            door(D, R1, R2, closed, S) &
            can_open_lock(K, D, R1, R2)  &
            has(X, V, S))
            =>
            (has(X, V, result(open_door(D,A,R1,R2,K,S))))
        )
    )
 
).
 
%%%%%%%%%%%%%%% doesnt affect location %%%%%%%%%%%%%%%%%%%%%
fof(open_door_frame_has_effect, axiom,
    ( ! [D,A,R1,R2,K,S,X,RX]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) &
            has(A, K, S) &
            door(D, R1, R2, closed, S) &
            can_open_lock(K, D, R1, R2)  &
            location(X, RX, S))
            =>
            (location(X, RX, result(open_door(D,A,R1,R2,K,S))))
        )
    )
 
).
%%%%%%%%%%%%%%% doesnt affect doors %%%%%%%%%%%%%%%%%%%%%
fof(open_door_frame_door_effect, axiom,
    ( ! [D,A,R1,R2,K,S,D2,RX,RY,F]:
        (
            % here fill the conditions for opening the door
            (location(A, R1, S) &
            has(A, K, S) &
            door(D, R1, R2, closed, S) &
            can_open_lock(K, D, R1, R2)  &
            door(D2, RX, RY, F, S) &
            D2 != D)
            =>
            (door(D2, RX, RY, F, result(open_door(D,A,R1,R2,K,S))))
        )
    )
 
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%     KEEP THIS BLOCK AS IT WILL BE AUTOMATICALLY DETECTED DURING GRADING     %
%       ITS REMOVAL MIGHT LEAD TO PROBLEMS WITH GRADING YOUR ASSIGNMENT       %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Goals to be proven
 
% It recommended to go from the simplest proves to the more complex ones.
% Finally, you have to prove that alice can get the treasure.
 
% Feel free to check any conjectures you want, this part of the solution will
% be replaced by a custom part for the grading process.
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(our_goal, conjecture,
    (  % Here are commented goals roughly ordered by increasing complexity and should help you in testing your code
 
        % 1. Hold at the begininning (s0)
        %?[S]: location(alice, room_a, S) % T
 
        %?[S]: location(bob, room_b, S) % T
        %?[S]: location(treasure, room_d, S) % T
        %?[S]: location(treasure, room_a, S) % F
        %?[S]: has(alice, key_ba, S) % F
 
        % 2. simple actions - effects
        %location(bob, room_c, result(move(bob,room_b,room_c,s0))) % T
        %has(bob, key_ba, result(pick_up(bob,key_ba,room_b,s0))) % T
        %location(bob, room_d, result(move(bob,room_c,room_d,result(move(bob,room_b,room_c,s0))))) % T
 
        % 3. simple actions - frames
        %can_move(bob, result(move(bob,room_b,room_c,s0))) % T
        %can_pick(alice, result(move(bob,room_b,room_c,s0))) % T
        %location(alice, room_a, result(move(bob,room_b,room_c,s0))) % T
        %location(alice, room_b, result(move(bob,room_b,room_c,s0))) % F
        %door(door_ab, room_a, room_b, closed, result(move(bob,room_b,room_c,s0))) % T
        %location(alice, room_a, result(move(bob,room_b,room_c,result(pick_up(bob,key_ba,room_b,s0))))) % T
 
 
        % 4. more complex actions
        %?[S]: door(door_ab, room_b, room_a, open, S) % T
        %?[S]: location(alice, room_b, S) % T
        %?[S]: reachable(room_a, room_d, S) % T
        %?[S]: location(alice, room_d, S) % T
        %?[S]: (door(door_ab, room_a, room_b, closed, S) & door(door_ab, room_b, room_a, open, S)) % F
        %?[S]: (location(alice, room_d, S) & location(treasure, room_d, S) & can_be_picked(treasure) & can_pick(alice, S)) % T
        % 5. Final goal to be reached
        %?[S]: has(alice, treasure, S) % T   (approx. in 20 s)
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
