package org.twgg.yrao.myexchgrates;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalculatorAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mStrs = null;

    public CalculatorAdapter(Context context, String[] strs){
        this.mContext = context;
        this.mStrs = strs;
    }
    @Override
    //返回已定義資料來源總數量
    public int getCount() {
        return mStrs.length;
    }
    @Override
    //告訴適配器取得目前容器中的資料物件
    public Object getItem(int position) {
        return mStrs[position];
    }
    @Override
    //告訴適配器取得目前容器中的資料ID
    public long getItemId(int position) {
        return position;
    }
    @Override
    //取得當前欲顯示的按鈕View
    public View getView(int position, View convertView, ViewGroup parent) {
        // 利用View的inflate方法產生實體一個View出來
        View view = View.inflate(mContext, R.layout.item_button, null);
        // 通過view找到按鈕對應的控制項TextView
        TextView textView = (TextView) view.findViewById(R.id.txt_button);
        // 根據position獲取按鈕應該設置的內容，並設置
        String str = mStrs[position];
        textView.setText(str);
        // 此處主要是為了給Back按鈕單獨的按下效果。根據str的值來判斷
        if(str.equals("←")){
            textView.setTextColor(Color.WHITE);
        }
        return view;
    }
}


