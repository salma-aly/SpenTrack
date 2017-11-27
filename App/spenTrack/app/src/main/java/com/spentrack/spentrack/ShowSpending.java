package com.spentrack.spentrack;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.loopj.android.http.*;

import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


import static com.loopj.android.http.AsyncHttpClient.log;


@TargetApi(Build.VERSION_CODES.N)
public class ShowSpending extends AppCompatActivity {

    private Button getSpendingbtn;
    private EditText shopNameText;

    Calendar myCalendar = Calendar.getInstance();

    private EditText dateFromText;
    private EditText dateToText;

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

        shopNameText=(EditText)findViewById(R.id.shop_edit_text);

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

    private void GetSpending() {


        String urlString = "http://35.196.180.79:8080/spentrack"; // URL to call
        JSONObject jsonParams = new JSONObject();
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
        log.d("in get spending","will create http client");
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            RequestParams params = new RequestParams();
            String query= "";
            jsonParams.put("date_from",dateFromText.getText());
            jsonParams.put("date_to",dateToText.getText());
            jsonParams.put("shop_name",shopNameText.getText());
            StringEntity entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            log.d("SHOP NAME IS:", getParams());

            //client.get("http://35.196.214.140:8080/spentrack",params, new AsyncHttpResponseHandler() {
            client.get(this,"http://35.196.214.140:8080/spentrack",entity,"application/json", new AsyncHttpResponseHandler() {
                //client.get(urlString,params, new AsyncHttpResponseHandler() {
                @Override

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.w("here", "success");
                    String content = new String(responseBody);
                    Log.w("here", content);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("here fail", error);

                }
            });
        }
        catch (Exception e) {

            System.out.println(e.getMessage());

        }

        //update ui
    }

    private String getParams(){
        String shopName=shopNameText.getText().toString();
        return shopName;
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
}
