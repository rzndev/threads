package ru.rzn.sbr.javaschool.lesson10.balls;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class Ball implements Runnable {

    BallWorld world;

    private volatile boolean visible = false;

    private int xpos, ypos, xinc, yinc;

    private final Color col;

    private final static int BALLW = 10;
    private final static int BALLH = 10;

    /**
     * Признак нахождения мяча ниже диагонали
     */
    private boolean isBelowDiagonal;

    /**
     * Признак заморозки
     */
    private volatile boolean isFreeze = false;

    /**
     * Самоблокировка с обратным отсчетом для выполнения запуска потока BallKiller после запуска всех потоков
     * (используется для выполнения задания 3)
     */
    private CountDownLatch cdl;

    public boolean getFreeze() {
        return isFreeze;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public Ball(BallWorld world, int xpos, int ypos, int xinc, int yinc, Color col, CountDownLatch cdl) {
        this.cdl = cdl;
        this.world = world;
        this.xpos = xpos;
        this.ypos = ypos;
        this.xinc = xinc;
        this.yinc = yinc;
        this.col = col;
        this.isBelowDiagonal = checkIsBelowDiagonal();
        this.isFreeze = false;
        world.addBall(this);
    }

    @Override
    public void run() {
        this.visible = true;
        cdl.countDown();
        try {
            while (!Thread.interrupted() && visible) {
                move();
            }
        } catch (InterruptedException e ){
            System.err.println("Thread " + Thread.currentThread().getName() + " throwed exception " + e.getMessage());
        } finally {
            visible = false;
            world.repaint();
            world.wakeUpAllFreezedBalls(this); // при завершении потока мяча необходимо убедиться, что хотя бы один
                                               // поток другого мяча активен
        }
    }

    public void move() throws InterruptedException {
        if (xpos >= world.getWidth() - BALLW || xpos <= 0) xinc = -xinc;

        if (ypos >= world.getHeight() - BALLH || ypos <= 0) yinc = -yinc;

        Thread.sleep(30);
        doMove();
        world.repaint();
        // п.2 задания. Добавлено для заморозки потока при пересечении мячом диагонали.
        if (checkIsBelowDiagonal() != isBelowDiagonal) {
            isBelowDiagonal = checkIsBelowDiagonal();
            synchronized (world) {
                if (!world.wakeUpAllFreezedBalls(this)) {
                    isFreeze = true;
                    world.wait();
                    isFreeze = false;
                }
            }
        }
    }

    /**
     * Мяч находится нниже диагонали
     * @return true, если мяч находится ниже диагонали; false в любом другом месте
     */
    private boolean checkIsBelowDiagonal() {
        return xpos + BALLW / 2 <= ypos + BALLW;
    }

    public synchronized void doMove() {
        xpos += xinc;
        ypos += yinc;
    }

    public synchronized void draw(Graphics g) {
        if (visible) {
            g.setColor(col);
            g.fillOval(xpos, ypos, BALLW, BALLH);
        }
    }
}
