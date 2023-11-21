package course.concurrency.m5_streams;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
    Практика: экзекьюторы и очереди
    Задание на проверку
    Обязательное
    Откройте класс ThreadPoolTask

    Создайте экземпляр экзекьютора, который обрабатывает задачи по принципу LIFO
    Верните его в методе getLifoExecutor()

    Создайте экземпляр экзекьютора, который содержит 8 потоков и отбрасывает задачи, если нет свободных потоков для их обработки
    Верните его в методе getRejectExecutor()

    Проверьте свои решения с помощью тестов ThreadPoolTaskTests
*/
public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        var stack = new LinkedBlockingDeque<Runnable>() {
            @Override
            public boolean offer(Runnable e) {
                return offerFirst(e);
            }
        };

        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                stack);
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(8, 8,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }
}
