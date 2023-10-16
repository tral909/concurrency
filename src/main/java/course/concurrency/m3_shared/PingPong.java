package course.concurrency.m3_shared;

import static java.lang.Thread.sleep;

public class PingPong {

    public static synchronized void ping() {
        while(true) {
            try {
                sleep(10);
                PingPong.class.notify();
                System.out.println("Ping");
                PingPong.class.wait();
            } catch (InterruptedException ignore) {}
        }
    }

    public static synchronized void pong() {
        while(true) {
            try {
                sleep(15);
                PingPong.class.notify();
                System.out.println("Pong");
                PingPong.class.wait();
            } catch (InterruptedException ignore) {}
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> ping());
        Thread t2 = new Thread(() -> pong());
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        try {
            sleep(1_000);
        } catch (InterruptedException ignore) {}

    }
}
