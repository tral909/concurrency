package course.concurrency.m6_1_myblockingqueue;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Тестов не добавил, вообще хз как это тестировать))
 */
public class Main {

    final static LinkedList<Integer> QUEUE = new LinkedList<>();

    final static MyBlockingQueue<Integer> MY_QUEUE = new MyBlockingQueue<>(3);

    // Real blocking queue
    public static void main(String[] args) {
        new ThinPublisher().start();
        new ThinSubscriber().start();
        sleep(2_500);
        System.exit(0);
    }

    // Queue - LinkedList + synchronizations, wait, notify on the publisher and subscriber side
    public static void main2(String[] args) {
        new ThickPublisher().start();
        new ThickSubscriber().start();
        sleep(2_500);
        System.exit(0);
    }

    static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

class MyBlockingQueue<T> {

    private final LinkedList<T> queue;

    private final int size;

    MyBlockingQueue(int size) {
        if (size < 1) throw new IllegalArgumentException("size must be more than 0");
        this.size = size - 1;
        queue = new LinkedList<>();
    }

    public synchronized void enqueue(T value) {
        if (queue.size() >= size) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.notify();
        queue.addFirst(value);
    }

    public synchronized T dequeue()  {
        if (queue.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.notify();
        return queue.removeLast();
    }
}

class ThinPublisher extends Thread {

    @Override
    public void run() {
        while (true) {
            Integer el = new Random().nextInt(101);
            Main.sleep(ThreadLocalRandom.current().nextInt(500));
            System.out.println(Thread.currentThread().getName() + " added elem: " + el);
            Main.MY_QUEUE.enqueue(el);
        }
    }
}

class ThinSubscriber extends Thread {

    @Override
    public void run() {
        while (true) {
            Main.sleep(ThreadLocalRandom.current().nextInt(500));
            System.out.println(Thread.currentThread().getName() + " removed elem: " + Main.MY_QUEUE.dequeue());
        }
    }
}

class ThickPublisher extends Thread {

    @Override
    public void run() {
        while (true) {
            Integer el = new Random().nextInt(101);
            synchronized (Main.QUEUE) {
                Main.sleep(100);
                Main.QUEUE.addFirst(el);
                System.out.println(Thread.currentThread().getName() + " added elem: " + el);
                Main.QUEUE.notify();
            }
        }
    }
}

class ThickSubscriber extends Thread {

    @Override
    public void run() {
        while (true) {
            synchronized (Main.QUEUE) {
                Main.sleep(100);
                System.out.println(Thread.currentThread().getName() + " removed elem: " + Main.QUEUE.removeLast());
                try {
                    Main.QUEUE.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}