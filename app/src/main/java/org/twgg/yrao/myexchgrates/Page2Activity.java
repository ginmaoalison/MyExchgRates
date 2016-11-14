package org.twgg.yrao.myexchgrates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Page2Activity extends AppCompatActivity {
    private List<Map<String, Object>> groupData;
    private List<List<Map<String, Object>>> childData;
    private ExpandableListView elst;
    private String[] arr_group = new String[]{ "常用貨幣", "其他貨幣"};
    private int[] imgs;
    private String[] currency;
    private String[] cname ;
    private String combo,combo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        elst = (ExpandableListView) findViewById(R.id.elst);

        combo = " [{Code: TWD, Name: 台幣}, {Code: JPY, Name: 日圓}, {Code: USD, Name: 美元}, {Code: CNY, Name: 人民幣}"+
                ", {Code: AUD, Name: 澳大利亞元}, {Code: GBP, Name: 英鎊}, {Code: EUR, Name: 歐元}, {Code: HKD, Name: 港幣}"+
                ", {Code: MOP, Name: 澳門幣}, {Code: KRW, Name: 韓圜}, {Code: THB, Name: 泰銖}, {Code: CAD, Name: 加拿大元}]";

        combo1 = " [{Code: XAU, Name: 金．金衡盎司}, {Code: XAG, Name: 銀．金衡盎司}, {Code: XPT, Name: 鉑．金衡盎司}, {Code: BTC, Name: 比特幣}]";

        prepareData();
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this,
                                                    groupData, R.layout.layout_group,
                                                    new String[]{"Group"}, new int[]{R.id.txt_group},
                                                    childData, R.layout.layout_item2,
                                                    new String[]{"Name","Code"}, new int[]{R.id.sel_cname, R.id.sel_currency});
        elst.setGroupIndicator(null);
        elst.setAdapter(adapter);
        for(int i=0;i<arr_group.length;i++){
            // open up all group
            elst.expandGroup(i);
        }

        // 關於ListView裡點選其中的項目
        elst.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                ExpandableListAdapter exAdapter = parent.getExpandableListAdapter();

                Map<String, String> childMap = (HashMap<String, String>) exAdapter.getChild(groupPosition, childPosition);
                String code = childMap.get("Code");
                String name = childMap.get("Name");
                Log.v("test","["+groupPosition+","+childPosition+"]"+": "+code+name);
                Intent it = new Intent(Page2Activity.this, MainActivity.class);
                it.putExtra("currency", code);
                it.putExtra("dname", name);
                //startActivity(it);
                setResult(RESULT_OK,it);
                finish();
                return false;
            }
        });
    }

    private void prepareData() {
        groupData = new ArrayList<Map<String, Object>>();
        childData = new ArrayList<List<Map<String, Object>>>();

        for (int i = 0; i < arr_group.length; i++) {
            Map<String, Object> groupObj = new HashMap<String, Object>();
            groupObj.put("Group", arr_group[i]);
            groupData.add(groupObj);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            switch (i) {
                case 0:
                    try {
                        JSONArray root = new JSONArray(combo);
                        int len = root.length();
                        currency = new String[len];
                        cname = new String[len];
                        imgs = new int[len];
                        for (int x=0;x<len;x++){
                            Map<String, Object> obj = new HashMap<String, Object>();
                            JSONObject item = root.getJSONObject(x);
                            currency[x] = item.getString("Code");
                            cname[x] = item.getString("Name");//轉換抓取drawable對應名稱的id
                            obj.put("Code", currency[x]);
                            obj.put("Name", cname[x]);
                            list.add(obj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    childData.add(list);
                    break;
                case 1:
                    try {
                        JSONArray root = new JSONArray(combo1);
                        int len = root.length();
                        currency = new String[len];
                        cname = new String[len];
                        imgs = new int[len];
                        for (int x=0;x<len;x++){
                            Map<String, Object> obj = new HashMap<String, Object>();
                            JSONObject item = root.getJSONObject(x);
                            currency[x] = item.getString("Code");
                            cname[x] = item.getString("Name");//轉換抓取drawable對應名稱的id
                            obj.put("Code", currency[x]);
                            obj.put("Name", cname[x]);
                            list.add(obj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    childData.add(list);
                    break;
            }
        }
    }
}
/*
public class Page2Activity extends AppCompatActivity {
    private ListView list2;
    private LinkedList<HashMap<String,Object>> data,data1;
    private String[] from = {"currency","img","cname"};
    private int[] to = {R.id.sel_currency,R.id.sel_img,R.id.sel_cname};
    private int[] imgs;
    private String[] currency;
    private String[] cname ;
    private SimpleAdapter adapter;
    private String combo,combo1;
    private int len=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);

        list2 = (ListView) findViewById(R.id.listView2);
        combo = " [{Code: TWD, Name: 台幣}, {Code: JPY, Name: 日圓}, {Code: USD, Name: 美元}, {Code: CNY, Name: 人民幣}"+
                 ", {Code: AUD, Name: 澳大利亞元}, {Code: GBP, Name: 英鎊}, {Code: EUR, Name: 歐元}, {Code: HKD, Name: 港幣}"+
                 ", {Code: MOP, Name: 澳門幣}, {Code: KRW, Name: 韓圜}, {Code: THB, Name: 泰銖}, {Code: CAD, Name: 加拿大元}]";

        combo1 = " [{Code: XAU, Name: 金}, {Code: XAG, Name: 銀}, {Code: XPT, Name: 鉑}, {Code: BTC, Name: 比特幣}]";

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
}*/
