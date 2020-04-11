package com.bhadrik.attendance_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context currentContext;
        TextView tv_home_factory_clockin,tv_home_site_clockin,tv_home_site_clockout;

        TextView tv_home_factory_clockin_text,tv_home_site_clockin_text,tv_home_site_clockout_text;

        SharedPreferences shpUserDetails;

        int whichclockin;
        String SiteId;

    TextView tvalertmessage,rlalertcancel;
            TextView tvalertbutton;

            TextView tvtoday,tvtitle;
    ImageView img_picture;
    EditText edmobile;

    Dialog alertDialog;

    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String TAG="GetCurrentLocation";
    private double currentlat=-101,currentlong=-101;
    String Address="";

    Geocoder geocoder;
    List<Address> addresses;

    private Uri image_ProfileUri;

            String site_id              ="";
            String employee_id          ="";
            String factoryclockin_id    ="";
            String factory_clockin_date ="";
            String site_clockin_time    ="";
            String site_clockout_time   ="";
            String factory_clockin_time ="";
            String todaydate            ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getCurrentLocation();

        currentContext=HomeScreen.this;

        shpUserDetails=getSharedPreferences("EmployeeDetails",0);

        edmobile        =findViewById(R.id.edmobile);
        tv_home_factory_clockin        =findViewById(R.id.tv_home_factory_clockin);
        tv_home_site_clockout          =findViewById(R.id.tv_home_site_clockout);
        tv_home_site_clockin           =findViewById(R.id.tv_home_site_clockin);

        tv_home_factory_clockin_text   =findViewById(R.id.tv_home_factory_clockin_text);
        tv_home_site_clockin_text      =findViewById(R.id.tv_home_site_clockin_text);
        tv_home_site_clockout_text     =findViewById(R.id.tv_home_site_clockout_text);

        tvtoday  =findViewById(R.id.tvdate      );
        tvtitle  =findViewById(R.id.tvlogout    );

        tv_home_factory_clockin         .setOnClickListener(this);
        tv_home_site_clockout           .setOnClickListener(this);
        tv_home_site_clockin            .setOnClickListener(this);

        tvtoday  .setOnClickListener(this);
        tvtitle  .setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Do next code
        }

        Map<String, String> getParams = new HashMap<>();
        getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));

        getClockinDetails(currentContext, GlobalLink.GETCLOCKINDETAILS_LINK, getParams);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);


        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted,");
                } else {
                    Log.e("value", "Permission Denied");
                }
                break;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "ACCESS_FINE_LOCATION permission allows us to get Current Location. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "ACCESS_COARSE_LOCATION permission allows us to get Current Location. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {


            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "CALL_PHONE permission allows us to Call Manager . Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "READ_EXTERNAL_STORAGE permission allows us to do store images. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {


            Snackbar snackbar = Snackbar
                    .make(tv_home_factory_clockin, "CAMERA permission allows us to Take Photo From Camera for Attendance. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
        }
    }

    public void FactoryClockin(Context context, String Url, final Map<String,String> getparams)
    {
        final ProgressDialog progressDialog= new ProgressDialog(context,R.style.NewDialog);
        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    System.out.println("Response : "+response);

                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                        alertDialog.dismiss();

                        JSONObject jsonObjectresult=jsonObject.getJSONObject("result");
                        tv_home_factory_clockin.setEnabled(false);

                        SiteId=jsonObjectresult.getString("site_id");

                        tv_home_factory_clockin.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        tv_home_factory_clockin_text.setText("You are clocked in at "+(jsonObjectresult.getString("factory_clockin_time")));
                        tv_home_factory_clockin_text.setVisibility(View.VISIBLE);

                        factory_clockin_time=(jsonObjectresult.getString("factory_clockin_time"));
                    }

                } catch (Exception e) {

                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {





                System.out.println("Response1 : "+error.toString()+error.getStackTrace());

                progressDialog.dismiss();
                return;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =getparams;


//                try {
//                    params.put("email_id","prakashvmistry064@gmail.com");
//                    params.put("phone_number","7698914697");
//                } catch (Exception e) {
//                    //Log.i(TAG,"Map error: Unable to compile post");
//                }
                return params;
            }


        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    public void SiteClockin(Context context, String Url, final Map<String,String> getparams)
    {

        final ProgressDialog progressDialog= new ProgressDialog(context,R.style.NewDialog);
        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    System.out.println("Response : "+response);

                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                        JSONArray jsonarr=jsonObject.getJSONArray("result");

                        for (int i=0;i<jsonarr.length();i++)
                        {
                            JSONObject jsonObject1=jsonarr.getJSONObject(i);
                            site_clockin_time=jsonObject1.getString("site_clockin_time");
                        }

                        alertDialog.dismiss();

                        tv_home_site_clockin.setEnabled(false);
                        tv_home_site_clockin.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        tv_home_site_clockin_text.setText("You are clocked in at "+site_clockin_time);
                        tv_home_site_clockin_text.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {

                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {





                System.out.println("Response1 : "+error.toString()+error.getStackTrace());

                progressDialog.dismiss();
                return;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =getparams;


//                try {
//                    params.put("email_id","prakashvmistry064@gmail.com");
//                    params.put("phone_number","7698914697");
//                } catch (Exception e) {
//                    //Log.i(TAG,"Map error: Unable to compile post");
//                }
                return params;
            }


        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    public void SiteClockout(Context context, String Url, final Map<String,String> getparams)
    {

        final ProgressDialog progressDialog= new ProgressDialog(context,R.style.NewDialog);
        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    System.out.println("Response : "+response);

                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {

                            site_clockin_time=jsonObject.getString("clock_out_time");

                        alertDialog.dismiss();

                        tv_home_site_clockout.setEnabled(false);
                        tv_home_site_clockout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        tv_home_site_clockout_text.setText("You are clocked in at "+site_clockin_time);
                        tv_home_site_clockout_text.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {

                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {





                System.out.println("Response1 : "+error.toString()+error.getStackTrace());

                progressDialog.dismiss();
                return;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =getparams;


//                try {
//                    params.put("email_id","prakashvmistry064@gmail.com");
//                    params.put("phone_number","7698914697");
//                } catch (Exception e) {
//                    //Log.i(TAG,"Map error: Unable to compile post");
//                }
                return params;
            }


        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    public void getClockinDetails(Context context, String Url, final Map<String,String> getparams)
    {

        final ProgressDialog progressDialog= new ProgressDialog(context,R.style.NewDialog);
          progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {




                try {
                    System.out.println("Response : "+response);

                    JSONObject jsonObject=new JSONObject(response);

                   if(jsonObject.getString("status").equalsIgnoreCase("2")) {

                       todaydate=jsonObject.getString("todaydate");

                       DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                       DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy EEEE");
                       String inputDateStr=todaydate;
                       Date date = inputFormat.parse(inputDateStr);
                       String outputDateStr = outputFormat.format(date);

                       tvtoday.setText(outputDateStr);
                       JSONArray jsonarr=jsonObject.getJSONArray("result");

                       System.out.println("Data:"+jsonarr);

                        for (int i=0;i<jsonarr.length();i++)
                        {
                            JSONObject jsonObject1=jsonarr.getJSONObject(i);

                            site_id=jsonObject1.getString("site_id");


                            employee_id               =jsonObject1.getString("employee_id");
                            factoryclockin_id         =jsonObject1.getString("factoryclockin_id");
                            factory_clockin_date      =jsonObject1.getString("factory_clockin_date");
                            factory_clockin_time        =jsonObject1.getString("factory_clockin_time");
                            site_clockin_time=jsonObject1.getString("site_clockin_time");
                            site_clockout_time=jsonObject1.getString("site_clockout_time");

                        }

                        if(factory_clockin_time.contains(":"))
                        {
                            tv_home_factory_clockin.setEnabled(false);
                            tv_home_factory_clockin.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            tv_home_factory_clockin_text.setText("You are clocked in at "+factory_clockin_time);
                            tv_home_factory_clockin_text.setVisibility(View.VISIBLE);
                        }
                        if(site_clockin_time.trim().contains(":"))
                        {
                            tv_home_site_clockin.setEnabled(false);
                            tv_home_site_clockin.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            tv_home_site_clockin_text.setText("You are clocked in at "+site_clockin_time);
                            tv_home_site_clockin_text.setVisibility(View.VISIBLE);
                        }

                        if(site_clockout_time.trim().contains(":"))
                        {
                            tv_home_site_clockout.setEnabled(false);
                            tv_home_site_clockout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            tv_home_site_clockout_text.setText("You are clocked out at "+site_clockout_time);
                            tv_home_site_clockout_text.setVisibility(View.VISIBLE);
                        }


                    }

                } catch (Exception e) {

                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {





                System.out.println("Response1 : "+error.toString()+error.getStackTrace());

                progressDialog.dismiss();
                return;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params =getparams;


//                try {
//                    params.put("email_id","prakashvmistry064@gmail.com");
//                    params.put("phone_number","7698914697");
//                } catch (Exception e) {
//                    //Log.i(TAG,"Map error: Unable to compile post");
//                }
                return params;
            }


        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);


    }

    private void selectImage( Context context) {
        final CharSequence[] options = { "Take Photo"};

         alertDialog= new Dialog(currentContext,R.style.NewDialog);
        alertDialog.setContentView(R.layout.custom_capture_dialog);
        alertDialog.getWindow().setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alertDialog.setCancelable(true);
        tvalertmessage=alertDialog.findViewById(R.id.tvalertmessage);
        tvalertbutton=alertDialog.findViewById(R.id.tv_alert_ok);
        rlalertcancel=alertDialog.findViewById(R.id.tv_alert_cancel);
         img_picture=alertDialog.findViewById(R.id.img_picture);

        tvalertmessage.setVisibility(View.VISIBLE);

        tvalertbutton.setText("Send");
        tvalertbutton.setVisibility(View.GONE);

        img_picture.setImageDrawable(null);
        img_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                image_ProfileUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, image_ProfileUri);
                startActivityForResult(intent, 1001);

            }
        });

        tvalertmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                image_ProfileUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, image_ProfileUri);
                startActivityForResult(intent, 1001);

            }
        });

        rlalertcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvalertbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentlat==-101 || currentlong==-101 )
                {
                    Toast.makeText(currentContext, "Didn't Find your Current Location.Please try again!", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Address=getAddressFromLatLong(currentlat,currentlong);

                    if(whichclockin==1)
                    {
                        Map<String, String> getParams = new HashMap<>();
                        getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));
                        getParams.put("latitude", String.valueOf(currentlat));
                        getParams.put("longitude", String.valueOf(currentlong));
                        getParams.put("factory_address", Address);
                        getParams.put("factory_clockin_photo", getEncoded64ImageStringFromBitmap(img_picture));

                        System.out.println("Params"+getParams);

                        FactoryClockin(currentContext, GlobalLink.FACTORYCLOCKIN_LINK, getParams);
                    }else  if(whichclockin==2)
                    {
                        System.out.println("SiteID:2"+site_id);
                        {
                            Map<String, String> getParams = new HashMap<>();
                            getParams.put("site_id", site_id);
                            getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));
                            getParams.put("latitude", String.valueOf(currentlat));
                            getParams.put("longitude", String.valueOf(currentlong));
                            getParams.put("site_address", Address);
                            getParams.put("site_clockin_photo", getEncoded64ImageStringFromBitmap(img_picture));

                            System.out.println("Params"+getParams);

                            SiteClockin(currentContext, GlobalLink.SITECLOCKIN_LINK, getParams);
                        }



                    }else  if(whichclockin==3)
                    {
                        System.out.println("SiteID:3"+site_id);
                            Map<String, String> getParams = new HashMap<>();
                            getParams.put("site_id", site_id);
                            getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));
                            getParams.put("latitude", String.valueOf(currentlat));
                            getParams.put("longitude", String.valueOf(currentlong));
                            getParams.put("site_address", Address);
                            getParams.put("site_clockout_photo", getEncoded64ImageStringFromBitmap(img_picture));

                            System.out.println("Params" + getParams);

                            SiteClockout(currentContext, GlobalLink.SITECLOCKOUT_LINK, getParams);

                    }
                }
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 1001:
                if (requestCode == 1001)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), image_ProfileUri);

                            Glide.with(currentContext).load(thumbnail).into(img_picture);
                            tvalertmessage.setVisibility(View.GONE);
                            tvalertbutton.setVisibility(View.VISIBLE);

                            String img = getRealPathFromURI(image_ProfileUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getEncoded64ImageStringFromBitmap(ImageView profile) {

        String imgString="";
        try {
            Bitmap bitmap=((BitmapDrawable)profile.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
            imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        }catch (Exception e)
        {
        }
        return imgString;
    }


    // itechhardikmistry@gmail
    // 972649

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)

                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit from this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor ed=shpUserDetails.edit();
                        ed.putBoolean("emp_"+shpUserDetails.getString("employee_id",""),false);
                        ed.commit();

                        Intent userredirectintent=new Intent(HomeScreen.this,LoginScreen.class);
                        startActivity(userredirectintent);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_home_factory_clockin:
                whichclockin=1;
                selectImage(currentContext);
                break;
            case R.id.tv_home_site_clockout:
                if(factory_clockin_time.contains(":") && site_clockin_time.contains(":"))
                {
                    Map<String, String> getParams = new HashMap<>();
                    getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));

                    getClockinDetails(currentContext, GlobalLink.GETCLOCKINDETAILS_LINK, getParams);

                    whichclockin=3;
                    selectImage(currentContext);
                }else
                {
                    new AlertDialog.Builder(this)
                            .setMessage("Please do Site Clockin First")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                            })
                            .show();
                }
                break;
            case R.id.tv_home_site_clockin:

                if(factory_clockin_time.contains(":"))
                {
                    Map<String, String> getParams = new HashMap<>();
                    getParams.put("employee_id", shpUserDetails.getString("employee_id", ""));

                    getClockinDetails(currentContext, GlobalLink.GETCLOCKINDETAILS_LINK, getParams);

                    whichclockin=2;
                    selectImage(currentContext);
                }else
                {
                    new AlertDialog.Builder(this)
                            .setMessage("Please do Factory Clockin First")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                            })
                            .show();
                }


                break;

            case R.id.tvdate:

                break;

            case R.id.tvlogout:
                onBackPressed();
                break;
        }

    }

    public void getCurrentLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            String mLongitudeText=String.valueOf(mLastLocation.getLongitude());



            Log.d(TAG, "getCurrentLocation: Lat "+mLatitudeText+" ,Long "+mLongitudeText);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            String mLongitudeText=String.valueOf(mLastLocation.getLongitude());

            //  Log.d(TAG, "getCurrentLocation: Lat "+mLatitudeText+" ,Long "+mLongitudeText);

            startLocationUpdates();
        }

    }

    public String getAddressFromLatLong(double latitude, double longitude)
    {
        String address="";
        try {
            geocoder = new Geocoder(this, Locale.getDefault());


            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();


            System.out.println("Address : "+address+"\n"+
                    "City : "+city+"\n"+
                    "State : "+state+"\n"+
                    "Country : "+country+"\n"+
                    "PostalCode : "+postalCode+"\n"+
                    "knownName : "+knownName+"\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: "+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: "+connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {


        String mLatitudeText = String.valueOf(location.getLatitude());
        String mLongitudeText=String.valueOf(location.getLongitude());

        currentlat  =location.getLatitude();
        currentlong =location.getLongitude();


        Log.d(TAG, "getCurrentLocation: Lat "+mLatitudeText+" ,Long "+mLongitudeText);

        getAddressFromLatLong(currentlat,currentlong);

        Location location1 = new Location(LocationManager.GPS_PROVIDER);

        double distance=location.distanceTo(location1);
        Log.d(TAG, "Distance : "+distance);

    }
}


