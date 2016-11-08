package org.twgg.yrao.myexchgrates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;

public class Page2Activity extends AppCompatActivity {
    private ListView list2;
    private LinkedList<HashMap<String,Object>> data;
    private String[] from = {"currency","img"};
    private int[] to = {R.id.sel_currency,R.id.sel_img};
    private int[] imgs = {R.drawable.twd, R.drawable.jpy, R.drawable.usd, R.drawable.cny};
    private String[] country = {"TWD", "JPY", "USD", "CNY"};
    private SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        list2 = (ListView) findViewById(R.id.listView2);

        initListView2();

    }

    private void initListView2() {
        data = new LinkedList<>();
        HashMap<String,Object> data0 = new HashMap<>();
        data0.put(from[0], country[0]);
        data0.put(from[1], imgs[0]);
        data.add(data0);

        HashMap<String,Object> data1 = new HashMap<>();
        data1.put(from[0], country[1]);
        data1.put(from[1], imgs[1]);
        data.add(data1);

        adapter = new SimpleAdapter(this, data, R.layout.layout_item2, from, to);
        list2.setAdapter(adapter);

        // 關於ListView裡點選其中的項目
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("test", "list2: p1="+position);
                Intent it = new Intent(Page2Activity.this, MainActivity.class);
                it.putExtra("currency",(String) data.get(position).get(from[0]));
                //startActivity(it);
                setResult(RESULT_OK,it);
                finish();
            }
        });
    }

    public void backMain(int p){
        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("currency",(String) data.get(p).get(from[0]));
        //startActivity(it);
        setResult(RESULT_OK,it);
        finish();
    }
}
