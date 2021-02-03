package com.project.ams.automatedmess;
import android.util.SparseIntArray;

public class ShareNumberPickersValues {

    // Declare an ArrayList to contain the NumberPickers values
    public SparseIntArray numberPickerVals = new SparseIntArray();

    // Its from here that we are using the singleton pattern to share these number picker values
    private static ShareNumberPickersValues instance = null;
    private void ShareNumberPickersValues() { }

    public static ShareNumberPickersValues getInstance() {
        if (instance == null) {
            instance = new ShareNumberPickersValues();
        }

        return instance;
    }

}
