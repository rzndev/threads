package ru.rzn.sbr.javaschool.lesson10.balls;

import java.awt.Color;
import java.util.concurrent.*;
import javax.swing.*;

/**
 * 1. Измените метода {@link Solution#main(String[])} таким образом, чтобы вместо явного создания потоков использовался
 * какой-нибудь {@link java.util.concurrent.Executor}.
 * 2. Реализуйте последовательую "заморозку" потоков при попадании {@link Ball} на диагональ {@link BallWorld}
 * (где x == y). Попаданием считать пересечение описывающего прямоуголькника диагонали. При заморозке всех потоков
 * осуществляйте возобновление выполнения
 * 3. Введите в программу дополнительный поток, который уничтожает {@link Ball} в случайные моменты времени.
 * Начните выполнение этого потока c задержкой в 15 секунд после старта всех {@link Ball}. {@link Ball} должны
 * уничтожаться в случайном порядке. Под уничтожением {@link Ball} подразумевается
 * а) исключение из массива {@link BallWorld#balls} (нужно реализовать потокобезопасный вариант)
 * б) завершение потока, в котором выполняется соответствующая задача (следует использовать
 * {@link java.util.concurrent.Future}сформированный при запуске потока для прерывания
 * {@link java.util.concurrent.Future#cancel(boolean)})
 */
public class Solution {

    public static void nap(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.err.println("Thread " + Thread.currentThread().getName() + " throwed exception " + e.getMessage());
        }
    }

    public static void main(String[] a) {

        final BallWorld world = new BallWorld();
        final JFrame win = new JFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                win.getContentPane().add(world);
                win.pack();
                win.setVisible(true);
            }
        });

        // cdl используется для проверки запуска всех потоков
        CountDownLatch cdl = new CountDownLatch(4);

        //  * 1. Изменен метод {@link Solution#main(String[])} таким образом, чтобы вместо явного создания потоков используется
        // * Executors.newScheduledThreadPool
        ExecutorService pool = Executors.newScheduledThreadPool(5);
        nap((int) (5000 * Math.random()));
        world.addTask(pool.submit(new Ball(world, 50, 80, 5, 10, Color.red, cdl)));
        nap((int) (5000 * Math.random()));
        world.addTask(pool.submit(new Ball(world, 70, 100, 8, 6, Color.blue, cdl)));
        nap((int) (5000 * Math.random()));
        world.addTask(pool.submit(new Ball(world, 150, 100, 9, 7, Color.green, cdl)));
        nap((int) (5000 * Math.random()));
        world.addTask(pool.submit(new Ball(world, 200, 130, 3, 8, Color.black, cdl)));

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((ScheduledExecutorService) pool).schedule(new BallKiller(world), 15, TimeUnit.SECONDS);
        Thread.currentThread().setName("MyMainThread");
        nap((int) (5000 * Math.random()));
    }
}
