package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.olc.scan.IScanCallBack;
import com.olc.scan.ScanManager;

import Printer.PrintHelper;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements MyCodeReceiver.Message, View.OnClickListener {
    private static final String TAG = "调试";
    ImageView imageView;
    PrintHelper mPrinter = new PrintHelper();
    ScanManager sm;
    Bitmap bitmap;
    Intent intent = new Intent();
    MyCodeReceiver myCodeReceiver;
    boolean isScan = false;
    boolean hasSetwbs, hasSetgood, hasSetsupply, selectsupply, selectgood, selectwbs ;
    EditText supply, wbs, good;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img123);
        Button btScan = findViewById(R.id.buttonScan);
        Button btGenerate = findViewById(R.id.buttonGenerate);
        Button btPrintQRcode = findViewById(R.id.buttonPrintQRcode);

        supply = findViewById(R.id.supply_input);
        wbs = findViewById(R.id.wbs_input);
        good = findViewById(R.id.good_input);

        supply.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    selectsupply = true;
                } else {
                    selectsupply = false;
                }
            }
        });
        wbs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    selectwbs = true;
                } else {
                    selectwbs = false;
                }
            }
        });

        good.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    selectgood = true;
                } else {
                    selectgood = false;
                }
            }
        });

        //扫描二维码
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanQRcode();
            }
        });
        //生成二维码
        btGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateQRcode();
            }
        });
        //打印二维码
        btPrintQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printQRcode();
            }
        });
        //隐藏软键盘
        findViewById(R.id.traceroute_rootview).setOnClickListener(this);
    }

    //通过实现MyReceiver.Message接口可以在这里对MyReceiver中的数据进行处理
    @Override
    public void getMsg(String str) {
        if (isScan & !"".equals(str)) {
            hasSetsupply = false;
            hasSetwbs = false;
            hasSetgood = false;
            if (selectsupply) {
                supply.setText(str);
                isScan = false;
                hasSetsupply = true;
            }
            if (selectwbs) {
                wbs.setText(str);
                isScan = false;
                hasSetwbs = true;
            }
            if (selectgood) {
                good.setText(str);
                isScan = false;
                hasSetgood = true;
            }
        }
    }

    @Override
    //注册广播接收器
    protected void onResume() {
        super.onResume();
        registerBroadcast();
    }

    @Override
    //注销广播接收器
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myCodeReceiver);
    }

    //注册广播
    public void registerBroadcast() {
        myCodeReceiver = new MyCodeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.barcode.sendBroadcast");
        registerReceiver(myCodeReceiver, intentFilter);
        myCodeReceiver.setMessage(this);
    }

    //扫描QRcode
    @SuppressLint("WrongConstant")
    public void ScanQRcode() {
        //打开扫描
        sm = (ScanManager) getSystemService("olc_service_scan");
        sm.setScanSwitchLeft(true);
        sm.setScanSwitchRight(true);
        sm.setScanOperatingMode(0);
        sm.setBarcodeReceiveModel(2);
        //发送广播
        intent.setAction("com.barcode.sendBroadcastScan");
        sendBroadcast(intent);
        isScan = true;

    }


    //生成二维码
    public void CreateQRcode() {
        String str = "";
        boolean inputTextCorrect = true;//判断编辑框输入内容是否正确标志位。true：输入正确；false：输入错误
        //组合字符串
        if (wbs.getText().toString().trim().equals("") & good.getText().toString().trim().equals("") & supply.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this, "内容不能全为空", Toast.LENGTH_SHORT).show();
            inputTextCorrect = false;
        } else {
            if (!wbs.getText().toString().trim().equals("")) {
                if (good.getText().toString().trim().equals("") & supply.getText().toString().trim().equals("")) {
                    str = wbs.getText().toString();
                } else {
                    str = wbs.getText().toString() + "&";
                }
            }
            if (!good.getText().toString().trim().equals("")) {
                if (supply.getText().toString().trim().equals("")) {
                    str = str + good.getText().toString();
                } else {
                    str = str + good.getText().toString() + "&";
                }
            }
            str = str + supply.getText().toString();
        }
        try {
            if (inputTextCorrect) {
                Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //设置编码方式为：UTF-8
                hints.put(EncodeHintType.MARGIN, "0"); //设置二维码外框边距为“0”，去除白框
                // hints.put(EncodeHintType.ERROR_CORRECTION, "H");//设置编码错误等级，加logo的时候需要设置
                //编码生成BitMaTrix
                BitMatrix bitMatrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, 200, 200, hints);
                //处理数据
                int fixbit[] = new int[bitMatrix.getWidth() * bitMatrix.getHeight()];
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    for (int x = 0; x < bitMatrix.getWidth(); x++) {
                        if (bitMatrix.get(x, y)) {
                            fixbit[x + y * bitMatrix.getWidth()] = Color.BLACK;
                        } else {
                            fixbit[x + y * bitMatrix.getWidth()] = Color.WHITE;
                        }
                    }
                }
                //生成二维码
                bitmap = Bitmap.createBitmap(fixbit, bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
                //二维码里面增加文字
                bitmap = addTextToBitmap(bitmap, wbs.getText().toString(), good.getText().toString(), supply.getText().toString());
                imageView.setImageBitmap(bitmap);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    //打印二维码
    public void printQRcode() {
        //调用打印机，打印二维码
        //设置打印区域，并打印二维码
        EditText printTime = findViewById(R.id.printTime);
        int i = 1;
        int result = mPrinter.Open(MainActivity.this);
        if (bitmap != null) {
            if (result == 0) {
                for (i = 1; i <= Integer.parseInt(printTime.getText().toString()); i++) {
                    mPrinter.PrintBitmapAtCenter(bitmap, 385, 415);
                }
                mPrinter.Close();
            }
        } else {
            Toast.makeText(MainActivity.this, "请先生成二维码", Toast.LENGTH_SHORT).show();
        }
    }

    //添加文字
    public static Bitmap addTextToBitmap(Bitmap bmpSrc, String text1, String text2, String text3) {
        int srcWidth = bmpSrc.getWidth();
        int srcHeight = bmpSrc.getHeight();
        int text1Length = (int) ((text1.length() + 6) * 14.07);
        int tex21Length = (int) ((text2.length() + 6) * 14.07);
        int tex31Length = (int) ((text3.length() + 7) * 14.07);

        int offsetX = 0;
        // 先计算text所需要的height
        int textSize = 25;//设置Text大小
        //计算3个文本输入的最大长度
        offsetX = text1Length > tex21Length ? text1Length : tex21Length;
        offsetX = offsetX > tex31Length ? offsetX : tex31Length;
        Bitmap bitmap = Bitmap.createBitmap(385, srcHeight + 120,
                Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bmpSrc, 100, 0, null);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            canvas.drawText("WBS号:" + text1.trim(), (390 - offsetX) / 2 + 5, srcHeight + 35, paint);
            canvas.drawText("物料号:" + text2.trim(), (390 - offsetX) / 2 + 5, srcHeight + 70, paint);
            canvas.drawText("供应商号:" + text3.trim(), (390 - offsetX) / 2 + 5, srcHeight + 105, paint);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.traceroute_rootview) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}





