# -*- coding: UTF-8 -*-
 
import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.patches import Rectangle
from typing import Tuple
import matplotlib
import numpy as np
import warnings
from abc import ABC
 
 
class MDP(ABC):
    def Q_from_V(self, V: np.ndarray) -> np.ndarray:
 
        Q = np.zeros((self.n_states + 1, self.n_actions))
        Q2 = np.zeros((self.n_states + 1, self.n_actions))
 
        #print(Q)
        for action in self.actions:
 
            Q[:, action] = self.rewards[:] + np.matmul(self.transition_proba[:, action, :], self.discount_factor * V);
            
        return Q
 
    def Q2V(self, Q: np.ndarray) -> np.ndarray:
        return  np.max(Q, axis = 1)
 
    def Q2Vbypolicy(self, Q: np.ndarray, policy: np.ndarray) -> np.ndarray:
        return  np.choose(policy, Q.T);
 
    def Q2policy(self, Q: np.ndarray) -> np.ndarray:

        return  np.argmax(Q, axis=1);
 
    def evaluate_policy(
        self,
        policy: np.ndarray,
        init_V: np.ndarray = None,
        max_iter: int = int(1e3),
        rtol: float = 1e-06,
        atol: float = 1e-08,
    ):
        old_V = self.rewards if init_V is None else init_V
        for i in range(max_iter):
          
            Q = self.Q_from_V(old_V)
            V = self.Q2Vbypolicy(Q, policy)
            if np.allclose(old_V, V, rtol=rtol, atol=atol):
                return V
            old_V = V
        warnings.warn(
            f"Maximum number of iterations ({max_iter}) has been exceeded."
            f"The iterative evaluation of a policy might have not converged"
        )
        return V
 
    def value_iteration(
        self, init_V: np.ndarray = None, max_iter: int = int(1e4), rtol: float = 1e-06, atol: float = 1e-08
    ):
        old_V = self.rewards if init_V is None else init_V
        for i in range(max_iter):
            Q = self.Q_from_V(old_V)
            V = self.Q2V(Q)
            if np.allclose(old_V, V, rtol=rtol, atol=atol):
                return Q
            old_V = V
        warnings.warn(
            f"Maximum number of iterations ({max_iter}) has been exceeded."
            f" Value iteration might have not converged."
        )
        return Q
 
    def policy_iteration(
        self,
        init_policy: np.ndarray = None,
        max_iter: int = int(1e4),
        rtol: float = 1e-08,
        atol: float = 1e-08,
        max_iter_eval_iteration: int = int(1e4),
    ):
        Q = np.zeros((self.n_states + 1, self.n_actions))
        old_Q = Q
        if init_policy is None:
            policy = np.zeros((self.n_states + 1,), dtype=int)
        else:
            policy = init_policy
        old_policy = policy
        for i in range(max_iter):
            V = self.evaluate_policy(policy,max_iter=max_iter_eval_iteration)
            Q = self.Q_from_V(V)
            policy = self.Q2policy(Q)
            if np.allclose(old_Q, Q, rtol=rtol, atol=atol) or np.array_equal(policy, old_policy):
                return Q
            old_Q = Q
            old_policy = policy
        warnings.warn(
            f"Maximum number of iterations ({max_iter}) has been exceeded."
            f"Policy iteration might have not converged."
        )
        return Q
 
 
class GridWorld(MDP):
 
    NORTH = 0
    EAST = 1
    SOUTH = 2
    WEST = 3
    actions = {NORTH: np.array((-1, 0)), EAST: np.array((0, 1)), SOUTH: np.array((1, 0)), WEST: np.array((0, -1))}
    n_actions = len(actions)
    mismatched_actions = {NORTH: (EAST, WEST), EAST: (NORTH, SOUTH), SOUTH: (EAST, WEST), WEST: (NORTH, SOUTH)}
 
    AVAILABLE_WORLDS = ["2x2", "3x3", "3x4", "4x4", "5x5", "6x8", "6x12"]
 
    @staticmethod
    def get_world(name: str, action_proba: float = None, action_cost: float = None) -> MDP:
 
        if name == "2x2":
            ab = 0.8 if action_proba is None else action_proba
            ac = 1 / 25 if action_cost is None else action_cost
            n_rows = 2
            n_columns = 2
            discount_factor = 1
            obstacles = np.zeros((n_rows, n_columns))
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[0, n_columns - 1] = 1
            grid_rewards[1, n_columns - 1] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[0, n_columns - 1] = 1
            terminals[1, n_columns - 1] = 1
 
            gw = GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
            return gw
 
        elif name == "3x3":
            ab = 0.6 if action_proba is None else action_proba
            ac = 1 / 25 if action_cost is None else action_cost
            n_rows = 3
            n_columns = 3
            discount_factor = 1
            obstacles = np.zeros((n_rows, n_columns))
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[0, n_columns - 1] = 1
            grid_rewards[1, n_columns - 1] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[0, n_columns - 1] = 1
            terminals[1, n_columns - 1] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
        elif name == "3x4":
            ab = 0.8 if action_proba is None else action_proba
            ac = 1 / 25 if action_cost is None else action_cost
            n_rows = 3
            n_columns = 4
            discount_factor = 1
            obstacles = np.zeros((n_rows, n_columns))
            obstacles[1, 1] = 1
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[0, n_columns - 1] = 1
            grid_rewards[1, n_columns - 1] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[0, n_columns - 1] = 1
            terminals[1, n_columns - 1] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
        elif name == "4x4":
            ab = 0.5 if action_proba is None else action_proba
            ac = 2 / 25 if action_cost is None else action_cost
            n_rows = 4
            n_columns = 4
            discount_factor = 0.8
            obstacles = np.zeros((n_rows, n_columns))
            obstacles[1, 1] = 1
            obstacles[2, 2] = 1
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[0, n_columns - 1] = 1
            grid_rewards[1, n_columns - 1] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[0, n_columns - 1] = 1
            terminals[1, n_columns - 1] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
        elif name == "5x5":
            ab = 0.95 if action_proba is None else action_proba
            ac = 1 / 50 if action_cost is None else action_cost
            n_rows = 5
            n_columns = 5
            discount_factor = 0.99
            obstacles = np.zeros((n_rows, n_columns))
            obstacles[1, 0] = 1
            obstacles[1, 1] = 1
            obstacles[2, 2] = 1
 
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[0, 0] = 2
            grid_rewards[1, 2] = -1
            grid_rewards[0, n_columns - 1] = 1
            grid_rewards[1, n_columns - 1] = -1
            grid_rewards[4, 2] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[0, 0] = 1
            terminals[1, 2] = 1
            terminals[0, n_columns - 1] = 1
            terminals[1, n_columns - 1] = 1
            terminals[4, 2] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
        elif name == "6x8":
            ab = 0.95 if action_proba is None else action_proba
            ac = 1 / 50 if action_cost is None else action_cost
            n_rows = 6
            n_columns = 8
            discount_factor = 0.99
            obstacles = np.zeros((n_rows, n_columns))
            obstacles[1:5, 1] = 1
            obstacles[1, 1:6] = 1
            obstacles[1:5, 5] = 1
            obstacles[4, 3] = 1
            obstacles[1, 6] = 1
            obstacles[3, 7] = 1
 
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[3, 3] = 2
            grid_rewards[4, 4] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[3, 3] = 1
            terminals[4, 4] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
        elif name == "6x12":
            ab = 0.7 if action_proba is None else action_proba
            ac = 0.01 if action_cost is None else action_cost
            n_rows = 6
            n_columns = 12
            discount_factor = 0.99
            obstacles = np.zeros((n_rows, n_columns))
            obstacles[4, 1:10] = 1
            obstacles[1:4, 1] = 1
            obstacles[0:3, 3] = 1
            obstacles[1:4, 5] = 1
            obstacles[0:3, 7] = 1
            obstacles[1:4, 9] = 1
 
            grid_rewards = np.zeros((n_rows, n_columns))
            grid_rewards[5, 8] = 2
            grid_rewards[1:5, 11] = -1
 
            terminals = np.zeros((n_rows, n_columns))
            terminals[5, 8] = 1
            terminals[1:5, 11] = 1
 
            return GridWorld(
                n_rows,
                n_columns,
                obstacles,
                terminals,
                grid_rewards,
                discount_factor=discount_factor,
                action_proba=ab,
                action_cost=ac,
            )
 
    def __init__(
        self,
        n_rows: int,
        n_columns: int,
        obstacles: np.ndarray,
        terminals: np.ndarray,
        grid_rewards: np.ndarray,
        discount_factor: float = 1,
        action_proba: float = 0.8,
        action_cost: float = 0.04,
    ):

        self.n_rows = n_rows
        self.n_columns = n_columns
        self.obstacles = obstacles
        self.terminals = terminals
        self.grid_rewards = grid_rewards
 
        self.discount_factor = discount_factor
        self.action_proba = action_proba
        self.action_cost = action_cost
        self.n_states = n_rows * n_columns  # number of states without terminal sink state
        self.transition_proba = None
        self._get_transition_proba()
 
        self.rewards = np.ones(self.n_states + 1) * -self.action_cost
        for ind_reward in np.flatnonzero(self.grid_rewards):
            self.rewards[ind_reward] = self.grid_rewards.flat[ind_reward]
        self.rewards[self.n_states] = 0
 
    def _state2coord(self, state: int) -> Tuple[int, int]:
        return np.unravel_index(state, (self.n_rows, self.n_columns))
 
    def _coord2state(self, coord: Tuple[int, int]) -> int:
        return np.ravel_multi_index(coord, (self.n_rows, self.n_columns))
 
    def _is_on_grid(self, coord: Tuple[int, int]) -> bool:
        r, c = coord
        return 0 <= r < self.n_rows and 0 <= c < self.n_columns
 
    def _is_obstacle(self, coord: Tuple[int, int]) -> bool:

        r, c = coord
        return self.obstacles[r, c] == 1
 
    def _is_terminal(self, coord: Tuple[int, int]) -> bool:

        return self.terminals[coord] == 1
 
    def _get_transition_proba(self):
 
        self.transition_proba = np.zeros(
            (self.n_states + 1, self.n_actions, self.n_states + 1)
        )  # terminal sink state added
        for state in range(self.n_states):
            state_coord = self._state2coord(state)
            if self._is_terminal(state_coord):
                # if the state is terminal, go to the single terminal sink with p = 1
                self.transition_proba[state, :, self.n_states] = 1
            else:
                for action in self.actions:
                    # compute the transition probabilities of intended actions
                    next_state_coord = state_coord + self.actions[action]
                    if not self._is_on_grid(next_state_coord) or self._is_obstacle(next_state_coord):
                        self.transition_proba[state, action, state] += self.action_proba
                    else:
                        next_state = self._coord2state(next_state_coord)
                        self.transition_proba[state, action, next_state] += self.action_proba
 
                    # compute the transition probabilities of mistaken actions
                    # the probability of mistaken action is the same for all possible mistakes for given action
                    for mismatched_action in self.mismatched_actions[action]:
                        mistake_proba = (1 - self.action_proba) / len(self.mismatched_actions[action])
                        next_state_coord = state_coord + self.actions[mismatched_action]
                        if not self._is_on_grid(next_state_coord) or self._is_obstacle(next_state_coord):
                            self.transition_proba[state, action, state] += mistake_proba
                        else:
                            next_state = self._coord2state(next_state_coord)
                            self.transition_proba[state, action, next_state] += mistake_proba
        # the terminal state cannot be left -> with any action, an agent stays in the state with p = 1
        self.transition_proba[self.n_states, :, self.n_states] = 1
 
    def plot(self, V: np.ndarray = None, policy: np.ndarray = None):

        if V is None:
            data = self.grid_rewards
        else:
            if len(V) == self.n_rows * self.n_columns + 1:
                V = V[:-1]
            data = V.reshape(self.n_rows, self.n_columns)
        policy_symbols = {0: "↑", 1: "→", 2: "↓", 3: "←"}
        mask = np.zeros_like(data, dtype=bool)
        mask[self.obstacles == 1] = True
        # to center the heatmap around zero
        maxval = max(np.abs(np.min(data)), np.abs(np.max(data)))
        ax = sns.heatmap(
            data, annot=True, mask=mask, fmt=".3f", square=1, linewidth=1.0, cmap="coolwarm", vmin=-maxval, vmax=maxval
        )
        for i, j in zip(*np.where(self.terminals == 1)):
            ax.add_patch(Rectangle((j, i), 1, 1, fill=False, edgecolor="black", lw=3))
        if policy is not None:
            for t, pol in zip(ax.texts, policy[:-1][(~mask).flat]):
                t.set_text(policy_symbols[pol])
                t.set_size("xx-large")
        plt.show()
 
 
if __name__ == "__main__":
    # One can experiment here or you can experiment in a separate script with GridWorld imported.
    # Example
    name = "6x12"
    gw = GridWorld.get_world(name)
 
    print("Plotting")
    gw.plot()
 
    plt.show()
 
    # random V
    V = np.random.random((gw.n_states + 1))
    print(V)
    # random policy
    policy = np.random.randint(4, size=(gw.n_states + 1))
 
    gw.plot(V=V)
    plt.show()
    gw.plot(V=V, policy=policy)
    plt.show()
