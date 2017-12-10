package com.spentrack.spentrack;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


import static com.loopj.android.http.AsyncHttpClient.log;
import static com.spentrack.spentrack.LoginActivity.USER_ID;


@TargetApi(Build.VERSION_CODES.N)
public class ShowSpending extends AppCompatActivity {

    private Button getSpendingbtn;
    //private EditText shopNameText;
    //private EditText categoryText;
    Calendar myCalendar = Calendar.getInstance();

    private EditText dateFromText;
    private EditText dateToText;
    private TextView responseText;
    private Spinner spinner1;
    private Spinner spinner2;

    private CheckBox shopNameCheck;
    private CheckBox categoryCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_spending);

        getSpendingbtn  = (Button) findViewById(R.id.get_spending_btn);
        getSpendingbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                GetSpending();
            }
        });

        //shopNameText=(EditText)findViewById(R.id.shop_edit_text);
        //categoryText=(EditText)findViewById(R.id.category_edit_text);

        shopNameCheck=(CheckBox)findViewById(R.id.shopname_check);
        shopNameCheck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                onshopNameChecked();
            }
        });

        categoryCheck=(CheckBox)findViewById(R.id.category_check);
        categoryCheck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                onCategoryChecked();
            }
        });

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2=(Spinner)findViewById(R.id.spinner2);
        addListenerOnSpinnerItemSelection();
        //addItemsOnSpinner2();
        //add on click listner for spinner 2 if it works
        responseText=(TextView)findViewById(R.id.data_text_view);

        dateFromText=(EditText)findViewById(R.id.date_from_edit_text);
        dateFromText.setKeyListener(null);
        dateToText=(EditText)findViewById(R.id.date_to_edit_text);
        dateToText.setKeyListener(null);
        //TODO: add checks for start and end date
        dateFromText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final Calendar mcurrentDate=Calendar.getInstance();
               int mYear=mcurrentDate.get(Calendar.YEAR);
               int mMonth=mcurrentDate.get(Calendar.MONTH);
               int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(ShowSpending.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH,selectedday);
                        updateLabel(true);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();

            }
        });


        dateToText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                final Calendar mcurrentDate=Calendar.getInstance();
               int mYear=mcurrentDate.get(Calendar.YEAR);
               int mMonth=mcurrentDate.get(Calendar.MONTH);
               int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(ShowSpending.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH,selectedday);
                        updateLabel(false);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();

            }
        });

    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private void onshopNameChecked(){
        spinner2.setVisibility(View.VISIBLE);
        spinner2.setEnabled(true);
        spinner1.setVisibility(View.INVISIBLE);
        spinner1.setEnabled(false);
        categoryCheck.setChecked(false);
    }

    private void onCategoryChecked(){
        spinner2.setVisibility(View.INVISIBLE);
        spinner2.setEnabled(false);
        spinner1.setVisibility(View.VISIBLE);
        spinner1.setEnabled(true);
        shopNameCheck.setChecked(false);
    }

//    public void addItemsOnSpinner2() {
//
//    }


    private void GetSpending() {

        JSONObject jsonParams = set_request_params();
        log.w("json params are : ", jsonParams.toString());
        boolean successed=false;
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        try {
//            RequestParams params = new RequestParams();
//            params.put("token",idToken);
//            jsonParams.put("notes", idToken);
//            StringEntity entity = new StringEntity(jsonParams.toString());
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            client.get(this,"http://35.196.214.140:8080/spentrack",entity,"application/json", new AsyncHttpResponseHandler() {

        //send get request to the server
        log.w("in get spending","will create http client");
        AsyncHttpClient client = new AsyncHttpClient();
        try {

            RequestParams params = new RequestParams();
            //params.put("token",idToken);

//            //add user id parameter
//            //add request type paramter
//            //log.w("GET ID", ""+get_ID());
//            jsonParams.put("id",1);
//
//           // jsonParams.put("request_type","spending_query");
//            jsonParams.put("date_from",dateFromText.getText());
//            jsonParams.put("date_to",dateToText.getText());
//            //jsonParams.put("shop_name",shopNameText.getText());
//            //jsonParams.put("category",categoryText.getText());
//            log.w("JSONPARAMS",jsonParams.toString());

            StringEntity entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //log.d("
            // SHOP NAME IS:", getParams());

            //client.get("http://35.196.214.140:8080/spentrack",params, new AsyncHttpResponseHandler() {
            client.get(this,"http://104.196.62.234:8080/spentrack",entity,"application/json", new AsyncHttpResponseHandler() {
                //client.get(urlString,params, new AsyncHttpResponseHandler() {
                @Override

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.w("here", "success");
                    final String content = new String(responseBody);
                    Log.w("here", content);
                    //send contetn to list view activity
                    displayResponse(content);
                    Log.w("printing content",content+content.length());
                    try {
                        JSONArray myTestJSONArray = new JSONArray(content);
                        log.w("lengthxxxxxxxxxxxxxxxxxxxxx",myTestJSONArray.length()+"");
                        JSONObject myObject = myTestJSONArray.getJSONObject(0);
                        String address = myObject.getString("Address");
                        log.w("adreess!!!!!: ", address);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    if(content.length()>3){
                        ShowSpending.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("d",
                                        "Starting new activity!");
                                Intent intent = new Intent(getApplicationContext(),
                                        ListViewTestActivity.class);
                                intent.putExtra("KEY",content);
//                                Bundle extras = intent.getExtras();
//                                if(extras !=null) {
//                                    String content2 = extras.getString("KEY");
//                                    log.w("CONTENT IS", content2);
//                                }
                                ShowSpending.this.startActivity(intent);
                            }
                        });
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("here fail", error);
                    displayResponse("oops! an error has occured");
                }


            });
        }
        catch (Exception e) {

            System.out.println(e.getMessage());

        }



        //update ui
    }



    private void updateLabel(boolean from) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if(from){
            dateFromText.setText(sdf.format(myCalendar.getTime()));
        }else{
            dateToText.setText(sdf.format(myCalendar.getTime()));
        }

        log.d("calendar paramters",sdf.format(myCalendar.getTime()));

    }

    private void displayResponse(String response){
        responseText.setText(response);
    }

//    private void getDate(EditText edittext){
//        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
//            }
//
//        };
//
//        edittext.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                new DatePickerDialog(ShowSpending.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

    private int get_ID(){
        log.w("in the get id","lol");
        SharedPreferences prefs = getSharedPreferences(USER_ID, MODE_PRIVATE);
        String id_s = prefs.getString("id", "BLALLLLABLAA");
        int id = Integer.parseInt(id_s);
        log.w("in the get id",id+"");
            return id;
    }

    private JSONObject set_request_params(){
        JSONObject jsonParams = new JSONObject();
        RequestParams params = new RequestParams();
        try {
            //get ID
            jsonParams.put("id",1);
            jsonParams.put("request_type","spending_query");
            jsonParams.put("date_from",dateFromText.getText());
            jsonParams.put("date_to",dateToText.getText());
            if(shopNameCheck.isChecked()){
                jsonParams.put("Shop Name",spinner2.getSelectedItem().toString());
            }else if (categoryCheck.isChecked()){
                if(spinner1.getSelectedItem()!=null)
                jsonParams.put("Category",MapCategory(spinner1.getSelectedItem().toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonParams;
    }

    private String MapCategory(String s ){

        String catgegory="";
        switch (s) {
            case "Restaurant":  catgegory = "restaurant";
                break;
            case "Cafe":  catgegory = "cafe";
                break;
            case "Clothing Store":  catgegory = "clothing_store";
                break;
            case "Furniture Store":  catgegory = "furniture_store";
                break;
            case "Hair Care":  catgegory= "hair_care";
                break;
            case "Grocery":  catgegory = "grocery_or_supermarket";
                break;
            case "ElectronicsStore":  catgegory = "electronics_store";
                break;
            case "Museum":  catgegory = "museum";
                break;
            case "Pharmacy":  catgegory = "pharmacy";
                break;
            case "Store":  catgegory = "store";
                break;
            default: catgegory = "Other";
                break;
        }
         return catgegory;
    }

}
