package ru.rzn.sbr.javaschool.lesson10.balls;

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class BallWorld extends JPanel {

    private final int xSize = 250;
    private final int ySize = 250;

    private final static Color BGCOLOR = Color.white;

    private ArrayList<Ball> balls = new ArrayList<Ball>();

    public BallWorld() {
        setPreferredSize(new Dimension(xSize, ySize));
        setOpaque(true);
        setBackground(BGCOLOR);
    }

    public void addBall(final Ball b) {
        balls.add(b);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Ball b : balls)
            b.draw(g);
    }

    /**
     * Получить признак "замороженности" всех мячей, за исключением проверяемого. Если мяч единственный, функция возвращает true.
     * Если мяч не единственный, то функция возвращает true только в случае, если остальные мячи "заморожены"
     */
    public synchronized boolean doesAllOthersFreeze(Ball ball) {
        boolean result = true;
        for(Ball item : balls) {
            if (item == ball) continue;
            result = result & item.getFreeze();
        }
        return result;
    }
}
