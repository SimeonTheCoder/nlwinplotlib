package build;

import data.Array;
import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import operations.Operation;
import parser.Interpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public enum nlwinplotlib implements Operation {
    PING {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, data.Array> arrays, String[] stringTable) throws IOException {
            System.out.println("Pong!");
        }

        @Override
        public String help() {
            return "Ping! Pong!";
        }
    },
    WINMAKE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, data.Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            nlwinplotlib.window = new Window(
                    "Hello, world!", Math.round((Float) instruction[1]), Math.round((Float) instruction[2])
            );
        }

        @Override
        public String help() {
            return "Creates a window with size arg0 arg1";
        }
    },
    PLOT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, data.Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            nlwinplotlib.window.points.add(new Point(
                    Math.round(Interpreter.getValue(instruction[1], memory)),
                    Math.round(Interpreter.getValue(instruction[2], memory))
            ));
        }

        @Override
        public String help() {
            return "Plots a point on the window with coordinates (arg0, arg1)";
        }
    },
    CLEARWIN {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, data.Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            nlwinplotlib.window.points.clear();

            nlwinplotlib.window.lineStarts.clear();
            nlwinplotlib.window.lineEnds.clear();
        }

        @Override
        public String help() {
            return "Clears the window";
        }
    },
    LINE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER, ObjType.NUMBER, ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            nlwinplotlib.window.lineStarts.add(new Point(
                    Math.round(Interpreter.getValue(instruction[1], memory)),
                    Math.round(Interpreter.getValue(instruction[2], memory))
            ));
            nlwinplotlib.window.lineEnds.add(new Point(
                    Math.round(Interpreter.getValue(instruction[3], memory)),
                    Math.round(Interpreter.getValue(instruction[4], memory))
            ));

            float thickness = 1;

            if(instruction[5] != null) {
                thickness = Interpreter.getValue(instruction[5], memory);
            }

            nlwinplotlib.window.thickness.add(thickness);
        }

        @Override
        public String help() {
            return "Draws a line from (arg0, arg1) to (arg2, arg3)";
        }
    }, SVGEXPORT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[] {ObjType.STRING};
        }

        @Override
        public void execute(Object[] instruction, float[] memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles, HashMap<String, Array> arrays, String[] stringTable) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            StringBuilder builder = new StringBuilder();

            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            builder.append(System.lineSeparator());
            builder.append(String.format("""
                    <svg
                       width="%dmm"
                       height="%dmm"
                       xmlns:xlink="http://www.w3.org/1999/xlink"
                       xmlns="http://www.w3.org/2000/svg"
                       xmlns:svg="http://www.w3.org/2000/svg">
                    """, window.sizeX, window.sizeY));

            builder.append(System.lineSeparator());

            builder.append(
                String.format("""
                    <defs>
                        <clipPath id="clipBox">
                          <rect x="0" y="0" width="%d" height="%d" />
                        </clipPath>
                      </defs>""", window.sizeX, window.sizeY
                )
            );

            builder.append(System.lineSeparator());

            for (int i = 0; i < window.lineStarts.size(); i ++) {
                int a = window.lineStarts.get(i).x;
                int b = window.lineStarts.get(i).y;
                int c = window.lineEnds.get(i).x;
                int d = window.lineEnds.get(i).y;
                float e = window.thickness.get(i);

                if(a < -100 || a > window.sizeX + 100 || b < -100 || b > window.sizeY + 100 || c < -100 || c > window.sizeX + 100 || d < -100 || d > window.sizeY + 100) continue;

                builder.append(
                        String.format(
                            "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:black;stroke-width:%d\" clip-path=\"url(#clipBox)\" />",
                                a, b, c, d, (int) e
                        )
                );
                builder.append(System.lineSeparator());
            }

            builder.append("</svg>");

            FileWriter fileWriter = new FileWriter((String) instruction[1]);

            fileWriter.write(builder.toString());
            fileWriter.close();
        }

        @Override
        public String help() {
            return "Exports the resulting image to SVG file format";
        }
    };

    public static Window window;

    public nlwinplotlib value(String str) {
        switch (str) {
            case "PING":
                return PING;

            case "WINMAKE":
                return WINMAKE;

            case "PLOT":
                return PLOT;

            case "CLEARWIN":
                return CLEARWIN;

            case "LINE":
                return LINE;

            default:
                return null;
        }
    }

    nlwinplotlib() {
    }

    float xOffset = 0, yOffset = 0;
    float scale = 1;

    class WindowListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'a' -> xOffset += 20;
                case 'd' -> xOffset -= 20;
                case 'w' -> yOffset += 20;
                case 's' -> yOffset -= 20;
                case 'z' -> scale += 0.1f;
                case 'x' -> scale -= 0.1f;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

    class Window extends JPanel {
        public java.util.List<Point> points;
        public java.util.List<Point> lineStarts;
        public java.util.List<Point> lineEnds;
        public java.util.List<Float> thickness;

        public int sizeX, sizeY;

        public Window(String title, int sizeX, int sizeY) {
            points = new ArrayList<>();

            lineStarts = new ArrayList<>();
            lineEnds = new ArrayList<>();
            thickness = new ArrayList<>();

            JFrame frame = new JFrame(title);
            frame.setSize(sizeX, sizeY);

            this.sizeX = sizeX;
            this.sizeY = sizeY;

            frame.add(this);
            frame.addKeyListener(new WindowListener());

            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            super.paint(g);

            for (int i = 0; i < points.size(); i ++) {
                int x = (int) (points.get(i).x * scale + xOffset);
                int y = (int) (points.get(i).y * scale + yOffset);

                g2d.fillRect(x, y, 5, 5);
            }

            for (int i = 0; i < lineStarts.size(); i ++){
                int x1 = (int) (lineStarts.get(i).x * scale + xOffset);
                int y1 = (int) (lineStarts.get(i).y * scale + yOffset);
                int x2 = (int) (lineEnds.get(i).x * scale + xOffset);
                int y2 = (int) (lineEnds.get(i).y * scale + yOffset);

                g2d.setStroke(new BasicStroke(thickness.get(i)));
                g2d.drawLine(x1, y1, x2, y2);
            }

            repaint();
        }
    }
}
