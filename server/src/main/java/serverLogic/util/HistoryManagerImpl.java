package serverLogic.util;


import util.HistoryManager;

import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HistoryManagerImpl implements HistoryManager {
    private static final int CAPACITY = 6;
    private final Queue<String> history = new ArrayBlockingQueue<>(CAPACITY);
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public void addNote(String note) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (history.size() == CAPACITY) {
                history.remove();
            }
            history.add(note);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String niceToString() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add("The last 6 commands were:");
        for (String commandName : history) {
            stringJoiner.add(commandName);
        }
        return stringJoiner.toString();
    }
}
