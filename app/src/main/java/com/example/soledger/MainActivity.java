package com.example.soledger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/*
按照实验的内容进行操作，掌握Android中常用控件的实现和设置属性等内容，熟悉Android中常见布局，并学习事件处理等内容。
完成实验内容后，设计一个UI界面，要求如下：
至少一个嵌套布局
至少有5种不同的控件，该5种控件至少涵括3个类别（文本框、按钮、列表、进度条、选择器、菜单、对话框），控件总数为10个以上
每个控件都有可操作的控件处理函数
 */

public class MainActivity extends AppCompatActivity {

    TextView date;
    EditText money;
    String checked;
    RadioGroup rgp;
    AutoCompleteTextView TxtIn;
    TextView TxtOut;
    TextView quote;
    int cnt = 0;

    static final String[] default_remarks = new String[] {"吃早餐", "发工资啦", "玄不救非，氪不改命",
            "医疗保险费"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cld = Calendar.getInstance();
        String now_date = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                + cld.get(Calendar.DAY_OF_MONTH);
        date = findViewById(R.id.txtDate);
        date.setText(now_date);

        money = findViewById(R.id.money);
        checked = getString(R.string.food);

        rgp = findViewById(R.id.rgp);
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbt01:
                        Toast.makeText(getApplicationContext(), R.string.food,
                                Toast.LENGTH_SHORT).show();
                        checked = getString(R.string.food);
                        break;
                    case R.id.rbt02:
                        Toast.makeText(getApplicationContext(), R.string.health,
                                Toast.LENGTH_SHORT).show();
                        checked = getString(R.string.health);
                        break;
                    case R.id.rbt03:
                        Toast.makeText(getApplicationContext(), R.string.play,
                                Toast.LENGTH_SHORT).show();
                        checked = getString(R.string.play);
                        break;
                    case R.id.rbt04:
                        Toast.makeText(getApplicationContext(), R.string.income,
                                Toast.LENGTH_SHORT).show();
                        checked = getString(R.string.income);
                        break;
                }
            }
        });

        TxtIn = findViewById(R.id.acp);
        ArrayAdapter<String> adp = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, default_remarks);
        TxtIn.setAdapter(adp);
        TxtOut = findViewById(R.id.tvOut);
        TxtIn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String str = TxtIn.getText().toString();
                String oup = getString(R.string.remarks) + str;
                TxtOut.setText(oup);
                return false;
            }
        });

        quote = findViewById(R.id.quotes);

        Button btn01 = findViewById(R.id.btn_calendar);
        btn01.setOnClickListener(new MyClickListener());
        Button btn02 = findViewById(R.id.btn_ok);
        btn02.setOnClickListener(new MyClickListener());
        Button btn03 = findViewById(R.id.btn_more);
        btn03.setOnClickListener(new MyClickListener());
        Button btn04 = findViewById(R.id.clear);
        btn04.setOnClickListener(new MyClickListener());
        Button btn05 = findViewById(R.id.btn_remain);
        btn05.setOnClickListener(new MyClickListener());
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_calendar:
                    Calendar ncd = Calendar.getInstance();
                    DatePickerDialog dtp = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String dt = year + "/" + (month + 1) + "/"
                                            + dayOfMonth;
                                    date.setText(dt);
                                }
                            }, ncd.get(Calendar.YEAR), ncd.get(Calendar.MONTH), ncd.get(Calendar.DAY_OF_MONTH));
                    dtp.show();
                    return ;
                case R.id.btn_ok:
                    String str = TxtIn.getText().toString();
                    String oup = getString(R.string.remarks) + str;
                    TxtOut.setText(oup);

                    String m = money.getText().toString();
                    if (m.isEmpty()) {
                        Toast.makeText(MainActivity.this, R.string.empty,
                                Toast.LENGTH_LONG).show();
                        return ;
                    }
                    double n = Double.parseDouble(m);
                    String msg = checked + ": " + n + "\n" + TxtOut.getText().toString();
                    AlertDialog.Builder myDlg = new AlertDialog.Builder(MainActivity.this);
                    myDlg.setTitle(R.string.alert);
                    myDlg.setMessage(msg);
                    myDlg.setNegativeButton(getString(R.string.cancel), null);
                    myDlg.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String dt = date.getText().toString();
                            String new_money = money.getText().toString();

                            SharedPreferences preferences = getSharedPreferences("detail",
                                    MODE_PRIVATE);
                            String old_data = preferences.getString(dt, "");
                            String new_data = old_data + "\n" + checked + " " + new_money + "  "
                                    + TxtOut.getText().toString();
                            SharedPreferences.Editor editor = getSharedPreferences("detail",
                                    MODE_PRIVATE).edit();
                            editor.putString(dt, new_data);
                            editor.apply();

                            SharedPreferences preferences2 = getSharedPreferences("all_comp",
                                    MODE_PRIVATE);
                            String old_all = preferences2.getString(dt, "0");
                            String last_money = preferences2.getString("remain", "0");
                            double new_all, all_money;
                            if (checked.equals("Income ☆") || checked.equals("收入 ☆")) {
                                new_all = Double.parseDouble(old_all) + Double.parseDouble(new_money);
                                all_money = Double.parseDouble(last_money) + Double.parseDouble(new_money);
                            }
                            else {
                                new_all = Double.parseDouble(old_all) - Double.parseDouble(new_money);
                                all_money = Double.parseDouble(last_money) - Double.parseDouble(new_money);
                            }
                            SharedPreferences.Editor editor2 = getSharedPreferences("all_comp",
                                    MODE_PRIVATE).edit();
                            editor2.putString(dt, Double.toString(new_all));
                            editor2.putString("remain", Double.toString(all_money));
                            editor2.apply();

                            money.setText("");
                            TxtIn.setText("");
                            TxtOut.setText(R.string.remarks);
                        }
                    });
                    myDlg.show();
                    return ;
                case R.id.btn_more:
                    Uri myu = Uri.parse("http://www.baidu.com");
                    Intent myi = new Intent(Intent.ACTION_VIEW, myu);
                    startActivity(myi);
                    return ;
                case R.id.clear:
                    AlertDialog.Builder myDlg2 = new AlertDialog.Builder(MainActivity.this);
                    myDlg2.setTitle(R.string.alert);
                    myDlg2.setMessage(getString(R.string.confirm));
                    myDlg2.setNegativeButton(getString(R.string.cancel), null);
                    myDlg2.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor tmp_editor = getSharedPreferences("detail",
                                    MODE_PRIVATE).edit();
                            tmp_editor.clear();
                            tmp_editor.apply();
                            SharedPreferences.Editor tmp_editor2 = getSharedPreferences("all_comp",
                                    MODE_PRIVATE).edit();
                            tmp_editor2.clear();
                            tmp_editor2.apply();
                            Toast.makeText(MainActivity.this, R.string.done,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    myDlg2.show();
                    switch (cnt) {
                        case 0:
                            quote.setText(R.string.quote2); cnt++; break;
                        case 1:
                            quote.setText(R.string.quote3); cnt++;  break;
                        case 2:
                            quote.setText(R.string.quote); cnt=0; break;
                    }
                    return ;
                case R.id.btn_remain:
                    SharedPreferences preferences3 = getSharedPreferences("all_comp",
                            MODE_PRIVATE);
                    String now_money = preferences3.getString("remain", "0");
                    AlertDialog.Builder myDlg3 = new AlertDialog.Builder(MainActivity.this);
                    myDlg3.setTitle(R.string.inquire);
                    String rmn = getString(R.string.remain) + ": " + now_money;
                    myDlg3.setMessage(rmn);
                    myDlg3.show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.addSubMenu(1, 0, 0, R.string.about);
        menu.addSubMenu(2, 1, 0, R.string.Statistics);
        SubMenu sbm03 = menu.addSubMenu(3, 2 ,0, R.string.faq);
        sbm03.add(3, 4, 1, R.string.faq_run);
        sbm03.add(3, 5, 0, R.string.faq_update);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder myDlg = new AlertDialog.Builder(MainActivity.this);
                myDlg.setTitle(R.string.about);
                myDlg.setMessage("\n  SoLedger     version 1.0");
                myDlg.setPositiveButton(getString(R.string.ok), null);
                myDlg.show();
                return true;
            case 1:
                AlertDialog.Builder myDlg2 = new AlertDialog.Builder(MainActivity.this);
                myDlg2.setTitle(R.string.Statistics);

                String dt = date.getText().toString();
                SharedPreferences preferences2 = getSharedPreferences("all_comp", MODE_PRIVATE);
                String all_data = preferences2.getString(dt, "0.0");

                SharedPreferences preferences = getSharedPreferences("detail", MODE_PRIVATE);
                String detail = preferences.getString(dt, "");

                String msg = dt + "\n" + getString(R.string.all) + "\n" + all_data + "\n" +
                        getString(R.string.detail) + detail;
                myDlg2.setMessage(msg);
                myDlg2.setPositiveButton("ok", null);
                myDlg2.show();
                return true;
            case 4:
                Toast.makeText(MainActivity.this, R.string.re_enter,
                        Toast.LENGTH_LONG).show();
                return true;
            case 5:
                Toast.makeText(MainActivity.this, R.string.bad_network,
                        Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}