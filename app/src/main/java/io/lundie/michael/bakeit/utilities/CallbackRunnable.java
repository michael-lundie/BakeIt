package io.lundie.michael.bakeit.utilities;

/**
 * A simple runnable which provides a callback
 * Class modified from : https://stackoverflow.com/a/826283
 */
public class CallbackRunnable implements Runnable {

    private final RunnableInterface runnableInterface;

    public CallbackRunnable(RunnableInterface runnableInterface) {
        this.runnableInterface = runnableInterface;
    }

    public void run() {
        runnableInterface.onRunCompletion();
    }
}
