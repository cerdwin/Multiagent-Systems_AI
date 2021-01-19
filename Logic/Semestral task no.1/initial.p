% Inequalities to substitute for missing UNA
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
fof(diff_car_bike, axiom, (car != bike)).
fof(diff_tabor_praha, axiom, (tabor != praha)).
fof(diff_tabor_benesov, axiom, (tabor != benesov)).
fof(diff_tabor_benesov, axiom, (tabor != plzen)).
fof(diff_tabor_benesov, axiom, (tabor != ostrava)).
fof(diff_benesov_praha, axiom, (benesov != praha)).
fof(diff_benesov_praha, axiom, (benesov != plzen)).
fof(diff_benesov_praha, axiom, (benesov != ostrava)).
fof(diff_benesov_praha, axiom, (praha != plzen)).
fof(diff_benesov_praha, axiom, (praha != ostrava)).
fof(diff_benesov_praha, axiom, (plzen != ostrava)).
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Defintions of roads between the three cities
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
fof(road_praha_benesov, axiom, road(praha, benesov)).
fof(road_benesov_tabor, axiom, road(benesov, tabor)).
fof(road_plzen_ostrava, axiom, road(plzen, ostrava)).
 
fof(bidirectional_roads, axiom, ! [X, Y] : (road(X,Y) => road(Y, X))).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Initial states
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
fof(car_initial_location,axiom,
    location(car, tabor, s0)
).
 
fof(bike_initial_location,axiom,
    location(bike, praha, s0)
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% Define the move action
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(move_action_effect, axiom,
    ! [A, X, Y, S]:
        (
            (road(X,Y) & location(A, X, S))
            =>
            (location(A,Y, result(move(A, X, Y, S))))
        )
).
 
fof(move_action_frame, axiom,
    ! [A, X, Y, S, A2, X2]:
        (
            (road(X,Y) & location(A, X, S) & location(A2, X2, S) & (A2 != A)) 
            =>
            (location(A2, X2, result(move(A, X, Y, S))))
        )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  
% Define the goals to be proven
% THIS IS YOUR ASSIGNMENT
% YOUR TASK IS TO WRITE AND REPORT THE RESULTS OF THE PROVER FOR FOLLOWING
% CONJECTURES (WHICH CAN BE USED AS TESTS FOR THE CORRECTNESS OF THE
% DEFINITIONS ABOVE):
 
% 00a: The car starts tabor (shown in the example below)
% 00b: The car can reach tabor (shown in the example below)
% 01: The car can reach benesov
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_01, conjecture,
    (
    ?[S]:
        location(car, benesov, S)
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 02: The car can be in both benesov and tabor at the same time
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_02, conjecture,
    (
    ?[S]:
        (location(car, benesov, S) & location(car, tabor, S))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 03: The bike and the car can be both in benesov at the same time
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_03, conjecture,
    (
    ?[S]:
        (location(car, benesov, S) & location(bike, benesov, S))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 04: There is a place where both car and bike might be at the same time
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_04, conjecture,
    (
    ?[S, P]:
        (location(car, P, S) & location(bike, P, S))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 05: There is a place where two different vehicles can be at the same time
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_05, conjecture,
    (
    ?[X, Y, S, P]:
        (location(X, P, S) & location(Y, P, S) & (X != Y))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 06: There is a road between tabor and benesov and also between benesov and tabor.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_06, conjecture,
    (
    (road(tabor, benesov) & road(benesov, tabor))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 07: There is a road between tabor and praha and between praha and tabor.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_07, conjecture,
    (
    (road(tabor, praha) & road(praha, tabor))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 08: There is a place from which there is a road to tabor and to praha.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_08, conjecture,
    (
    ?[P]:
    (road(P, praha) & road(P, tabor))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 09: There is a road from praha to praha.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_09, conjecture,
    (
    (road(praha, praha))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 10: There is a road from praha to plzen
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_10, conjecture,
    (
    (road(praha, plzen))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 11: There is time (state), when there is a road from praha to plzen
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_11, conjecture,
    (
    ?[S]:
    (road(praha, plzen))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 12: The bike can reach plzen.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(zanzajen_conjecture_12, conjecture,
    (
    ?[S]:
        (location(bike, plzen, S))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% 13: There is a route of arbitrary length from praha to tabor (i.e. tabor can be reached from praha).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% I understand this as whether there can be two states, when X can be in Prague in one of them and in tabor in the other
fof(zanzajen_conjecture_13, conjecture,
    (
    ?[S1, S2, X]:
        (location(X, praha, S1) & location(X, tabor, S2))
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 14: There are three different places that bike can reach in two moves.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Honestly, I was at my wits end with this one a bit. Im not sure if I understood the prompt at hand as it was meant.
%% Given the comments on the course forum saying the prompt could be interpreted in various ways, here is my take:
%% I understood it as a sequence of consecutive actions, where a bike starts somewhere (X), makes a step to Y and then another to
%% Z, where X != Y != Z.
fof(zanzajen_conjecture_14, conjecture,
    (
    ?[P1, P2, P3, S]:
        (
            (P1 != P2) & (P2 != P3) & (P1 != P3)
            & location(bike, P1, S)
            & location(bike, P2, result(move(bike, P1, P2, S)))
            & location(bike, P3, result(move(bike, P2, P3, result(move(bike, P1, P2, S)))))
        )
    )
).
 
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 15: The bike and car can swap places.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% by "swap", I understand that at some point they will be at each other's respective positions occupied at an earlier point
%% whilst I thought of using something along the lines of "result(move(car/bike, P2/P1, P1/P2, S))" instead of S2
%% to account for one-move swaps, that wasnt how I quite understood the task. I think that by "swapping" positions,
%% they could have made a plethora of steps/actions in between before landing on each others' original spots.
 
fof(zanzajen_conjecture_15, conjecture,
    (
    ?[S1, S2, P1, P2]:
        (
        (P1 != P2) &
        location(bike, P1, S1) &
        location(car, P2, S1) &
        location(bike, P2, S2) &
        location(car, P1, S2)
        )
    )
).
%% solution, if the interpretation was that a swap consists of one move, eliciting each others former locations
%%fof(zanzajen_conjecture_15, conjecture,
%%    (
%%    ?[S, P1, P2]:
%%        ((P1 != P2) &  location(bike, P1, S) & location(car, P2, S)
%%        & location(bike, P2, result(move(bike, P1, P2, S))) & location(car, P1, result(move(car, P2, P1, S))))
%%    )
%%).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 16: Object that start at different places, are different.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% I understood this as there arent two places which are different, yet at which one object can be at once.
fof(zanzajen_conjecture_16, conjecture,
    (
    ~? [X, Y, P1, P2]:
        (
        (P1 != P2) &  location(X, P1, s0) & location(Y, P2, s0) & (X = Y)
        )
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
 
% NAME THE CONJECTURES USING THIS TEMPLATE:
% <username>_conjecture_XX WHERE XX IS THE CONJECTURE NUMBER
% AND DELIMIT THE CONJECTURES USING AT LEAT 5x '%'
 
% IF YOU WILL NOT FOLLOW THIS EXAMPLE THIS MAY INTEFERE WITH THE AUTOMATIC
% TESTING AND MIGHT LEAD TO LOWER POINT SCORE OBTAINED OR, IN THE VERY LEAST,
% TO DELAYS IN RECEIVING THE POINTS
  
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
% This section is followed by brief example how the conjectures should look.
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(kuncvlad_conjecture_00a, conjecture,
    (
        location(car, tabor, s0)
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
fof(kuncvlad_conjecture_00b, conjecture,
    (
    ?[S]:
        location(car, tabor, S)
    )
).
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
