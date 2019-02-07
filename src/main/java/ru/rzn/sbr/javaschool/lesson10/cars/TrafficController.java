package ru.rzn.sbr.javaschool.lesson10.cars;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TrafficController {
    private Lock lock = new ReentrantLock();
    public void enterLeft() {
        lock.lock();
    }

    public void enterRight() {
        lock.lock();
    }

    public void leaveLeft() {
        lock.unlock();
    }

    public void leaveRight() {
        lock.unlock();
    }
}