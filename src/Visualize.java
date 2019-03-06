import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Class creating map of the gridWorld
class Picture extends JPanel{
    private int width, height;  // Size of map
    private int xSize, ySize;   // Size of a grid cell
    private ArrayList<int[]> xy;    // List of current paths points
    private GridWorld gw;   // The current gridWorld
    Picture(int width, int height, GridWorld _gw){
        xy = new ArrayList<>();
        gw = _gw;
        this.width = width;
        this.height = height;
        xSize = Math.max(1, width/gw.m);
        ySize = Math.max(1, height/gw.n);
    }
    public void repaint(){
        if(xy != null)xy.clear();   // Delete all points of the solution
        super.repaint();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.clearRect(0, 0, width, height);
        double[][] colors = gw.normalizedV();
        for(int i = 0; i < gw.n; i++){
            for(int j = 0; j < gw.m; j++){
                // The greater cost the more of red and less of blue
                // But values must always be in range [0; 1]
                g.setColor(new Color(Math.min(1, Math.max(0, (float)colors[i][j])),
                        Math.min(1,Math.max(0, (float)colors[i][j]/100)),
                        Math.min(1, Math.max(0, 1.f-(float)colors[i][j]))));
                g.fillRect((j* xSize), i* ySize, xSize, ySize);

                // Write the value of the cell
                g.setColor(Color.CYAN);
                g.setFont(new Font("Arial", Font.BOLD, ySize -2));
                String num = Integer.toString(gw.matrix[i][j]);
                g.drawString(num, (j)* xSize + xSize /2-num.length()*(ySize -2)/4, (i+1)* ySize -(ySize -2)/8);
            }
        }
        // For each point of solution
        for(int[] a : xy){
            // Draw line from the start point to the end point
            g.setColor(Color.CYAN);
            g.drawLine(a[1]+ xSize /2, a[0]+ ySize /2, a[3]+ xSize /2, a[2]+ ySize /2);
            // Draw borders of starting rectangle
            g.setColor(Color.CYAN);
            g.drawRect(a[1]+3, a[0]+3, xSize -6, ySize -6);
        }
        // Draw borders of the last rectangle
        if(xy != null && xy.size() > 0)
            g.drawRect(xy.get(xy.size()-1)[3]+3, xy.get(xy.size()-1)[2]+3, xSize -6, ySize -6);

    }
    // Change the world
    void setNewWorld(GridWorld _gw){
        gw = _gw;
        xSize = Math.max(1, width/gw.m);
        ySize = Math.max(1, height /gw.n);
        xy.clear();     // Clear outdated points
    }
    // Add points into the list
    void drawLine(int i1, int j1, int i2, int j2){
        xy.add(new int[]{i1* ySize, j1* xSize, i2* ySize, j2* xSize});
    }

}

// Some Java AWT magic
class Visualize extends JFrame{
    private JLabel ScoreField;
    private JTextField NField, MField;
    private GridWorld gw;
    private Picture pic;
    Visualize(GridWorld _gw){
        gw = _gw;
        gw.run();
        int width = 1280, height = 800;
        pic = new Picture(width, height /5*4, gw);

        JLabel NLabel = new JLabel("N = ");
        JLabel MLabel = new JLabel("M = ");
        ScoreField = new JLabel("");
        NField = new JTextField("20");
        MField = new JTextField("10");

        JButton generate = new JButton("Generate");
        JButton taskA1 = new JButton("Task A(1)");
        JButton taskA2 = new JButton("Task A(2)");
        JButton taskB1 = new JButton("Task B(1)");
        JButton taskB2 = new JButton("Task B(2)");

        generate.addActionListener(e -> {
            new Thread(() ->
                    gw = new GridWorld(Integer.parseInt(NField.getText()),
                            Integer.parseInt(MField.getText()))).run();
            pic.setNewWorld(gw);
            repaint();
        });

        taskA1.addActionListener(e -> new Thread(() ->
                ScoreField.setText("Score = " + gw.SolveA(false, gw.n - 1, pic) + "    ")).run());
        taskA2.addActionListener(e -> new Thread(() ->
                ScoreField.setText("Score = " + gw.SolveA(true, gw.n - 1, pic) + "    ")).run());
        taskB1.addActionListener(e -> new Thread(() ->
                ScoreField.setText("Score = " + gw.SolveB(false, pic) + "    ")).run());
        taskB2.addActionListener(e -> new Thread(() ->
                ScoreField.setText("Score = " + gw.SolveB(true, pic) + "    ")).run());


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setSize(width, height /5);

        controlPanel.add(ScoreField);
        controlPanel.add(NLabel);
        controlPanel.add(NField);
        controlPanel.add(MLabel);
        controlPanel.add(MField);
        controlPanel.add(generate);
        controlPanel.add(taskA1);
        controlPanel.add(taskA2);
        controlPanel.add(taskB1);
        controlPanel.add(taskB2);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));


        wrapper.add(controlPanel);

        pic.setPreferredSize(new Dimension(width, height /5*4));
        wrapper.add(pic);


        add(wrapper);

        setSize(width, height);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }
}

