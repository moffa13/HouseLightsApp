package houselights.moffa.com.houselightsapp;

import java.util.ArrayList;

/**
 * Created by moffa on 02-07-18.
 */

interface ActionInterface {
    void action(boolean error);
}


public class CallbackHandler<T> {

    private int _i = 0;

    private ArrayList<Callback> _callbacks = new ArrayList<>();
    public CallbackHandler(){

    }

    public CallbackHandler<T> registerCallback(Callback callback){
        _callbacks.add(callback);
        return this;
    }

    public void reset(){
        _i = 0;
    }

    public void stop(){
        reset();
        _callbacks.clear();
    }

    public void run(final ActionInterface ai){
        Callback callback = _callbacks.get(_i++);
        callback.run(new ActionInterface() {
            @Override
            public void action(boolean error) {
                if(!error){
                    if(_i < _callbacks.size())
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
