package org.twgg.yrao.myexchgrates;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
    private String[] from = {"currency","img","default","dname"};
    private int[] to = {R.id.item_currency,R.id.img,R.id.inputDollar,R.id.dollarName};
    private int[] imgs = {R.drawable.twd, R.drawable.jpy, R.drawable.usd, R.drawable.cny};
    private String[] currency = {"TWD", "JPY", "USD", "CNY"};
    private String[] dname = {"台幣","日圓","美元","人民幣"};
    private Double[] money = new Double[4];
    private SimpleAdapter adapter;
    private EditText input;
    private DecimalFormat df=new DecimalFormat("#,###,###,###,###,##0.00");
    private int pos=0;
    private Double dollars=100D;

    private WebView webview;

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
        webview = (WebView) findViewById(R.id.webview);

        /*mGridButtons = (GridView) findViewById(R.id.grid_buttons);
        mAdapter = new CalculatorAdapter(this, mTextBtns);
        mGridButtons.setAdapter(mAdapter);
        // 新建一個自訂AdapterView.OnItemClickListener的物件，用於設置GridView每個選項按鈕點擊事件
        OnButtonItemClickListener listener = new OnButtonItemClickListener();
        mGridButtons.setOnItemClickListener(listener);*/


        initRates();    //預設貨幣匯率
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initListView();
    }
    private void setData(){
        data = new LinkedList<>();
        for (int x=0; x<4; x++){
            HashMap<String,Object> map = new HashMap<>();
            map.put(from[0], currency[x]);
            map.put(from[1], imgs[x]);
            map.put(from[2], df.format(money[x]));
            map.put(from[3], dname[x]);
            data.add(map);
        }

        adapter = new SimpleAdapter(this, data, R.layout.layout_item, from, to);
        list.setAdapter(adapter);
        getChart();
    }

    public void initListView() {
        Log.v("test", money[0]+";"+money[1]+";"+money[2]+";"+money[3]);

        /*data = new LinkedList<>();
        HashMap<String,Object> data0 = new HashMap<>();
        data0.put(from[0], currency[0]);
        data0.put(from[1], imgs[0]);
        data0.put(from[2], df.format(money[0]));
        data.add(data0);

        HashMap<String,Object> data1 = new HashMap<>();
        data1.put(from[0], currency[1]);
        data1.put(from[1], imgs[1]);
        data1.put(from[2], df.format(money[1]));
        data.add(data1);

        HashMap<String,Object> data2 = new HashMap<>();
        data2.put(from[0], currency[2]);
        data2.put(from[1], imgs[2]);
        data2.put(from[2], df.format(money[2]));
        data.add(data2);

        HashMap<String,Object> data3 = new HashMap<>();
        data3.put(from[0], currency[3]);
        data3.put(from[1], imgs[3]);
        data3.put(from[2], df.format(money[3]));
        data.add(data3);*/
        setData();

        // 關於ListView裡點選其中的項目
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView inputDollar = (TextView)view.findViewById(R.id.inputDollar);
                Log.v("test", "p1="+position+"; "+inputDollar.getText());
                pos = position;  //基準貨幣位置
                setAlertDialogEvent();
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
            JSONObject st = root.getJSONObject("USD"+currency[pos]);
            Double st_dollar = dollars / Double.valueOf(st.getString("Exrate"));

            money[pos] = dollars;
            for (int i = 0; i<4; i++){
                if (i != pos) {
                    JSONObject ex = root.getJSONObject("USD" + currency[i]);
                    Double ex_dollar = Double.valueOf(ex.getString("Exrate"));
                    money[i] = st_dollar * ex_dollar;
                    //Log.v("test", country[i]+":"+ex_dollar);
                    Log.v("test", currency[i] + ":" + money[i]);
                }
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
        pos = p;  //基準貨幣位置
        startActivityForResult(it, p);
    }


    @Override
    protected void onActivityResult(int p, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            //取得startActivityForResult回傳過來的值
            String re_currency = intent.getStringExtra("currency");
            String re_dname = intent.getStringExtra("dname");

            //轉換抓取drawable對應名稱的id
            int id = getResources().getIdentifier(re_currency.toLowerCase(), "drawable", getPackageName());

            HashMap<String,Object> data_chg = new HashMap<>();
            currency[p] = re_currency;
            imgs[p] = id;
            dname[p] = re_dname;
            //data.remove(p);
            initRates();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*Log.v("test", p+": "+currency[p]+"; "+imgs[p]+"; "+df.format(money[p]));
            data_chg.put(from[0], currency[p]);
            data_chg.put(from[1], imgs[p]);
            data_chg.put(from[2], df.format(money[p]));
            //data.add(data_chg);
            data.add(p, data_chg);*/
            setData();


        } else if (resultCode == RESULT_CANCELED) {
            //當還沒完成動作時，使用者倒退回上一步會判定為cancel
        }
    }

    private void setAlertDialogEvent(){
        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_alert, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("輸入數字")
                .setView(item)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == -1) {
                            EditText editText = (EditText) item.findViewById(R.id.editText);
                            if (editText.getText().toString().equals(null) || editText.getText().toString().equals("")) {
                                dollars = 100D;
                            } else {
                                dollars = Double.valueOf(editText.getText().toString());
                            }
                            Log.v("test", "dollars=" + dollars);

                            initRates();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setData();
                        }
                    }
                }).show();
    }

    private void getChart(){
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://chart.finance.yahoo.com/z?s=USD"+currency[pos]+"%3dX&t=1m&q=l&l=on&z=l&a=v&p=s&lang=zh-Hant-TW&region=TW");
        // 調整到適合WebView大小
        webview.getSettings().setUseWideViewPort(true);
        //webview.getSettings().setLoadWithOverviewMode(true);
        // 設置支持縮放
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        // 設置縮放比例
        webview.setInitialScale(95);
    }

}
