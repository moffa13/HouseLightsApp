package houselights.moffa.com.houselightsapp;

/**
 * Created by moffa on 29-06-18.
 */

public interface APIActionInterface<T>{
    void action(boolean error, T t);
}