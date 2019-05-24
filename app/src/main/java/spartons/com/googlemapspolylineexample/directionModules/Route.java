package spartons.com.googlemapspolylineexample.directionModules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Ahsen Saeed on 5/15/2017.
 */

public class Route {

    Distance distance;
    Duration duration;
    public LatLng endLocation;
    LatLng startLocation;

    public List<LatLng> points;
}
