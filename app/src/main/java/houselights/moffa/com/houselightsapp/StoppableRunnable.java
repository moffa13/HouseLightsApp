package houselights.moffa.com.houselightsapp;

/**
 * Created by moffa on 11-07-18.
 */

public class StoppableRunnable implements Runnable {

    private boolean _stopped = false;

    public void stop(){
        _stopped = true;
    }

    @Override
    public void run() {
        if(_stopped) return;
    }
}
