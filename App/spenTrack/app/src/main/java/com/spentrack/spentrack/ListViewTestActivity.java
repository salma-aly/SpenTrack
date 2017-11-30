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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class ListViewTestActivity extends ListActivity {

    public ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_test);


        mListView = (ListView) findViewById(android.R.id.list);
// 1
        final ArrayList<String> myArrayList = new ArrayList<>();
        myArrayList.add("Shop Name: Salma's chocolate shop  Spending: 90$ Click to show details");
        myArrayList.add("Salma");
        myArrayList.add("If you click on one of these, then we can display the info!");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");
        myArrayList.add("Salma");



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
            String more_detailItem = less_detailItem.replaceAll("Click to show details", "Address: 603 St-Catherine");
        mTextView.setText(more_detailItem);
         }
        else if (!less_detailItem.contains("show details")){
            //put back 'Click to show details' and remove the extra info to display
            //regex will match from 'address' to the end of the string
            less_detailItem = less_detailItem.replaceAll("Address.*", "Click to show details");

            mTextView.setText(less_detailItem);
        }
    }

}


