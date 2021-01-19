#!/usr/bin/env python3
# -*- coding: utf-8 -*-
 
import math
import time
import numpy as np
import collections
import heapq
import GridMap as gmap
from operator import itemgetter
 
 
class GridPlanner:
 
    def __init__(self):
        pass
 
    def distance(self, start, end, neigh):
        if neigh == 'N4':
            return abs(start[0] - end[0]) + abs(start[1] - end[1])
        else:
            return np.linalg.norm(np.array(start) - np.array(end))
 
    def plan(self, gridmap, start, goal, neigh):
        """
        Method to plan the path
 
        Parameters
        ----------
        gridmap: GridMap
            gridmap of the environment
        start: (int,int)
            start coordinates
        goal:(int,int)
            goal coordinates
        neigh:string(optional)
            type of neighborhood for planning ('N4' - 4 cells, 'N8' - 8 cells)
 
        Returns
        -------
        list(int,int)
            the path between the start and the goal if there is one, None if there is no path
        """
        came_from = self.a_star_search(gridmap, start, goal, neigh)
        if not goal in came_from:
            return None
        path = self.reconstruct_path(came_from, start, goal)
        return path
 
    #########################################
    # A_STAR
    #########################################
    def a_star_search(self, graph, start, goal, neigh):
        """
        This is the function for you to implement.
 
        Parameters
        ----------
        graph: GridMap
            gridmap of the environment
        start: (int,int)
            start coordinates
        goal: (int,int)
            goal coordinates
        neigh:string(optional)
            type of neighborhood for planning ('N4' - 4 cells, 'N8' - 8 cells)
 
        Returns
        -------
        dict (int,int) -> (int, int)
            for every node in path give his predecessor.
        """
        open_dict = {}
        closed_dict = {}
        f_dict = {}
        starting_node = [self.distance(start, goal, neigh), start, 0, (-1, -1)]
        open_dict[start] = starting_node
        f_dict[start] = starting_node[0]
        running = True
        while running:
            current_pair = min(f_dict.items(), key=lambda x: x[1])
            current_position = current_pair[0]
            current_body = open_dict[current_position]
            f_dict.pop(current_position)
            open_dict.pop(current_position)
            for neighbour in graph.neighbors(current_position, neigh):
                f = self.distance(current_position, neighbour, neigh) + self.distance(neighbour, goal, neigh) + \
                    current_body[2]
                if neighbour == goal:
                    closed_dict[neighbour] = [f, neighbour, self.distance(current_position, neighbour, neigh)+current_body[2], current_position]
                    f_dict[current_position] = f
                    running = False
                    break
                if neighbour in open_dict and open_dict[neighbour][0] <= f:
                    continue
                if neighbour in closed_dict and closed_dict[neighbour][0] <= f:
                    continue
 
                open_dict[neighbour] = [f, neighbour, self.distance(current_position, neighbour, neigh)+current_body[2], current_position]
                f_dict[neighbour] = f
            closed_dict[current_position] = current_body
        result_dict = {}
        item = goal
        while item != start:
            result_dict[item] = closed_dict[item][3]
            item = closed_dict[item][3]
        return result_dict
 
 
 
 
 
 
 
 
 
    def old_a_star_search(self, graph, start, goal, neigh):
        """
        This is the function for you to implement.
 
        Parameters
        ----------
        graph: GridMap
            gridmap of the environment
        start: (int,int)
            start coordinates
        goal: (int,int)
            goal coordinates
        neigh:string(optional)
            type of neighborhood for planning ('N4' - 4 cells, 'N8' - 8 cells)
 
        Returns
        -------
        dict (int,int) -> (int, int)
            for every node in path give his predecessor.
        """
 
        # instantiate the map
        h = self.distance(start, goal, neigh)
        g = 0
        f = g + h
        open_list = []
        open_dict = {}
        closed_list = []
        closed_dict = {}
        start_node = [f, start, g, (-1, -1)]
        open_list.append(start_node)
        open_dict[start] = start_node
        looking = True
        my_current = []
        while looking:
            minimum = 999
            for key, value in open_dict.items():
                if int(value[0]) <= minimum:
                    minimum = value[0]
                    my_current = value
 
            if len(open_dict) == 1:
                open_dict = {}
            else:
                open_dict[my_current[1]] = [99999]
 
 
            if my_current[1] == goal:
                looking = False
                closed_dict[my_current[1]] = my_current
                break
            for neighbour in graph.neighbors(my_current[1], neigh):
                successor_current_cost = my_current[2] + self.distance(neighbour, my_current[1], neigh)
                # if successor is in the open list
                if neighbour in open_dict.keys() and open_dict[neighbour] != [99999]:
                    if open_dict[neighbour][2] < successor_current_cost:
                        continue
                elif neighbour in closed_dict.keys():
                    if closed_dict[neighbour][2] < successor_current_cost:
                        continue
                    temp = closed_dict[neighbour]
                    open_dict[neighbour] = temp
                    del closed_dict[neighbour]
                else:
                    updated_entry = [successor_current_cost + self.distance(neighbour, goal, neigh), neighbour,
                                     successor_current_cost, my_current[1]]
                    open_dict[neighbour] = updated_entry
                open_dict[neighbour] = [successor_current_cost + self.distance(neighbour, goal, neigh), neighbour,
                                        successor_current_cost, my_current[1]]
            closed_dict[my_current[1]] = my_current
        result_dict = {}
        item = goal
        while item != start:
            result_dict[item] = closed_dict[item][3]
            item = closed_dict[item][3]
        return result_dict
 
    #########################################
    # backtracking function
    #########################################
 
    def reconstruct_path(self, came_from, start, goal):
        current = goal
        path = [current]
        while current != start:
            print("current")
            print(current)
            current = came_from[current]
            path.append(current)
        path.append(start)  # optional
        path.reverse()  # optional
        return path
