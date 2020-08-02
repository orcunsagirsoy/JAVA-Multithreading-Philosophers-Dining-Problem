package orcunsagirsoy;

import sun.java2d.windows.GDIRenderer;

import java.util.Random;

public class Philosopher extends Thread {

    private final Random random = new Random();

    private final int number;
    private final Fork leftFork;
    private final Fork rightFork;

    private PhilosopherEventHandler philosopherEventHandler = null;

    public Philosopher(int number, Fork leftFork, Fork rightFork) {
        this.number = number;
        this.leftFork = leftFork;
        this.rightFork = rightFork;

        setName("philosopher-" + number);
        setDaemon(true);
    }

    public int getNumber() {
        return number;
    }

    public Fork getLeftFork() {
        return leftFork;
    }

    public Fork getRightFork() {
        return rightFork;
    }

    public PhilosopherEventHandler getPhilosopherEventHandler() { return philosopherEventHandler; }

    public void setPhilosopherEventHandler(PhilosopherEventHandler philosopherEventHandler) {
        this.philosopherEventHandler = philosopherEventHandler;
    }

    private void sleepMe(long ms){

        try
        {

            Thread.sleep(ms);
        }
        catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void eat()
    {
        if(philosopherEventHandler != null)
        {
            philosopherEventHandler.eating(this);
        }
        sleepMe(2500L + random.nextInt(5000));

        if (philosopherEventHandler != null) {
            philosopherEventHandler.thinking(this);
        }
    }

    private void forkTaken(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null) {

            philosopherEventHandler.forkTaken(this, fork, direction);
        }
    }

    private void forkReleased(Fork fork, Direction direction)
    {
        if (philosopherEventHandler != null) {

            philosopherEventHandler.forkReleased(this, fork, direction);
        }
    }

    @Override
    public void run() {
        while(true){

            synchronized (leftFork)
            {
                // Left side fork handled

                forkTaken(leftFork, Direction.LEFT);

                synchronized (rightFork)
                {
                    //Right side fork handled
                    forkTaken(rightFork, Direction.RIGHT);
                    // Lets eat
                    eat();

                    // Leave the right fork
                    forkReleased(rightFork, Direction.RIGHT);
                }

                // Leave the left fork
                forkReleased(leftFork, Direction.LEFT);
            }

            //Wait 2sec to others can have the forks
            sleepMe(2000L);
        }
    }
}
