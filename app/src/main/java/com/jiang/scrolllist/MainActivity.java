package com.jiang.scrolllist;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiang.scrolllist.view.ScrollListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private ScrollListView mListView;
    private String[] colorArray=new String[]{"#abcdef","#ff0000","#ffffff","#123456","#987654","#abcdef",
            "#ff0000","#ffffff","#123456","#987654","#abcdef","#ff0000","#ffffff","#123456","#987654"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ScrollListView) findViewById(R.id.listView);
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return colorArray.length;
            }

            @Override
            public Object getItem(int position) {
                return colorArray[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null){
                    convertView=new TextView(getApplication());
                    AbsListView.LayoutParams params=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp2px(120));
                    convertView.setLayoutParams(params);
                }
                convertView.setBackgroundColor(Color.parseColor(colorArray[position]));
                return convertView;
            }
        });

    }

    private int dp2px(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
