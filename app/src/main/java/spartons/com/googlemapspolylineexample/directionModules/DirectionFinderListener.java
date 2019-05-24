package spartons.com.googlemapspolylineexample.directionModules;

import java.util.List;

/**
 * Created by Ahsen Saeed on 5/15/2017.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
