package ru.rzn.sbr.javaschool.lesson10.balls;

import java.awt.*;
import javax.swing.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

public class BallWorld extends JPanel {

    private final int xSize = 250;
    private final int ySize = 250;

    private final static Color BGCOLOR = Color.white;

    private CopyOnWriteArrayList<Ball> balls = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Future<?>> tasks = new CopyOnWriteArrayList<>();

    public BallWorld() {
        setPreferredSize(new Dimension(xSize, ySize));
        setOpaque(true);
        setBackground(BGCOLOR);
    }

    public void addTask(Future<?> task) {
        tasks.add(task);
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
     * п.2 задания. Получить признак "замороженности" всех мячей, за исключением проверяемого. Если мяч единственный, функция возвращает true.
     * Если мяч не единственный, то функция возвращает true только в случае, если остальные мячи "заморожены"
     * В случае, если все остальные мячи "заморожены", или мяч единственный, вызывается notifyAll для "пробуждения" всех мячей
     */
    public synchronized boolean wakeUpAllFreezedBalls(Ball ball) {
        boolean result = true;
        for(Ball item : balls) {
            if (item == ball) continue; // пропускаем проверяемый мяч
            if (!item.getVisible()) continue; // пропускаем мяч, который не отображается
            result = result & item.getFreeze();
        }
        if (result) {
            this.notifyAll();
        }

        return result;
    }

    /**
     * а) исключение из массива {@link BallWorld#balls} (нужно реализовать потокобезопасный вариант)
     */
    public boolean killRandomBallCaseA() {
        boolean removed = false;
        for (Ball item : balls) {
            if (!item.getVisible()) continue; // если мяч не отображается, не анализируем его
            if (Math.random() > 0.5) {
                balls.remove(item);
                item.setVisible(false); // в случае, если мяч становится не видимым, его поток завершается
                removed = true;
                break;
            }
        }
        // Если ни одного мяча не удалили, удаляем первый в списке
        if (!removed) {
            for (Ball item : balls) {
                if (!item.getVisible()) continue;
                removed = true;
                balls.remove(item);
                break;
            }
        }
        return removed;
    }

    public boolean killRandomBallCaseB() {
        boolean removed = false;
        for(Future<?> task : tasks){
            if (!task.isDone()) {
                if (Math.random() > 0.5) {
                    task.cancel(true);
                    removed = true;
                    break;
                }
            }
        }
        // Если ни одного мяча не удалили, удаляем первый в списке
        if (!removed){
            for(Future<?> task : tasks) {
                if (!task.isDone()) {
                    removed = true;
                    task.cancel(true);
                    break;
                }
            }
        }
        return removed;
    }
}
