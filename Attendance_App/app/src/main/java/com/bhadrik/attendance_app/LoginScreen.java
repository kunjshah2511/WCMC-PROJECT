package com.bhadrik.attendance_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    EditText ed_login_contactno,ed_login_password;
    Button btnlogin;
    private SharedPreferences shpUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        shpUserDetails=getSharedPreferences("EmployeeDetails",0);

        if(shpUserDetails.getBoolean("emp_"+shpUserDetails.getString("employee_id",""),false))
        {
            Intent userredirectintent=new Intent(LoginScreen.this,HomeScreen.class);
            startActivity(userredirectintent);
        }

        ed_login_contactno=findViewById(R.id.ed_login_contactno);
        ed_login_password=findViewById(R.id.ed_login_password);
        btnlogin=findViewById(R.id.btnlogin);

        btnlogin.setOnClickListener(this);

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
                    .make(ed_login_contactno, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar snackbar = Snackbar
                    .make(ed_login_contactno, "ACCESS_FINE_LOCATION permission allows us to get Current Location. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar snackbar = Snackbar
                    .make(ed_login_contactno, "ACCESS_COARSE_LOCATION permission allows us to get Current Location. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {


            Snackbar snackbar = Snackbar
                    .make(ed_login_contactno, "CALL_PHONE permission allows us to Call Manager . Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar snackbar = Snackbar
                    .make(ed_login_contactno, "READ_EXTERNAL_STORAGE permission allows us to do store images. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {


            Snackbar snackbar = Snackbar
                    .make(ed_login_contactno, "CAMERA permission allows us to Take Photo From Camera for Attendance. Please allow this permission in App Settings.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();

            snackbar.show();

        }
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onClick(View v) {


        if(v.getId()==R.id.btnlogin)
        {
            if(ed_login_contactno.getText().toString().trim().length()==0)
            {
                Toast.makeText(this, "Contact number should not empty!", Toast.LENGTH_SHORT).show();
            }else if(ed_login_password.getText().toString().trim().length()==0)
            {
                Toast.makeText(this, "Password should not empty!", Toast.LENGTH_SHORT).show();
            }else
            {
                Map<String,String> getParams=new HashMap<>();
                getParams.put("contact_no",ed_login_contactno.getText().toString());
                getParams.put("password",ed_login_password.getText().toString());
                System.out.println("Data"+getParams);
                VerifyLogin(LoginScreen.this,GlobalLink.LOGIN_LINK,getParams);
            }
        }
    }

    // Verify Credential and Login To App - API - GlobalLink.COMPANYCODE_LIST_LINK

    public void VerifyLogin(Context context, String Url, final Map<String,String> getparams)
    {

        final Dialog progressDialog= new Dialog(context,R.style.NewDialog);
//                 progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    System.out.println("Response : "+response);

                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equalsIgnoreCase("0")) {

                        new AlertDialog.Builder(LoginScreen.this)

                                .setTitle("Error")
                                .setMessage("invalid contact number or password!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                        }
                                })

                                .show();
                    }else
                    if(jsonObject.getString("status").equalsIgnoreCase("1")) {


                        Snackbar snackbar = Snackbar
                                .make(ed_login_contactno, "Login Sucessfully!", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        JSONArray jsonArray=jsonObject.getJSONArray("result");

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            SharedPreferences.Editor ed=shpUserDetails.edit();
                            ed.putBoolean("emp_"+jsonObject1.getString("employee_id"),true);
                            ed.putString("employee_id",jsonObject1.getString("employee_id"));
                            ed.putString("employee_name",jsonObject1.getString("employee_name"));
                            ed.putString("contact_no",jsonObject1.getString("contact_no"));
                            ed.commit();
                        }

                        Intent userredirectintent=new Intent(LoginScreen.this,HomeScreen.class);
                        startActivity(userredirectintent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

}
