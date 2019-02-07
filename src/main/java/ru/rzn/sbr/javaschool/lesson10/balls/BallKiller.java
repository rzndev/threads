package ru.rzn.sbr.javaschool.lesson10.balls;

public class BallKiller implements Runnable {

    BallWorld world;

    public BallKiller(BallWorld world) {
        this.world = world;
    }

    @Override
    public void run() {
        boolean isCaseA = true;
        while(true) {
            try {
                Thread.sleep((long)(5000 * Math.random()));
                if (isCaseA)
                    killRandomBallCaseA();
                else
                    killRandomBallCaseB();
                isCaseA = !isCaseA;

            } catch (InterruptedException e) {
                e.printStackTrace();System.err.println("Thread " + Thread.currentThread().getName() + " throwed exception " + e.getMessage());
            }
        }
    }

    /**
     * а) исключение из массива {@link BallWorld#balls} (нужно реализовать потокобезопасный вариант)
     */
    private void killRandomBallCaseA() {
        if (world.killRandomBallCaseA())
            System.out.println("ball removed case A");
    }

    /**
     * б) завершение потока, в котором выполняется соответствующая задача (следует использовать
     * {@link java.util.concurrent.Future}сформированный при запуске потока для прерывания
     * {@link java.util.concurrent.Future#cancel(boolean)})     */
    private void killRandomBallCaseB() {
        if (world.killRandomBallCaseB())
            System.out.println("ball removed case B");
    }
}
