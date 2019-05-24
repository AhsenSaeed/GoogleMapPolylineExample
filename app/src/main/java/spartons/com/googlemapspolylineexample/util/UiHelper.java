package spartons.com.googlemapspolylineexample.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Ahsen Saeed}
 * ahsansaeed067@gmail.com}
 * 5/25/19}
 */

public class UiHelper {

    public static MaterialDialog showAlwaysCircularProgressDialog(Context callingClassContext, String content) {
        return new MaterialDialog.Builder(callingClassContext)
                .content(content)
                .progress(true, 100)
                .cancelable(false)
                .show();
    }
}
