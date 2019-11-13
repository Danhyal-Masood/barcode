package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.DecoderResultPointCallback;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import net.dongliu.requests.Requests;

import org.bytedeco.javacv.JavaCVCL;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opencv.android.*;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class BarcodeInterface extends AppCompatActivity {
    Mat m1;
    DecoratedBarcodeView barcodeView;
    String resulttxt;

        private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(resulttxt)) {
                return;
            }

            resulttxt= result.getText();
            barcodeView.setStatusText(result.getText());
            Log.d(TAG,resulttxt);

                Thread thread = new Thread(new Runnable() {
                    String apiendpoint=String.format("https://world.openfoodfacts.org/api/v0/product/%s.json",resulttxt);

                @Override
                public void run() {
                    try  {
                       JSONParser jsonParser=new JSONParser();

                        System.out.println(apiendpoint);
                        String response = Requests.post(apiendpoint).socksTimeout(10000).send().readToText();
                        Object obj=jsonParser.parse(response);
                        JSONObject jobj= (JSONObject) obj;
                        JSONObject product= (JSONObject) jobj.get("product");
                        System.out.println(jobj.keySet());
                        System.out.println(product.get("product_name_en"));
                        System.out.println(product.get("nutriments"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();



//            ImageView imageView = (ImageView) findViewById(R.id.cam);
//            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

        OpenCVFrameConverter.ToMat converter1 = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToOrgOpenCvCoreMat converter2 = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
    final String TAG="::DEBUG::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_interface);
        barcodeView=findViewById(R.id.cam);
        barcodeView.getBarcodeView();
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
    }
        @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

//    @Override
//    public void onCameraViewStarted(int width, int height) {
//        Log.d(TAG,"test!");
//        m1=new Mat(width,height);
//    }
//    Bitmap bitmap;
//    @Override
//    public void onCameraViewStopped() {
//        m1.release();
//    }



    public static Mat Detection(Mat mat){
         OpenCVFrameConverter.ToMat converter1 = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToOrgOpenCvCoreMat converter2 = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
        org.opencv.core.Mat graymat=converter2.convert(converter1.convert(mat));
        org.opencv.imgproc.Imgproc.cvtColor(graymat,graymat, Imgproc.COLOR_RGBA2GRAY);
        org.opencv.core.Mat gradx=new org.opencv.core.Mat(graymat.width(),graymat.height(), CvType.CV_32F);
        org.opencv.core.Mat grady=new org.opencv.core.Mat(graymat.width(),graymat.height(), CvType.CV_32F);
        org.opencv.core.Mat gradient = new org.opencv.core.Mat(graymat.width(),graymat.height(), CvType.CV_32F);

        org.opencv.imgproc.Imgproc.Sobel(graymat,gradx,CvType.CV_32F,1,0,-1);
        org.opencv.imgproc.Imgproc.Sobel(graymat,grady,CvType.CV_32F,0,1,-1);

        org.opencv.core.Core.subtract(gradx,grady,gradient);
        org.opencv.core.Core.convertScaleAbs(gradient,gradient);
        Log.d("::DEBUG::",String.format("print? %s",graymat.dataAddr()));
        return converter1.convert(converter2.convert(gradient));

    }
//    @Override
//    public Mat onCameraFrame(Mat mat) {
//
//        return Detection(mat);
//    }

}
