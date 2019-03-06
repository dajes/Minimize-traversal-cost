# Minimize traversal cost

Given a matrix A(n, m), each pass through (i, j) grid cell costs A(i, j). The task is to minimise a cost while completing given subtasks:
A) Get from any cell in the first row to the last row:
	1) While allowed to move only to the three neighbour bottom cells
	2) While allowed to move to any of the eight neighbour cells
B) The same as A, but get from the first row and column to the last row and column: (1, 1) -> (n, m)

For the task, I used reinforcement learning DP approach. Each cell at the beginning has only its price, but we iteratively update its virtual cost respectively to cost of the cheapest neighbour multiplied by some gamma coefficient which belongs to that interval: [0;1), so it will converge even on infinite matrix. At the end of every iteration, we add the step cost which means that the farther current cell from the goal the higher price of being in here. To ensure that each value in the array has influenced every other value at least once, I set our iterations count is max(N, M) (Bigger value would be better, but it is enough to make optimal moves). Each iteration complexity is O(N * M), so the whole algorithm complexity is O(max(N, M) * N * M). In case of square matrix: O(N^3).

In the application, the high price of being in a cell marked with red colour and the cheap one marked with blue colour.