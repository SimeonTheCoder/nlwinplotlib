package build;

import data.ObjType;
import data.ReadableFile;
import data.WritableFile;
import operations.Operation;
import parser.Interpreter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public enum CustomOperation implements Operation {
    PING {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException {
            System.out.println("Pong!");
        }
    },
    WINMAKE {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            CustomOperation.window = new Window(
                    "Hello, world!", Math.round((Float) instruction[1]), Math.round((Float) instruction[2])
            );
        }
    },
    PLOT {
        @Override
        public ObjType[] getArguments() {
            return new ObjType[]{ObjType.NUMBER, ObjType.NUMBER};
        }

        @Override
        public void execute(Object[] instruction, HashMap<String, Float> memory, HashMap<String, WritableFile> writableFiles, HashMap<String, ReadableFile> readableFiles) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
            CustomOperation.window.points.add(new Point(
                    Math.round(Interpreter.getValue(instruction[1], memory)),
                    Math.round(Interpreter.getValue(instruction[2], memory))
            ));
        }
    };

    public static Window window;

    public CustomOperation value(String str) {
        switch (str) {
            case "PING":
                return PING;

            case "WINMAKE":
                return WINMAKE;

            case "PLOT":
                return PLOT;

            default:
                return null;
        }
    }

    CustomOperation() {
    }

    class Window extends JPanel {
        public java.util.List<Point> points;

        public Window(String title, int sizeX, int sizeY) {
            points = new ArrayList<>();

            JFrame frame = new JFrame(title);
            frame.setSize(sizeX, sizeY);

            frame.add(this);

            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for (int i = 0; i < points.size(); i ++) {
                g.fillRect(points.get(i).x, points.get(i).y, 5, 5);
            }

            repaint();
        }
    }
}