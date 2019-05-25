package spartons.com.googlemapspolylineexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.List;

import spartons.com.googlemapspolylineexample.directionModules.DirectionFinder;
import spartons.com.googlemapspolylineexample.directionModules.DirectionFinderListener;
import spartons.com.googlemapspolylineexample.directionModules.Route;

import static spartons.com.googlemapspolylineexample.util.GoogleMapHelper.buildCameraUpdate;
import static spartons.com.googlemapspolylineexample.util.GoogleMapHelper.defaultMapSettings;
import static spartons.com.googlemapspolylineexample.util.GoogleMapHelper.getDefaultPolyLines;
import static spartons.com.googlemapspolylineexample.util.GoogleMapHelper.getDottedPolylines;
import static spartons.com.googlemapspolylineexample.util.UiHelper.showAlwaysCircularProgressDialog;

public class MainActivity extends AppCompatActivity implements DirectionFinderListener {

    private enum PolylineStyle {
        DOTTED,
        PLAIN
    }

    private static final String[] POLYLINE_STYLE_OPTIONS = new String[]{
            "PLAIN",
            "DOTTED"
    };

    private PolylineStyle polylineStyle = PolylineStyle.PLAIN;

    private GoogleMap googleMap;
    private Polyline polyline;
    private EditText sourceAddressTextView, destinationAddressTextView;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            defaultMapSettings(googleMap);
            this.googleMap = googleMap;
        });
        sourceAddressTextView = findViewById(R.id.sourceAddressTextView);
        destinationAddressTextView = findViewById(R.id.destinationAddressTextView);

        AppCompatSpinner polylineStyleSpinner = findViewById(R.id.polylineStyleSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, POLYLINE_STYLE_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        polylineStyleSpinner.setAdapter(adapter);
        polylineStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    polylineStyle = PolylineStyle.PLAIN;
                else if (position == 1)
                    polylineStyle = PolylineStyle.DOTTED;
                if (polyline == null || !polyline.isVisible())
                    return;
                List<LatLng> points = polyline.getPoints();
                polyline.remove();
                if (position == 0)
                    polyline = googleMap.addPolyline(getDefaultPolyLines(points));
                else if (position == 1)
                    polyline = googleMap.addPolyline(getDottedPolylines(points));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.getDirectionButton).setOnClickListener(view -> {
            String origin = sourceAddressTextView.getText().toString();
            String destination = destinationAddressTextView.getText().toString();
            if (origin.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Please first fill all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!origin.contains(",") || !destination.contains(",")) {
                Toast.makeText(this, "Invalid data fill in fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchDirections(origin, destination);
        });
    }

    private void fetchDirections(String origin, String destination) {
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        if (materialDialog == null)
            materialDialog = showAlwaysCircularProgressDialog(this, "Fetching Directions...");
        else materialDialog.show();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (materialDialog != null && materialDialog.isShowing())
            materialDialog.dismiss();
        if (!routes.isEmpty() && polyline != null) polyline.remove();
        try {
            for (Route route : routes) {
                PolylineOptions polylineOptions = getDefaultPolyLines(route.points);
                if (polylineStyle == PolylineStyle.DOTTED)
                    polylineOptions = getDottedPolylines(route.points);
                polyline = googleMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
        googleMap.animateCamera(buildCameraUpdate(routes.get(0).endLocation), 10, null);
    }
}
