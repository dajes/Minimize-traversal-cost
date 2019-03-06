import java.util.Random;

public class GridWorld extends Thread{

    private int stepCost = 50;  // Cost of being 1 in step from the goal
    private double[][] V;       // Values of being in the cells
    private boolean[][] visited;// It is certainly not optimal to visit the same cell again

    int[][] matrix;             // Real values of the cells
    int n, m;
    // Normalize V values to pick colour easier
    double[][] normalizedV(){
        double[][] result = new double[n][m];
        double max = result[0][0], min = result[0][0];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                max = max >= V[i][j] ? max : V[i][j];
                min = min <= V[i][j] ? min : V[i][j];
            }
        }
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                result[i][j] = (V[i][j]-min)/max;
            }
        }
        return result;
    }
    GridWorld(int n, int m) {
        this.n = n;
        this.m = m;
        matrix = new int[n][m];
        V = new double[n][m];
        visited = new boolean[n][m];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = r.nextInt(99) + 1;
                V[i][j] = matrix[i][j];
                visited[i][j] = false;
            }
        }
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                sb.append("[");
                sb.append(matrix[i][j]);
                sb.append("]");
            }
            sb.append("\t\t");
            for (int j = 0; j < m; j++) {
                sb.append("[");
                sb.append(V[i][j]);
                sb.append("]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Calculate cost of being in certain positions
    private void buildV(boolean anyDirection, int n) {
        double gamma = 0.9;
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.m; j++) {
                V[i][j] = matrix[i][j];         // Set Value matrix equal to original costs
            }
        }
        // Amount of iterations is max(N, M)
        // But not less than 30
        int count = (int)Math.max(30, 1.0*Math.max(this.m, this.n));
        for (int k = 0; k < count; k++) {
            addStepCost();      // Add the step cost
            double[][] newV = new double[this.n][this.m];   // Updated Value matrix
            for (int i = 0; i < this.n; i++) {
                // If it is not the target row
                if (i != n) {
                    // Calculate new Value for each cell
                    for (int j = 0; j < this.m; j++) {
                        // Find out the best move
                        int[] indexes = bestMove(i, j, anyDirection);
                        // If best move is found, update cost as
                        // New cost = original value + gamma * best possible move cost
                        newV[i][j] = indexes[0] >= 0 ?
                                matrix[i][j] + gamma * (V[indexes[0]][indexes[1]])
                                : V[i][j];
                    }
                } else {
                    // Rewrite row with original values
                    for (int j = 0; j < this.m; j++) {
                        newV[i][j] = matrix[i][j];
                    }
                }
            }
            // Update V matrix
            V = newV;
        }
        // At the end add step cost
        addStepCost();
    }
    // Calculate cost of being in certain positions (Task B)
    private void buildVB(boolean anyDirection) {
        double gamma = .9;
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.m; j++) {
                V[i][j] = matrix[i][j];     // Set Value matrix equal to original costs
            }
        }
        V[n-1][m-1] -= 10* stepCost;        // Reward for getting to the goal
        // Amount of iterations is max(N, M)
        // But not less than 30
        int count = (int)Math.max(30, 1.5*Math.max(this.m, this.n));
        for (int k = 0; k < count; k++) {
            addStepCost(n-1, m-1, anyDirection);    // Add the step cost
            double[][] newV = new double[this.n][this.m];   // Updated Value matrix
            for (int i = 0; i < this.n; i++) {
                for (int j = 0; j < this.m; j++) {
                    // If it is not the target row and column
                    if (i != n-1 || j != n-1) {
                        // Calculate new Value for the cell

                        // Find out the best move
                        int[] indexes = bestMove(i, j, anyDirection);
                        // If best move is found, update cost as
                        // New cost = original value + gamma * best possible move cost
                        newV[i][j] = indexes[0] >= 0 ?
                                matrix[i][j] + gamma * (V[indexes[0]][indexes[1]]) : V[i][j];
                    } else {
                        // Rewrite row with original values
                        newV[i][j] = matrix[i][j];
                    }
                }
            }
            V = newV;
        }
        addStepCost(n-1, m-1, anyDirection);

    }
    // Guess what would be the best move in position (i, j)
    // Based on minimizing the prediction of cells price
    private int[] bestMove(int i, int j, boolean anyDirection){
        int[] answer = new int[]{-1, -1};
        double min = Double.POSITIVE_INFINITY;
        if(i+1 < this.n && j-1 >= 0) {
            if(min > V[i + 1][j - 1] && !visited[i+1][j-1]){
                answer[0] = i+1;
                answer[1] = j-1;
                min = V[answer[0]][answer[1]];
            }
        }
        if(i+1 < this.n) {
            if(min > V[i + 1][j] && !visited[i+1][j]){
                answer[0] = i+1;
                answer[1] = j;
                min = V[answer[0]][answer[1]];
            }
        }
        if(i+1 < this.n && j+1 < this.m && !visited[i+1][j+1]) {
            if(min > V[i + 1][j + 1]){
                answer[0] = i+1;
                answer[1] = j+1;
                min = V[answer[0]][answer[1]];
            }
        }
        if(anyDirection){
            if(i-1 >= 0 && j-1 >= 0 && !visited[i-1][j-1]) {
                if(min > V[i - 1][j - 1]){
                    answer[0] = i-1;
                    answer[1] = j-1;
                    min = V[answer[0]][answer[1]];
                }
            }
            if(i-1 >= 0) {
                if(min > V[i - 1][j] && !visited[i-1][j]){
                    answer[0] = i-1;
                    answer[1] = j;
                    min = V[answer[0]][answer[1]];
                }
            }
            if(i-1 >= 0 && j+1 < this.m && !visited[i-1][j+1]) {
                if(min > V[i - 1][j + 1]){
                    answer[0] = i-1;
                    answer[1] = j+1;
                    min = V[answer[0]][answer[1]];
                }
            }
            if(j-1 >= 0 && !visited[i][j-1]) {
                if(min > V[i][j - 1]){
                    answer[0] = i;
                    answer[1] = j-1;
                    min = V[answer[0]][answer[1]];
                }

            }
            if(j+1 < this.m && !visited[i][j+1]) {
                if(min > V[i][j + 1]){
                    answer[0] = i;
                    answer[1] = j+1;
                }
            }
        }
        return answer;
    }
    // Add price for being far from the goal cell
    private void addStepCost(int _i, int _j, boolean anyDirection){
        for(int i = 0; i < this.n; i++){
            for(int j = 0; j < this.m; j++){
                V[i][j] += (Math.abs(_i-i)+Math.abs(_j-j)) * stepCost;
            }
        }
        // If agent can not move in any direction,
        // It gets punishment when gets into the last row
        // ( Except the goal cell )
        if(!anyDirection) {
            for (int i = 0; i < this.m - 1; i++) {
                V[this.n - 1][i] += 10 * stepCost;
            }
        }
    }
    // Add price for being far from the goal row
    private void addStepCost(){
        for(int i = 0; i < this.n; i++){
            for(int j = 0; j < this.m; j++){
                V[i][j] += (this.n-i)* stepCost;
            }
        }
    }

    // Solve sub-task A
    int SolveA(boolean anyDirection, int n, Picture pic){
        // Create Values matrix for the current situation
        buildV(anyDirection, n);

        // Repaint to allow see the result
        pic.repaint();

        // Find minimal price cell in the first row to start
        double temp = Double.POSITIVE_INFINITY;
        int j = -1;
        for(int i = 0; i < this.m; i++){
            if(V[0][i] < temp){
                temp = V[0][i];
                j = i;
            }
        }

        int i = 0;
        int cost = matrix[i][j];
        visited[i][j]=true;
        // Go through optimal cells until reach the goal
        while (i != n){
            // Find out the best move from the current position
            int[] answer = bestMove(i, j, anyDirection);

            // If got stuck, return
            if(answer[0]<0)return -1;

            // Add new vector into list
            pic.drawLine(i, j, answer[0], answer[1]);
            i = answer[0];
            j = answer[1];
            visited[i][j] = true;

            // Calculate current price of the traverse
            cost += matrix[i][j];
        }
        // Mark all the cells as unvisited
        for(int _j = 0; _j < this.n; _j++){
            for(int k = 0; k < this.m; k++){
                visited[_j][k] = false;
            }
        }
        // Return final value of the traverse ( Should be optimal )
        return cost;
    }
    // Solve sub-task B
    int SolveB(boolean anyDirection, Picture pic){
        // Create Values matrix for the current situation
        buildVB(anyDirection);

        // Repaint to allow see the result
        pic.repaint();


        int j = 0;
        int i = 0;
        int cost = matrix[i][j];
        visited[i][j]=true;
        // Go through optimal cells until reach the goal
        while (i != n-1 || j != m-1){
            // Find out the best move from the current position
            int[] answer = bestMove(i, j, anyDirection);

            // If got stuck, return
            if(answer[0]<0)return -1;

            // Add new vector into list
            pic.drawLine(i, j, answer[0], answer[1]);
            i = answer[0];
            j = answer[1];
            visited[i][j] = true;

            // Calculate current price of the traverse
            cost += matrix[i][j];
        }
        // Mark all the cells as unvisited
        for(int _j = 0; _j < this.n; _j++){
            for(int k = 0; k < this.m; k++){
                visited[_j][k] = false;
            }
        }
        // Return final value of the traverse ( Should be optimal )
        return cost;
    }
}
