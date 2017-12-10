package com.spentrack.spentrack;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class ListViewTestActivity extends ListActivity {

    public ListView mListView;
    ArrayList<String[]> parsedList;
    String content;
    private TextView totalv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_test);

        totalv=(TextView)findViewById(R.id.totalview);

        Bundle extras = getIntent().getExtras();
        if(extras ==null){
            log.w("extra is null", "extra is null");
        }
        if(extras !=null) {
            content = extras.getString("KEY");
        }
        mListView = (ListView) findViewById(android.R.id.list);
// 1
        final ArrayList<String> myArrayList = new ArrayList<>();

        String [] resultsList=splitRetrievedRecords(content);
        log.w("retreived records lenght",resultsList.length+"");
        //parsedList=parseRecords(resultsList);
        parsedList=parseRecords2(content);
        int spendingTotal=computeTotalSpending(parsedList);
       totalv.setText("Total is:" +spendingTotal+"$");

        //get response
        //split response into a list of records
        //for each record parse it into a json object and get key values
        //add in the list view shop name and total fields
        //create a new 2d arraylist that contains website , maps link, phone number
        //when user clicks on show details show the rest
        //when user clicks again remove

        for(int i=0; i<parsedList.size();i++){
            myArrayList.add("Total: "+parsedList.get(i)[0]+"$ Date: "+parsedList.get(i)[1]+"  Click to show details");
        }
//        myArrayList.add("Shop Name: Salma's chocolate shop  Spending: 90$ Click to show details");
//        myArrayList.add("Salma");
//        myArrayList.add("If you click on one of these, then we can display the info!");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");
//        myArrayList.add("Salma");



// 2
        String[] listItems = new String[myArrayList.size()];
// 3
        for (int i = 0; i < myArrayList.size(); i++) {
            String array_element = myArrayList.get(i);
            listItems[i] = array_element;
        }
// 4
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);


    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {     // Do something when a list item is clicked
        TextView mTextView = (TextView) v;

        System.out.println(mTextView.getText().toString());
        String less_detailItem = mTextView.getText().toString();
        if (less_detailItem.contains("Click to show details")){
            //replace 'Click to show details' by the extra info to display
            //String more_detailItem = less_detailItem.replaceAll("Click to show details", "Address: 603 St-Catherine");
            String details=getDetailsString(position);
            String more_detailItem = less_detailItem.replaceAll("Click to show details", details);
            mTextView.setText(more_detailItem);
         }
        else if (!less_detailItem.contains("Click to show details")){
            //put back 'Click to show details' and remove the extra info to display
            //regex will match from 'address' to the end of the string
            less_detailItem = less_detailItem.replaceAll("Shop Name.*$", "Click to show details");

            mTextView.setText(less_detailItem);
        }
    }

    public String [] splitRetrievedRecords(String content){
//        content=content.replace("}","*");
//        content=content.replace("{","#");
        String [] listOfRecords=content.split("\\} , \\{");
        return listOfRecords;
    }

    public ArrayList<String[]> parseRecords (String []records){

        ArrayList <String[]> parsedRecords= new ArrayList<String[]>();
        JSONObject content_inJSON= null;

        for(int i=0; i< records.length;i++){
            String[] currRecord = new String[9];
            try {
                content_inJSON = new JSONObject(records[i]);
                currRecord[0] =content_inJSON.getString( "Total");
                currRecord[1] =content_inJSON.getString( "Date");
                currRecord[2] =content_inJSON.getString( "Shop Name");
                currRecord[3] =content_inJSON.getString( "Address");
                currRecord[4]=content_inJSON.getString( "Category");
                currRecord[5] =content_inJSON.getString( "Telephone number");
                currRecord[6] =content_inJSON.getString( "Website");
                currRecord[7] =content_inJSON.getString( "See place on google maps");
                currRecord[8]=content_inJSON.getString( "Rating");
                String extractedCategory=content_inJSON.getString( "Category");

                //map categories names given from the server to a user friendly syntax to be displayed on the android app
                String catgegory;
                switch (extractedCategory) {
                    case "restaurant":  catgegory = "Restaurant";
                        break;
                    case "cafe":  catgegory = "Cafe";
                        break;
                    case "clothing_store":  catgegory = "Clothing Store";
                        break;
                    case "furniture_store":  catgegory = "Furniture Store";
                        break;
                    case "hair_care":  catgegory= "Hair Care";
                        break;
                    case "grocery_or_supermarket":  catgegory = "Grocery";
                        break;
                    case "electronics_store":  catgegory = "Electronics Store";
                        break;
                    case "museum":  catgegory = "Museum";
                        break;
                    case "pharmacy":  catgegory = "Pharmacy";
                        break;
                    case "store":  catgegory = "Store";
                        break;
                    default: catgegory = "Other";
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            parsedRecords.add(currRecord);
        }

        return parsedRecords;
    }

    public ArrayList<String[]> parseRecords2 (String content ){

        ArrayList <String[]> parsedRecords= new ArrayList<String[]>();

        try {
            JSONArray myTestJSONArray = new JSONArray(content);
            log.w("lengthxxxxxxxxxxxxxxxxxxxxx",myTestJSONArray.length()+"");
            JSONObject myObject = myTestJSONArray.getJSONObject(0);
            String address = myObject.getString("Address");
            log.w("adreess!!!!!: ", address);


            for(int i=0; i< myTestJSONArray.length();i++){
                String[] currRecord = new String[9];
                try {
                    //content_inJSON = new JSONObject(records[i]);
                    currRecord[0] =myTestJSONArray.getJSONObject(i).getString( "Total");
                    currRecord[1] =myTestJSONArray.getJSONObject(i).getString( "Date");
                    currRecord[2] =myTestJSONArray.getJSONObject(i).getString( "Shop Name");
                    currRecord[3] =myTestJSONArray.getJSONObject(i).getString( "Address");
                    currRecord[4]=myTestJSONArray.getJSONObject(i).getString( "Category");
                    currRecord[5] =myTestJSONArray.getJSONObject(i).getString( "Telephone number");
                    currRecord[6] =myTestJSONArray.getJSONObject(i).getString( "Website");
                    currRecord[7] =myTestJSONArray.getJSONObject(i).getString( "See place on google maps");
                    currRecord[8]=myTestJSONArray.getJSONObject(i).getString( "Rating");
                    String extractedCategory=myTestJSONArray.getJSONObject(i).getString( "Category");

                    //map categories names given from the server to a user friendly syntax to be displayed on the android app
                    String catgegory;
                    switch (extractedCategory) {
                        case "restaurant":  catgegory = "Restaurant";
                            break;
                        case "cafe":  catgegory = "Cafe";
                            break;
                        case "clothing_store":  catgegory = "Clothing Store";
                            break;
                        case "furniture_store":  catgegory = "Furniture Store";
                            break;
                        case "hair_care":  catgegory= "Hair Care";
                            break;
                        case "grocery_or_supermarket":  catgegory = "Grocery";
                            break;
                        case "electronics_store":  catgegory = "Electronics Store";
                            break;
                        case "museum":  catgegory = "Museum";
                            break;
                        case "pharmacy":  catgegory = "Pharmacy";
                            break;
                        case "store":  catgegory = "Store";
                            break;
                        default: catgegory = "Other";
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                parsedRecords.add(currRecord);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }




        return parsedRecords;
    }



    public int computeTotalSpending(ArrayList<String[]> retreivedRecords){
        log.w("String retreived records",retreivedRecords.toString());
        int total =0;
        for(int i=0; i < retreivedRecords.size();i++){
            total+= Double.parseDouble(retreivedRecords.get(i)[0]);
        }
        return  total;
    }

    public String getDetailsString(int position){
        String details="";

//        currRecord[1] =myTestJSONArray.getJSONObject(i).getString( "Date");
//        currRecord[2] =myTestJSONArray.getJSONObject(i).getString( "Shop Name");
//        currRecord[3] =myTestJSONArray.getJSONObject(i).getString( "Address");
//        currRecord[4]=myTestJSONArray.getJSONObject(i).getString( "Category");
//        currRecord[5] =myTestJSONArray.getJSONObject(i).getString( "Telephone number");
//        currRecord[6] =myTestJSONArray.getJSONObject(i).getString( "Website");
//        currRecord[7] =myTestJSONArray.getJSONObject(i).getString( "See place on google maps");
//        currRecord[8]=myTestJSONArray.getJSONObject(i).getString( "Rating");
//        String extractedCategory=myTestJSONArray.getJSONObject(i).getString( "Category");


        if(parsedList!=null){
            details="\nShop Name: "+parsedList.get(position)[2]+"\nAddress: "+parsedList.get(position)[3]+"\nTelephone number: "+
                    parsedList.get(position)[5]+"\nRating: "+parsedList.get(position)[8];
        }

        return details;

    }

}


