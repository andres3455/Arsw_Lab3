package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    private volatile boolean paused = false;

    private volatile boolean alive = true;

    private volatile boolean stopped = false;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (!stopped && !Thread.interrupted()) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            Immortal im;
            synchronized (immortalsPopulation) {
                if (immortalsPopulation.size() < 2) {
                    return;
                }

                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = (nextFighterIndex + 1) % immortalsPopulation.size();
                }

                im = immortalsPopulation.get(nextFighterIndex);
            }

            fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void fight(Immortal i2) {

        Immortal firstLock = this;
        Immortal secondLock = i2;
        if (this.hashCode() > i2.hashCode()) {
            firstLock = i2;
            secondLock = this;
        }

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (i2.isAlive() && i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");

                    // Si el oponente muere, lo marcamos como inactivo
                    if (i2.getHealth() <= 0) {
                        i2.alive = false;
                    }
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }

    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void pauseImmortal() {
        paused = true;
    }

    public synchronized void resumeImmortal() {
        paused = false;
        notify();
    }

    public boolean isImAlive() {
        return alive;
    }

    public void stopImmortal() {
        stopped = true;
    }

}
