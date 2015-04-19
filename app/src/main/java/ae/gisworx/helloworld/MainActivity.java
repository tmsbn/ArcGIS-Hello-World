package ae.gisworx.helloworld;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.osm.OpenStreetMapLayer;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;


public class MainActivity extends ActionBarActivity implements OnStatusChangedListener, OnSingleTapListener, LocationListener {

    MapView mMapView;
    boolean gotLocation = false;
    GraphicsLayer graphicsLayer;

    OpenStreetMapLayer openStreetMapLayer;
    ArcGISTiledMapServiceLayer satelliteMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //binding map to xml
        mMapView = (MapView) findViewById(R.id.map);

        //initialize the open street map as the basemap
        openStreetMapLayer = new OpenStreetMapLayer();

        //initialize the basemap layer
        satelliteMap = new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer");

        mMapView.addLayer(openStreetMapLayer);

        //add listener to check the status of map
        mMapView.setOnStatusChangedListener(this);

        //add a listener to check device on-tap
        mMapView.setOnSingleTapListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //remove the old layer
        mMapView.removeLayer(0);

        if (id == R.id.street) {

            //add the street layer again
            mMapView.addLayer(openStreetMapLayer,0);
            return true;

        } else if (id == R.id.base) {

            //addd the satelite layer to the basemap
            mMapView.addLayer(satelliteMap,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusChanged(Object object, STATUS status) {
        if (object == mMapView) {
            if (status == STATUS.INITIALIZED) {

                //
                Toast.makeText(this, "map loaded", Toast.LENGTH_SHORT).show();

                //start the location manager
                mMapView.getLocationDisplayManager().setLocationListener(this);
                mMapView.getLocationDisplayManager().start();

                //show the venue point
                showVenuePoint();

            } else if (status == STATUS.INITIALIZATION_FAILED) {

            }
        }
    }

    @Override
    public void onSingleTap(float x, float y) {

        Toast.makeText(this, "Hello map", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {

        if (!gotLocation) {
            Toast.makeText(this, "Got the current location...", Toast.LENGTH_SHORT).show();
            mMapView.zoomToScale(mMapView.getLocationDisplayManager().getPoint(), mMapView.getMaxScale());
            gotLocation = true;
        }

    }

    private void showVenuePoint() {

        //Make a graphic object using a symbol and point
        Point point = GeometryEngine.project(55.34531, 25.24805, mMapView.getSpatialReference());
        PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(this, getResources().getDrawable(R.drawable.ic_venue));
        Graphic graphic = new Graphic(point, pictureMarkerSymbol);

        //initialize a graphic layer
        GraphicsLayer graphicsLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.DYNAMIC);
        mMapView.addLayer(graphicsLayer);

        graphicsLayer.addGraphic(graphic);


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
