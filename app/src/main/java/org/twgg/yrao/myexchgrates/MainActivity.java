package org.twgg.yrao.myexchgrates;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    private LinkedList<HashMap<String,Object>> data;
    private String[] from = {"currency","img","default"};
    private int[] to = {R.id.item_currency,R.id.img,R.id.inputDollar};
    private int[] imgs = {R.drawable.twd, R.drawable.jpy, R.drawable.usd, R.drawable.cny};
    private String[] country = {"TWD", "JPY", "USD", "CNY"};
    private Double[] money = new Double[4];
    private SimpleAdapter adapter;
    private EditText input;
    private DecimalFormat df=new DecimalFormat("#.##");

    //計算機button
    private GridView mGridButtons = null;
    private BaseAdapter mAdapter = null;
    private final String[] mTextBtns = new String[]{
            "1","2","3","+",
            "4","5","6","-",
            "7","8","9","*",
            ".","0","←","/",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView);

        mGridButtons = (GridView) findViewById(R.id.grid_buttons);
        mAdapter = new CalculatorAdapter(this, mTextBtns);
        mGridButtons.setAdapter(mAdapter);
        // 新建一個自訂AdapterView.OnItemClickListener的物件，用於設置GridView每個選項按鈕點擊事件
        OnButtonItemClickListener listener = new OnButtonItemClickListener();
        mGridButtons.setOnItemClickListener(listener);


        initRates();    //預設貨幣匯率
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initListView();
    }

    public void initListView() {
        data = new LinkedList<>();
        Log.v("test", money[0]+";"+money[1]+";"+money[2]+";"+money[3]);
        HashMap<String,Object> data0 = new HashMap<>();
        data0.put(from[0], country[0]);
        data0.put(from[1], imgs[0]);
        data0.put(from[2], df.format(money[0]));
        data.add(data0);

        HashMap<String,Object> data1 = new HashMap<>();
        data1.put(from[0], country[1]);
        data1.put(from[1], imgs[1]);
        data1.put(from[2], df.format(money[1]));
        data.add(data1);

        HashMap<String,Object> data2 = new HashMap<>();
        data2.put(from[0], country[2]);
        data2.put(from[1], imgs[2]);
        data2.put(from[2], df.format(money[2]));
        data.add(data2);

        HashMap<String,Object> data3 = new HashMap<>();
        data3.put(from[0], country[3]);
        data3.put(from[1], imgs[3]);
        data3.put(from[2], df.format(money[3]));
        data.add(data3);

        adapter = new SimpleAdapter(this, data, R.layout.layout_item, from, to);
        list.setAdapter(adapter);

        // 關於ListView裡點選其中的項目
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("test", "p1="+position);
            }
        });
        // 長按
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "p2="+position);
                gotoPage2(position);
                return false;
            }
        });
    }

    private void initRates(){
        new Thread(){
            @Override
            public void run() {
                doRates();
            }
        }.start();
    }
    private void doRates(){
        try {
            URL url = new URL("https://tw.rter.info/capi.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String strJSON = reader.readLine();
            //Log.v("test", "strLength:"+strJSON.length()+":"+strJSON);
            parseJSON(strJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void parseJSON(String json){
        try {
            JSONObject root = new JSONObject(json);
            JSONObject st = root.getJSONObject("USD"+country[0]);
            Double st_dollar = 100 / Double.valueOf(st.getString("Exrate"));

            money[0] = 100D;
            for (int i = 1; i<4; i++){
                JSONObject ex = root.getJSONObject("USD"+country[i]);
                Double ex_dollar = Double.valueOf(ex.getString("Exrate"));
                money[i] = st_dollar * ex_dollar;
                //Log.v("test", country[i]+":"+ex_dollar);
                Log.v("test", country[i]+":"+money[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gotoPage2(int p){
        Intent it = new Intent(this, Page2Activity.class);
        //it.putExtra("currency",(String) data.get(p).get(from[0]));
        //it.putExtra("img",(String) data.get(p).get(from[1]));
        //startActivity(it);
        //使用startActivityForResult等待回傳
        startActivityForResult(it, p);
    }


    @Override
    protected void onActivityResult(int p, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            //取得回傳過來的值
            String currency = intent.getStringExtra("currency");

            //轉換抓取drawable對應名稱的id
            int id = getResources().getIdentifier(currency.toLowerCase(), "drawable", getPackageName());

            HashMap<String,Object> data_chg = new HashMap<>();
            country[p] = currency;
            imgs[p] = id;
            data.remove(p);
            initRates();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.v("test", p+": "+country[p]+"; "+imgs[p]+"; "+df.format(money[p]));
            data_chg.put(from[0], country[p]);
            data_chg.put(from[1], imgs[p]);
            data_chg.put(from[2], df.format(money[p]));
            //data.add(data_chg);
            data.add(p, data_chg);

            adapter = new SimpleAdapter(this, data, R.layout.layout_item, from, to);
            list.setAdapter(adapter);
        } else if (resultCode == RESULT_CANCELED) {
            //當還沒完成動作時，使用者倒退回上一步會判定為cancel
        }
    }

    //
    private class OnButtonItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = (String) parent.getAdapter().getItem(position);
            Log.v("test", position+": "+text);

        }
    }
}
