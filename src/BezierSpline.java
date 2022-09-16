import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BezierSpline {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static ArrayList<Point2D> points;

    private static ArrayList<Ellipse2D.Double> ellipses;

    private static int pointCount;

    private static int pointCoursorDraw;

    private static boolean needToDrawSpline = false;

    private static double t = 0.0001;

    static class Panel extends JPanel {

        public Panel() {
            this.addMouseListener(new MouseAction());
            this.addMouseMotionListener(new MouseMotion());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(Color.BLACK);

            pointCoursorDraw = 1;

            for (Ellipse2D.Double ellips : ellipses) {
                graphics.fill(ellips);
                graphics.drawString(Integer.toString(pointCoursorDraw++),
                                    (int) ellips.x,
                                    (int) ellips.y);
            }

            if (needToDrawSpline) {
                double[][] linesX = new double[points.size()-1][4];
                double[][] linesY = new double[points.size()-1][4];
                double[] segmentsBetweenPointsLength = new double[points.size() - 1];
                double[][] midpoints = new double[points.size()-1][2];
                double[][] midpointsSemiSegments = new double[points.size()-2][2];
                double[] segmentsCoef = new double[segmentsBetweenPointsLength.length - 1];

                for (int i = 0; i < points.size()-1; i ++) {
                    double segmentLength = Math.sqrt(Math.pow(points.get(i+1).getX()
                                                                      - points.get(i).getX() ,2)
                                                             + Math.pow(points.get(i+1).getY()
                                                                                - points.get(i).getY() ,2));
                    segmentsBetweenPointsLength[i] = segmentLength;

                    midpoints[i][0] = (points.get(i).getX() + points.get(i+1).getX()) / 2;
                    midpoints[i][1] = (points.get(i).getY() + points.get(i+1).getY()) / 2;
                }

                for (int i = 0; i < points.size() - 2; i++) {
                    segmentsCoef[i] = segmentsBetweenPointsLength[i] / segmentsBetweenPointsLength[i + 1];
                    midpointsSemiSegments[i][0] =
                            (midpoints[i][0] + segmentsCoef[i] * midpoints[i + 1][0]) / (1 + segmentsCoef[i]);
                    midpointsSemiSegments[i][1] =
                            (midpoints[i][1] + segmentsCoef[i] * midpoints[i + 1][1]) / (1 + segmentsCoef[i]);
                }


                for (int i = 0; i <= points.size() - 2; i++) {
                    if (i != 0 && i != points.size() - 2) {
                        double firstBiasX = points.get(i).getX() - midpointsSemiSegments[i - 1][0];
                        double firstBiasY = points.get(i).getY() - midpointsSemiSegments[i - 1][1];

                        double secondBiasX = points.get(i + 1).getX() - midpointsSemiSegments[i][0];
                        double secondBiasY = points.get(i + 1).getY() - midpointsSemiSegments[i][1];

                        linesX[i][0] = points.get(i).getX();
                        linesY[i][0] = points.get(i).getY();

                        linesX[i][1] = midpoints[i][0] + firstBiasX;
                        linesY[i][1] = midpoints[i][1] + firstBiasY;
                        graphics.setColor(Color.BLUE);
                        graphics.drawOval((int) linesX[i][1], (int) linesY[i][1], 2,  2 );

                        linesX[i][2] = midpoints[i][0] + secondBiasX;
                        linesY[i][2] = midpoints[i][1] + secondBiasY;
                        graphics.setColor(Color.BLUE);
                        graphics.drawOval((int) linesX[i][2], (int) linesY[i][2], 2,  2 );

                        linesX[i][3] = points.get(i + 1).getX();
                        linesY[i][3] = points.get(i + 1).getY();
                    } else if (i == 0) {
                        double biasX = points.get(i + 1).getX() - midpointsSemiSegments[i][0];
                        double biasY = points.get(i + 1).getY() - midpointsSemiSegments[i][1];

                        linesX[i][0] = points.get(i).getX();
                        linesY[i][0] = points.get(i).getY();

                        linesX[i][1] = points.get(i).getX();
                        linesY[i][1] = points.get(i).getY();

                        linesX[i][2] = midpoints[i][0] + biasX;
                        linesY[i][2] = midpoints[i][1] + biasY;
                        graphics.setColor(Color.BLUE);
                        graphics.drawOval((int) linesX[i][2], (int) linesY[i][2], 2,  2 );

                        linesX[i][3] = points.get(i+1).getX();
                        linesY[i][3] = points.get(i+1).getY();
                    } else {
                        double biasX = points.get(i).getX() - midpointsSemiSegments[i-1][0];
                        double biasY = points.get(i).getY() - midpointsSemiSegments[i-1][1];

                        linesX[i][0] = points.get(i).getX();
                        linesY[i][0] = points.get(i).getY();

                        linesX[i][1] = midpoints[i][0] + biasX;
                        linesY[i][1] = midpoints[i][1] + biasY;

                        graphics.setColor(Color.BLUE);
                        graphics.drawOval((int) linesX[i][1], (int) linesY[i][1], 2,  2 );

                        linesX[i][2] = points.get(i+1).getX();
                        linesY[i][2] = points.get(i+1).getY();

                        linesX[i][3] = points.get(i+1).getX();
                        linesY[i][3] = points.get(i+1).getY();
                    }
                }

                for (int i = 0; i <= points.size() - 2; i++) {
                    double x, y;
                    g.setColor(Color.RED);
                    for (double k = t; k <= 1 + t; k += t) {
                        double r = 1 - k;
                        x = Math.pow(r, 3) * linesX[i][0] + 3 * k * Math.pow(r, 2) * linesX[i][1]
                                + 3 * Math.pow(k, 2) * (1 - k) * linesX[i][2] + Math.pow(k, 3) * linesX[i][3];
                        y = Math.pow(r, 3) * linesY[i][0] + 3 * k * Math.pow(r, 2) * linesY[i][1]
                                + 3 * Math.pow(k, 2) * (1 - k) * linesY[i][2] + Math.pow(k, 3) * linesY[i][3];
                        g.drawOval((int) x, (int) y, 3, 3);
                    }
                }
            }

        }

        class MouseAction extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    pointCount++;

                    double x = e.getX();
                    double y = e.getY();
                    points.add(new Point2D.Double(x, y));
                    ellipses.add(new Ellipse2D.Double(x - 4, y - 4, 14, 14));
                    repaint();
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    needToDrawSpline = !needToDrawSpline;
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {


            }
        }

        class MouseMotion extends MouseMotionAdapter {
            @Override
            public void mouseDragged(MouseEvent e) {

            }
        }
    }

    public BezierSpline() {
        JFrame f = new JFrame();
        f.setTitle("Bezier Splines");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(WIDTH, HEIGHT);
        f.setLocationRelativeTo(null);

        points = new ArrayList<Point2D>();
        ellipses = new ArrayList<Ellipse2D.Double>();

        Panel panel = new Panel();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setBackground(Color.WHITE);

        f.setContentPane(panel);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new BezierSpline();
    }
}
