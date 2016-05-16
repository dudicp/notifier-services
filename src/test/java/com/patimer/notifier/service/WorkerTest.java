package com.patimer.notifier.service;

public class WorkerTest implements Runnable
{
    private boolean isDone = false;
    private boolean simulateException = false;

    public WorkerTest(){}

    public WorkerTest(boolean simulateException)
    {
        this.simulateException = simulateException;
    }

    @Override
    public void run()
    {
        System.out.println("Running WorkerTest");
        sleep(1000);
        if(simulateException)
            throw new RuntimeException("unit-test");

        isDone = true;
    }

    public boolean isDone()
    {
        return isDone;
    }

    private void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
