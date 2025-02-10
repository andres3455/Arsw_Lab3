package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.BlockingQueue;

/**
 * Consumidor que extrae datos lentamente.
 */
public class Consumer extends Thread {
    private final BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int elem = queue.take(); // Bloquea si la cola está vacía
                System.out.println("Consumer consumes: " + elem);
                Thread.sleep(2000); // Simula consumo lento
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
