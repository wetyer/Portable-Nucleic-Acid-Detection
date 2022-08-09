package com.example.myapplication;

import static android.content.ContentValues.TAG;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.FileNotFoundException;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private int nfa=0,sto=0,progress=122;
    private Button but;
    private  Button but1;
    private Button but2;
    private  Button but3;
    private Button but4;
    private SeekBar mLumSeekBar= (SeekBar) findViewById(R.id.lum_seek_bar);;
    private ImageView img;
    private Bitmap bitmap;
    private float mHue = 0, mSaturation = 1f, mLum = 1f;
    private static final int MID_VALUE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button but = findViewById(R.id.but);//导入荧光图片
        img = findViewById(R.id.img);
        Button but1 = findViewById(R.id.use);//荧光计算
        Button but2 = findViewById(R.id.butt);//导入一般图片
        Button but3 = findViewById(R.id.usee);//计算一般图片
        Button but4 = findViewById(R.id.calc);//泊松分析计算浓度

        mLumSeekBar = (SeekBar) findViewById(R.id.lum_seek_bar);


        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
                //类型的内容给你选择
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
                startActivityForResult(intent, 1);

            }
        });


        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count;
                int ltype = CvType.CV_16UC(1);
                int ltype1 = CvType.CV_8UC(1);
                int ltype2 = CvType.CV_32FC1;
                final Size kernelSize = new Size(1, 1);
                Mat kernel = Imgproc.getStructuringElement(1, kernelSize);
                Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                Mat src = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC(3));
                Utils.bitmapToMat(bit, src);

                //先写了再说
                int width = src.cols();
                int height = src.rows();
                int channels = src.channels();
                byte[] data = new byte[channels];
                int b = 0;
                int g = 0;
                int r = 0;


                //要紧部分

                Mat gray = new Mat(bit.getHeight(), bit.getWidth(), 3);
                Mat bi2 = new Mat();
                Mat bi22 = new Mat();
                Mat stats = new Mat();
                Mat con = new Mat();
                Mat dst = new Mat(bit.getHeight(), bit.getWidth(), 1);


                Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(gray, bi2, 70.0, 255.0, Imgproc.THRESH_BINARY);
                Imgproc.medianBlur(bi2, bi2, 5);
                Imgproc.morphologyEx(bi2, bi22, Imgproc.MORPH_OPEN, kernel);
                count = Imgproc.connectedComponentsWithStats(bi2, dst, stats, con, 4, ltype);
                nfa = count;
                //试着加点颜色(初始化)
                int colors[][] = new int[count + 1][3];
                int i, j;
                for (i = 1; i < (count + 1); i++) {
                    Random rd = new Random();
                    int a1 = rd.nextInt(255) + 1;
                    int a2 = rd.nextInt(255) + 1;
                    int a3 = rd.nextInt(255) + 1;
                    colors[i][0] = a1;
                    colors[i][1] = a2;
                    colors[i][2] = a3;
                }


                dst.convertTo(dst, ltype1);
                //试着加点颜色(理论可行)
//        Mat result = new Mat(src.size(),src.type());
//        for (i = 0; i < height ; ++i){
//            for (j = 0;j < width ; ++j){
//                dst.get(i-1,j-1,data);
//                int label = data[0];
//                if(label == 0){
//                    continue;
//                }
//                data[0]=(byte)colors[label][0];
//                data[1]=(byte)colors[label][1];
//                data[2]=(byte)colors[label][2];
//


//            result.put(i-1,j-1,data);
//            }
//        }


                Utils.matToBitmap(bi22, bitmap);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                TextView textView = (TextView) findViewById(R.id.tvShow);
                textView.setText("连通区域有" + count + "个");
            }
        });

//
//
////
////    {
//
//        mLumSeekBar.OnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//        public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser){
//            TextView textView = (TextView) findViewById(R.id.tvShow2);
//            textView.setText("变了");
//        }
////
////
//        public void onStartTrackingTouch (SeekBar seekBar,int progress, boolean fromUser){
//            TextView textView = (TextView) findViewById(R.id.tvShow2);
//            textView.setText("碰");
//        }
////
////
//        public void onStopTrackingTouch (SeekBar seekBar,int progress, boolean fromUser){
//            TextView textView = (TextView) findViewById(R.id.tvShow2);
//            textView.setText("不碰");
//        }
//
//
//}
//        );
//


    }




    public void hui_in(View view) {
        Intent intent=new Intent();
        intent.setType("image/*");
        //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
        //类型的内容给你选择
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
        startActivityForResult(intent, 1);
    }

    public void hui_calc(View view) {
        int count;

        int  ltype = CvType.CV_16UC(1);
        int  ltype1 = CvType.CV_8UC(1);
        final Size kernelSize = new Size(1, 1);
        Mat kernel = Imgproc.getStructuringElement(1,new Size(4, 4));
        Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        Mat src = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC(3));
        Utils.bitmapToMat(bit, src);

        //先写了再说
        int width = src.cols();
        int height = src.rows();
        int channels=src.channels();
        byte[] data = new byte[channels];
        int b=0,g=0,r=0;



        //要紧部分

        Mat gray = new Mat(bit.getHeight(), bit.getWidth(), 3);
        Mat bi2 = new Mat();
        Mat bi22 = new Mat();
        Mat stats = new Mat();
        Mat con = new Mat();
        Mat dst = new Mat(bit.getHeight(), bit.getWidth(),1);


        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray,bi2, 120.0, 255.0, Imgproc.THRESH_BINARY);
    //    Imgproc.erode(bi2, bi22, kernel); //腐蚀膨胀
        Imgproc.morphologyEx(bi2, bi22, Imgproc.MORPH_OPEN,kernel);
        count = Imgproc.connectedComponentsWithStats(bi22,dst,stats,con,4,ltype);
        sto = count;



        //试着加点颜色(初始化)
//        int colors[][] = new int [count+1][3];
//        int i,j;
//        for (i=1;i<(count+1);i++)
//        {
//            Random rd = new Random();
//            int a1 = rd.nextInt(255)+1;
//            int a2 = rd.nextInt(255)+1;
//            int a3 = rd.nextInt(255)+1;
//            colors[i][0] = a1;
//            colors[i][1] = a2;
//            colors[i][2] = a3;
//        }

//
//        dst.convertTo(dst,ltype1);
        //试着加点颜色(理论可行)
//        Mat result = new Mat(src.size(),src.type());
//        for (i = 0; i < height ; ++i){
//            for (j = 0;j < width ; ++j){
//                dst.get(i-1,j-1,data);
//                int label = data[0];
//                if(label == 0){
//                    continue;
//                }
//                data[0]=(byte)colors[label][0];
//                data[1]=(byte)colors[label][1];
//                data[2]=(byte)colors[label][2];
//


//            result.put(i-1,j-1,data);
//            }
//        }



        count=count-1;

        Utils.matToBitmap(bi22, bitmap);
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
        TextView textView=(TextView)findViewById(R.id.tvShow);
        textView.setText("连通区域有"+count+"个");

    }



    public void last_calc(View view) {
        double namuda;
        if (sto > nfa) {
            namuda = -Math.log(1 - ((double) nfa / sto));
            //    double v =
            TextView textView = (TextView) findViewById(R.id.tvShow);
            textView.setText("发现荧光微滴" + nfa + "个\n" + "发现总共微滴" + sto + "个\n" + "微滴内核算拷贝数为："+namuda+"个\n" );
        }
        else{
            TextView textView = (TextView) findViewById(R.id.tvShow);
            textView.setText("发现荧光微滴" + nfa + "个\n" + "发现总共微滴" + sto + "个\n" + "出问题了，请重试吧！");
        }
    }


//

//
//    };









//
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.lum_seek_bar:
//                mLum = progress * 1f / (MID_VALUE*2)*255;
//                break;
//        }
//        int count;
//        int  ltype = CvType.CV_16UC(1);
//        int  ltype1 = CvType.CV_8UC(1);
//        int  ltype2 = CvType.CV_32FC1;
//        final Size kernelSize = new Size(1, 1);
//        Mat kernel = Imgproc.getStructuringElement(1,kernelSize);
//        Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, false);
//        Mat src = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC(3));
//        Utils.bitmapToMat(bit, src);
//
//        //先写了再说
//        int width = src.cols();
//        int height = src.rows();
//        int channels=src.channels();
//        byte[] data = new byte[channels];
//        int b=0;
//        int g=0;
//        int r=0;
//
//
//
//        //要紧部分
//
//        Mat gray = new Mat(bit.getHeight(), bit.getWidth(), 3);
//        Mat bi2 = new Mat();
//        Mat bi22 = new Mat();
//        Mat stats = new Mat();
//        Mat con = new Mat();
//        Mat dst = new Mat(bit.getHeight(), bit.getWidth(),1);
//
//
//        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(gray,bi2, mLum, 255.0, Imgproc.THRESH_BINARY);
//        Imgproc.medianBlur(bi2,bi2,5);
//        Imgproc.morphologyEx(bi2, bi22, Imgproc.MORPH_OPEN,kernel);
//        count = Imgproc.connectedComponentsWithStats(bi2,dst,stats,con,4,ltype);
//
//        //试着加点颜色(初始化)
//        int colors[][] = new int [count+1][3];
//        int i,j;
//        for (i=1;i<(count+1);i++)
//        {
//            Random rd = new Random();
//            int a1 = rd.nextInt(255)+1;
//            int a2 = rd.nextInt(255)+1;
//            int a3 = rd.nextInt(255)+1;
//            colors[i][0] = a1;
//            colors[i][1] = a2;
//            colors[i][2] = a3;
//        }
//
//
//
//        dst.convertTo(dst,ltype1);
//        //试着加点颜色(理论可行)
////        Mat result = new Mat(src.size(),src.type());
////        for (i = 0; i < height ; ++i){
////            for (j = 0;j < width ; ++j){
////                dst.get(i-1,j-1,data);
////                int label = data[0];
////                if(label == 0){
////                    continue;
////                }
////                data[0]=(byte)colors[label][0];
////                data[1]=(byte)colors[label][1];
////                data[2]=(byte)colors[label][2];
////
//
//
////            result.put(i-1,j-1,data);
////            }
////        }
//
//
//
//
//
//        Utils.matToBitmap(bi22, bitmap);
//        Message message=new Message();
//        message.what=1;
//        handler.sendMessage(message);
//        TextView textView=(TextView)findViewById(R.id.tvShow);
//        textView.setText("连通区域有"+count+"个");
//
//    }
//













    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if(resultCode==RESULT_OK){
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }else{
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {

            Log.i("cv", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.i("cv", "OpenCV library found inside package. Using it!");
        }
    }
    Handler handler=new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case 1:img.setImageBitmap(bitmap);break;
            }
        }
    };

}
/**
 * 连通域分析
 * author: yidong
 * 2020/6/7
 */
