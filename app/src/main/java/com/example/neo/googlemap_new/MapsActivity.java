package com.example.neo.googlemap_new;

        import android.Manifest;
        import android.content.Context;
        import android.graphics.Color;
        import android.location.Address;
        import android.location.Geocoder;
        import android.provider.DocumentsContract;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;

        import com.akexorcist.googledirection.DirectionCallback;
        import com.akexorcist.googledirection.GoogleDirection;
        import com.akexorcist.googledirection.constant.RequestResult;
        import com.akexorcist.googledirection.constant.TransportMode;
        import com.akexorcist.googledirection.model.Direction;
        import com.akexorcist.googledirection.model.Leg;
        import com.akexorcist.googledirection.model.Route;
        import com.akexorcist.googledirection.model.Step;
        import com.akexorcist.googledirection.util.DirectionConverter;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.GoogleMapOptions;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import com.google.android.gms.maps.model.Polyline;
        import com.google.android.gms.maps.model.PolylineOptions;
        import com.google.maps.android.SphericalUtil;
        import android.content.DialogInterface;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.text.InputType;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.ArrayAdapter;
        import android.widget.AutoCompleteTextView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.FrameLayout;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.PendingResult;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.location.LocationServices;
        import com.google.android.gms.location.places.PlaceLikelihood;
        import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
        import com.google.android.gms.location.places.Places;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.jsoup.Jsoup;
        import org.jsoup.nodes.Document;

        import java.io.BufferedReader;
        import java.io.FileInputStream;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.sql.SQLData;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Locale;

        import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
        import static java.lang.Math.log;
        import static java.lang.Math.random;
        import static java.lang.Math.round;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;


    private final LocationDbHelper db = new LocationDbHelper(this);
    private boolean isLogin = false;


    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;


    private LatLng myLocation;
    private Location mLastKnownLocation;
    private Polyline polylineFinal;
    private Leg leg;
    private Route route;
    private boolean directionFlag = false;
    private List<Marker> allParkSpots;
    private Circle targetPlace;
    private String bookName;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private final int mMaxEntries = 5;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
//        List<MYLocation>allspots = praseData();
//        for(MYLocation myLocation:allspots)
//            db.addLoaction(myLocation);

        GoogleMapOptions googleMapOptions = new GoogleMapOptions().liteMode(true).minZoomPreference(20);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        myLocation = new LatLng(22.286689, 114.160758);
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        setEditText();
        setMenuButton();

        List<MYLocation>ParkSpots= createParkSpot();

        removeAllMarker(allParkSpots);
        allParkSpots=addMarker(myLocation,ParkSpots);



        mMap.setMinZoomPreference(15);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("selflocation")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selflocation));



        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void setEditText(){
        final AutoCompleteTextView editText = (AutoCompleteTextView)findViewById(R.id.text_input);
        editText.setText(new String("   Please input your destination"));
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(20);
        editText.setBackgroundColor(Color.WHITE);
        List<MYLocation> locationArrayList = db.getAllLocations();
        ArrayList<String> stringList= new ArrayList<String>();
        for(MYLocation location: locationArrayList)
            stringList.add(location.getName());
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringList);
        editText.setThreshold(1);
        editText.setAdapter(adapter);
        editText.setSelectAllOnFocus(false);
        editText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                editText.setText("");

            }
        });
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if((actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    String inputName = editText.getText().toString();
                    MYLocation result = db.getLocation(inputName);

                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    if (result == null) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
//                        builder.setTitle("Oh no!")
//                                .setMessage("We can not find that place").show();
                        try{
                            if (targetPlace!=null)
                                targetPlace.remove();
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocationName(inputName, 5);
                            if (addresses.size() > 0) {
                                Double lat = (double) (addresses.get(0).getLatitude());
                                Double lon = (double) (addresses.get(0).getLongitude());
                                LatLng targetPosition = new LatLng(lat,lon);
                                targetPlace = mMap.addCircle(new CircleOptions().center(targetPosition).fillColor(Color.BLUE).strokeColor(Color.WHITE)
                                        .radius(50).strokeWidth(10));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(targetPosition));
                                removeAllMarker(allParkSpots);
                                try {
                                    List<MYLocation> allPositions = createParkSpot();
                                    allParkSpots=addMarker(targetPosition, allPositions);
                                }catch (Exception e){
                                    Log.e("error","fail to get all positions");
                                }
                            }
                        }
                        catch (Exception e){
                            Log.e("error","fail to get new position through name");
                        }

                    }
                    else{
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(result.getLatitude()),Double.parseDouble(result.getLongitude()))).title(result.getName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        mMap.setMinZoomPreference(18);
                        sendDirectionRequest(marker);
                        onMapClick(marker.getPosition());
                    }
                    return true; // consume.
                }
                return false;
            }

        });
    }
    public void setMenuButton() {

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Button navigation = (Button) findViewById(R.id.go);
                navigation.setVisibility(View.INVISIBLE);
                RelativeLayout detailboard = (RelativeLayout) findViewById(R.id.detailboard);
                detailboard.setVisibility(View.INVISIBLE);

                final RelativeLayout registrationBoard = (RelativeLayout)findViewById(R.id.registration);
                registrationBoard.setBackgroundColor(Color.WHITE);
                final Button register = (Button) findViewById(R.id.register);
                register.setBackgroundColor(Color.rgb(51, 122, 224));
                final Button cancel = (Button) findViewById(R.id.cancel);
                cancel.setBackgroundColor(Color.rgb(179, 177, 179));



                final RelativeLayout loginBoard = (RelativeLayout) findViewById(R.id.loginBoard);
                loginBoard.setBackgroundColor(Color.WHITE);
                Button login = (Button) findViewById(R.id.loginButton);
                login.setBackgroundColor(Color.rgb(51, 122, 224));
                Button register_Login = (Button) findViewById(R.id.register_login);
                register_Login.setBackgroundColor(Color.rgb(179, 177, 179));


                final RelativeLayout infoBoard = (RelativeLayout) findViewById(R.id.infoBoard);
                infoBoard.setBackgroundColor(Color.WHITE);
                Button logoutButton = (Button)findViewById(R.id.logoutButton);
                logoutButton.setBackgroundColor(Color.rgb(51, 122, 224));

                final Button bookCancelButton = (Button)findViewById(R.id.cancelBookButton);




                if(isLogin == false){
                    loginBoard.setVisibility(View.VISIBLE);

                    loginBoard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginBoard.setVisibility(View.INVISIBLE);
                            infoBoard.setVisibility(View.INVISIBLE);
                            registrationBoard.setVisibility(View.INVISIBLE);
                        }
                    });
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText inputName_Login = (EditText)findViewById(R.id.inputname_login);
                            EditText inputPassword_Login = (EditText)findViewById(R.id.inputpassword_login);
                            final String userName = inputName_Login.getText().toString();
                            final String password = inputPassword_Login.getText().toString();
                            if(userName.equals("")||password.equals("")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                builder.setTitle("Fail!")
                                        .setMessage("Please fill in username and password").show();
                            }
                            else{
                                /*add database verify*/
                                boolean verified = db.verify(userName,password);
                                if (verified == true) {
                                    isLogin = true;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                    builder.setTitle("Successful!")
                                            .setMessage("You are now login").show();

                                    loginBoard.setVisibility(View.INVISIBLE);
                                    infoBoard.setVisibility(View.INVISIBLE);
                                    registrationBoard.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                    builder.setTitle("Fail!")
                                            .setMessage("Wrong Password").show();

                                }
                            }

                        }
                    });
                    register_Login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginBoard.setVisibility(View.INVISIBLE);
                            registrationBoard.setVisibility(View.VISIBLE);
                            infoBoard.setVisibility(View.INVISIBLE);


                            register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final EditText inputName = (EditText)findViewById(R.id.inputname);
                                    final EditText inputPassword = (EditText)findViewById(R.id.inputpassword);
                                    final EditText inputCarPlate = (EditText)findViewById(R.id.inputcarplate);
                                    final EditText inputphone = (EditText)findViewById(R.id.inputphonenum);
                                    final String userName = inputName.getText().toString();
                                    final String password = inputPassword.getText().toString();
                                    final String phone = inputphone.getText().toString();
                                    final String carPlate = inputCarPlate.getText().toString();
                                    if(userName.equals("")||password.equals("")||phone.equals("")||carPlate.equals("")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Fail!")
                                                .setMessage("Please complete all information").show();
                                    }
                                    else{
                                         /*database update*/
                                        db.addAccount(new Account(userName,password,carPlate,phone));

                                        registrationBoard.setVisibility(View.INVISIBLE);
                                        loginBoard.setVisibility(View.VISIBLE);
                                        infoBoard.setVisibility(View.INVISIBLE);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                        builder.setTitle("Successful!")
                                                .setMessage("Now Please Login").show();

                                    }

                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registrationBoard.setVisibility(View.INVISIBLE);
                                    loginBoard.setVisibility(View.VISIBLE);
                                    infoBoard.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    });

                }
                else{
                    EditText inputName = (EditText) findViewById(R.id.inputname_login);
                    String userName = inputName.getText().toString();
                    Account loginAccount = db.getAccount(userName);

                    TextView username_info = (TextView)findViewById(R.id.username_info_1);
                    TextView phone_info = (TextView)findViewById(R.id.phonenum_info_1);
                    TextView carPlate_info = (TextView)findViewById(R.id.carplate_info_1);


                    username_info.setText(loginAccount.getUserName());
                    phone_info.setText(loginAccount.getPhoneNum());
                    carPlate_info.setText(loginAccount.getCarPlate());

                    registrationBoard.setVisibility(View.INVISIBLE);
                    loginBoard.setVisibility(View.INVISIBLE);
                    infoBoard.setVisibility(View.VISIBLE);

                    infoBoard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginBoard.setVisibility(View.INVISIBLE);
                            infoBoard.setVisibility(View.INVISIBLE);
                            registrationBoard.setVisibility(View.INVISIBLE);
                        }
                    });

                    logoutButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginBoard.setVisibility(View.INVISIBLE);
                            infoBoard.setVisibility(View.INVISIBLE);
                            registrationBoard.setVisibility(View.INVISIBLE);
                            isLogin = false;
                        }
                    });
                    bookCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(bookName != null){
                                try{
                                    MYLocation bookedLocation = db.getLocation(bookName);
                                    bookedLocation.setQuota(bookedLocation.getQuota()+1);
                                    db.updateLocation(bookedLocation);
                                    bookName = null;
                                }
                                catch (Exception e)
                                {
                                    Log.e("error","failed to update location after canceling book");
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                builder.setTitle("Successful!")
                                        .setMessage("Thanks for your canceling").show();
                            }
                        }
                    });




                }

            }
        });
    }
    public List<MYLocation> createParkSpot(){

        List<MYLocation>allparkspot = db.getAllLocations();
        return allparkspot;
    }
    public List<Marker>addMarker(LatLng target,List<MYLocation>allparkspot){

        List<Marker> allMarkers = new ArrayList<Marker>();

        for(int i =0;i<allparkspot.size();i++){
            double lat = Double.parseDouble(allparkspot.get(i).getLatitude());
            double lon = Double.parseDouble(allparkspot.get(i).getLongitude());
            LatLng position = new LatLng(lat,lon);
            System.out.println(SphericalUtil.computeDistanceBetween(target,position));
            double distance = SphericalUtil.computeDistanceBetween(target,position);
            if (SphericalUtil.computeDistanceBetween(target,position)<1500)
            {

                String name = allparkspot.get(i).getName();
                String address = allparkspot.get(i).getAddress();
                allMarkers.add(mMap.addMarker(new MarkerOptions().position(position).title(name).snippet(address)));
            }

        }
        return allMarkers;

    }
    public void removeAllMarker(List<Marker>allMarkers){
        if(allMarkers!=null) {
            for (Marker marker : allMarkers)
                marker.remove();
        }
    }

    @Override
    public void onMapClick(final LatLng latLng){
        Button navigation = (Button) findViewById(R.id.go);
        navigation.setVisibility(View.INVISIBLE);
        RelativeLayout detailboard = (RelativeLayout) findViewById(R.id.detailboard);
        detailboard.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onMarkerClick(final Marker marker){
//        directionFlag = false;
        if(polylineFinal !=null)
            polylineFinal.remove();
        TextView durationTimeView = (TextView) findViewById(R.id.estimatedtime);
        durationTimeView.setText("");


        if (marker.getTitle().equals("selflocation")==false) {
            marker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 400, null);

//            RelativeLayout board = (RelativeLayout) findViewById(R.id.board);


            sendDirectionRequest(marker);

        }

        return true;

    }

    public void sendDirectionRequest(final Marker marker){
        GoogleDirection.withServerKey("AIzaSyDp2QnsXv1cB3kS2FRErxhkQVz2d-UVdxk")
                .from(myLocation)
                .to(marker.getPosition())
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        String status = direction.getStatus();
                        if(status.equals(RequestResult.OK)){

                            route = direction.getRouteList().get(0);
                            leg = route.getLegList().get(0);
                            RelativeLayout detailboard = (RelativeLayout) findViewById(R.id.detailboard);
                            detailboard.setVisibility(View.INVISIBLE);
                            detailboard.setBackgroundColor(Color.WHITE);



                            final Button navigation = (Button) findViewById(R.id.go);
                            navigation.setVisibility(View.INVISIBLE);

                            navigation.setText("Book !");
                            navigation.setTextSize(20);
                            navigation.setTextColor(Color.WHITE);
                            navigation.setBackgroundColor(Color.rgb(70, 136, 241));
                            navigation.setVisibility(View.VISIBLE);
                            navigation.setOnClickListener(new View.OnClickListener() {


                                boolean polylineReady = true;
                                public void onClick(View v) {

                                    if (navigation.getText().toString() == "Go !") {

                                        final Button navigation = (Button) findViewById(R.id.go);

                                        navigation.setVisibility(View.INVISIBLE);

                                        ArrayList<LatLng> directionPoint = leg.getDirectionPoint();
                                        final PolylineOptions polylineOptions = new PolylineOptions()
                                                .addAll(directionPoint)
                                                .color(Color.RED)
                                                .width(15);
                                        mMap.setMaxZoomPreference(17);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation), 400, null);
                                        polylineFinal = mMap.addPolyline(polylineOptions);

                                        final RelativeLayout detailboard = (RelativeLayout) findViewById(R.id.detailboard);
                                        detailboard.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {


                                                if (polylineReady == true) {
                                                    RelativeLayout directionBoard = (RelativeLayout) findViewById(R.id.directioninfoboard);
                                                    directionBoard.setVisibility(View.VISIBLE);
                                                    directionBoard.setBackgroundColor(Color.WHITE);
                                                    detailboard.setVisibility(View.INVISIBLE);
                                                    navigation.setVisibility(View.INVISIBLE);


                                                    String directionInfoTxt = "";
                                                    List<Step> stepList = leg.getStepList();
                                                    for (int i = 0; i < stepList.size(); i++) {
                                                        Step step = stepList.get(i);
                                                        Document doc = Jsoup.parse(step.getHtmlInstruction());
//                                      mMap.addMarker(new MarkerOptions().position(step.getStartLocation().getCoordination()))
//                                      mMap.addMarker(new MarkerOptions().position(step.getEndLocation().getCoordination()));
                                                        directionInfoTxt += step.getDistance().getText() + "   " + step.getDuration().getText() + "  " + step.getManeuver() + "  "
                                                                + doc.text() + "\n";
                                                    }


                                                    TextView directionInfo = (TextView) findViewById(R.id.directioninfo);

                                                    directionInfo.setText(directionInfoTxt);
                                                    directionBoard.setOnClickListener(new View.OnClickListener() {
                                                        public void onClick(View v) {
                                    /*set text*/            v.setOnClickListener(null);
                                                            polylineReady = true;
                                                            v.setVisibility(View.INVISIBLE);
                                                            detailboard.setVisibility(View.VISIBLE);
                                                        }
                                                    });

                                                    polylineReady = false;
                                                } else
                                                    v.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    }
                                    else if(navigation.getText().toString()=="Book !") {
                                        if (isLogin == false) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                            builder.setTitle("Fail!")
                                                    .setMessage("Please Login First").show();

                                        } else {
                                            if(bookName != null){
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                                builder.setTitle("Fail!")
                                                        .setMessage("Please cancel your book first").show();
                                                return;

                                            }
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            //Yes button clicked
                                                            navigation.setText("Go !");
                                                            bookName = marker.getTitle();

                                                            String durationTime = leg.getDuration().getText();
                                                            TextView durationTimeView = (TextView) findViewById(R.id.estimatedtime);
                                                            durationTimeView.setText(durationTime);
                                                            durationTimeView.setTextSize(20);
                                                            durationTimeView.setVisibility(View.VISIBLE);

                                                            TextView tv = (TextView) findViewById(R.id.detatilinfo);

                                                            MYLocation markerLocation = db.getLocation(marker.getTitle());
                                                            String tvContent = "Destination: " + markerLocation.getName() +" park spot"+ "\n" +
                                                                    "Distance: " + leg.getDistance().getText() + "\n" + "Quota: " + String.valueOf(markerLocation.getQuota());
                                                            tv.setText("");
                                                            tv.setText(tvContent);

                                                            try{
                                                                String name = marker.getTitle();
                                                                MYLocation bookedLocation = db.getLocation(name);
                                                                bookedLocation.setQuota(bookedLocation.getQuota()-1);
                                                                db.updateLocation(bookedLocation);
                                                            }
                                                            catch (Exception e)
                                                            {
                                                                Log.e("error","failed to update location after booking");
                                                            }

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                                            builder.setTitle("Successful!")
                                                                    .setMessage("Please cancel order when you leave").show();
                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            break;
                                                    }
                                                }
                                            };
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                                    .setNegativeButton("No", dialogClickListener);
                                            final AlertDialog dialog = builder.create();
                                            dialog.show();
                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                                        }
                                    }
                                }
                            });


                            TextView tv = (TextView) findViewById(R.id.detatilinfo);
                            String feeINfo = "$30/h";

                            MYLocation markerLocation = db.getLocation(marker.getTitle());
                            String tvContent = "Name: " + markerLocation.getName() +" park spot"+ "\n" +
                                    "Address: " + markerLocation.getAddress() + "\n" + "Open Time:" + markerLocation.getOpentime()
                                    + "     " +"Fee:  "+feeINfo+"    "+"Quota: " + String.valueOf(markerLocation.getQuota());
                            tv.setText("");
                            tv.setText(tvContent);


                            detailboard.setVisibility(View.VISIBLE);

                            directionFlag = false;
                        }else if(status.equals(RequestResult.NOT_FOUND)){
                            System.out.println("not find the way");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                        System.out.println("failed");
                    }
                });


    }
    public List<MYLocation> praseData(){
        List<MYLocation> allParkSpots = new ArrayList<MYLocation>();
        try {
// open the file for reading

            InputStream instream = getResources().openRawResource(R.raw.shops);
// if file the available for reading
            if (instream != null) {
                // prepare the file for reading
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                // read every line of the file into the line-variable, on line at the time
                do {
                    line = buffreader.readLine();
                    String [] stringArray = line.split("\t");
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(stringArray[0], 5);
                    if (addresses.size() > 0) {
                        String lat = String.valueOf(addresses.get(0).getLatitude());
                        String lon = String.valueOf(addresses.get(0).getLongitude());
                        String name = stringArray[0];
                        String address = stringArray[1];
                        int quota = 150;
                        String opentime = "0110:12:00";
                        allParkSpots.add(new MYLocation(name,address,opentime,quota,lat,lon));
                    }

                } while (line != null);
                instream.close();
            }
        } catch (Exception ex) {
            // print stack trace.
            Log.e("error","fail to find file");
        }
        return allParkSpots;
    }


}

