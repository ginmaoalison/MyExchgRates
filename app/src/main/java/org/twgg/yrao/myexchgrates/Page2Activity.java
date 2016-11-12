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
import java.util.Map;

public class Page2Activity extends AppCompatActivity {
    private ListView list2;
    private LinkedList<HashMap<String,Object>> data;
    private String[] from = {"currency","img","cname"};
    private int[] to = {R.id.sel_currency,R.id.sel_img,R.id.sel_cname};
    private int[] imgs;
    private String[] currency;
    private String[] cname ;
    private SimpleAdapter adapter;
    private String combo;
    private int len=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        list2 = (ListView) findViewById(R.id.listView2);
        combo = " [{Code: TWD, Name: 台幣}, {Code: JPY, Name: 日圓}, {Code: USD, Name: 美元}, {Code: CNY, Name: 人民幣}"+
                 ", {Code: AUD, Name: 澳大利亞元}, {Code: GBP, Name: 英鎊}, {Code: EUR, Name: 歐元}, {Code: HKD, Name: 港幣}]";

        initListView2();

    }

    private void initListView2() {
        try {
            JSONArray root = new JSONArray(combo);
            len = root.length();
            currency = new String[len];
            cname = new String[len];
            imgs = new int[len];
            for (int i=0;i<len;i++){
                JSONObject item = root.getJSONObject(i);
                currency[i] = item.getString("Code");
                cname[i] = item.getString("Name");//轉換抓取drawable對應名稱的id
                imgs[i] = getResources().getIdentifier(currency[i].toLowerCase(), "drawable", getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        data = new LinkedList<>();
        for (int x=0; x<len; x++){
            HashMap<String,Object> map = new HashMap<>();
            map.put(from[0], currency[x]);
            map.put(from[1], imgs[x]);
            map.put(from[2], cname[x]);
            data.add(map);
        }

        adapter = new SimpleAdapter(this, data, R.layout.layout_item2, from, to);
        list2.setAdapter(adapter);

        // 關於ListView裡點選其中的項目
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("test", "list2: p1="+position);
                Intent it = new Intent(Page2Activity.this, MainActivity.class);
                it.putExtra("currency",(String) data.get(position).get(from[0]));
                it.putExtra("dname",(String) data.get(position).get(from[2]));
                //startActivity(it);
                setResult(RESULT_OK,it);
                finish();
            }
        });
    }


}
