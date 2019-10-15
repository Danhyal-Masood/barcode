package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.bytedeco.javacv.JavaCVCL;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.android.*;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

public class BarcodeInterface extends AppCompatActivity implements CvCameraPreview.CvCameraViewListener {
    Mat m1;
     OpenCVFrameConverter.ToMat converter1 = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToOrgOpenCvCoreMat converter2 = new OpenCVFrameConverter.ToOrgOpenCvCoreMat();
    final String TAG="::DEBUG::";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_interface);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d(TAG,"test!");
        m1=new Mat(width,height);
    }

    @Override
    public void onCameraViewStopped() {
        m1.release();
    }

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
    @Override
    public Mat onCameraFrame(Mat mat) {
         org.opencv.core.Mat graymat=converter2.convert(converter1.convert(mat));
         System.out.println(graymat);
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
}
