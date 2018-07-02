package houselights.moffa.com.houselightsapp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by moffa on 25-06-18.
 */

public class SpinnerListener implements AdapterView.OnItemSelectedListener {

    private boolean _firstSelection = true;
    private final OnSelectedItemInterface _i;

    public SpinnerListener(OnSelectedItemInterface i){
        _i = i;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView)adapterView.getChildAt(0)).setTextColor(adapterView.getResources().getColor(R.color.colorText));
        _i.selected(i, _firstSelection);
        _firstSelection = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        return;
    }
}
