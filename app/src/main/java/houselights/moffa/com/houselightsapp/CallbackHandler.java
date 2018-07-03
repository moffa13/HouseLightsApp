package houselights.moffa.com.houselightsapp;

import java.util.ArrayList;

/**
 * Created by moffa on 02-07-18.
 */

interface ActionInterface {
    void action(boolean error);
}


public class CallbackHandler<T> {

    private ArrayList<Callback> _callbacks = new ArrayList<>();
    public CallbackHandler(){

    }

    public CallbackHandler<T> registerCallback(Callback callback){
        _callbacks.add(callback);
        return this;
    }

    public void run(final ActionInterface ai){
        Callback callback = _callbacks.remove(0);
        callback.run(new ActionInterface() {
            @Override
            public void action(boolean error) {
                if(!error){
                    if(!_callbacks.isEmpty())
                        run(ai);
                    else
                        ai.action(false);
                }else{
                    ai.action(true);
                    return;
                }
            }
        });
    }

}
