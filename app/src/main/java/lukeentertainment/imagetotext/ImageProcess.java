package lukeentertainment.imagetotext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImageProcess extends AppCompatActivity {
    BaseLoaderCallback baseLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status)
            {
                case BaseLoaderCallback.SUCCESS:{
                    func();

                }
                break;
                default : super.onManagerConnected(status);
            }

        }
    };


    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        initializeView();


    }

    private void initializeView() {
        view=(ImageView) findViewById(R.id.loadImageView);
    }

    void func()
    {
        Bundle extras=getIntent().getExtras();
        String path=extras.getString("path");
        System.out.println("Activity : "+path);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mediaStorageDir.getPath() + File.separator + "IMG_" + path + ".jpg");
        Mat mat=Mat.zeros(bitmap.getHeight(),bitmap.getWidth(),CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,mat,false);
        int size=(int)mat.total()*mat.channels();
        byte[] b=new byte[size];
        mat.get(0,0,b);
        int col=mat.cols();
        int row=mat.rows();
        int x,y,r=0,c=0;

        for(x=0,r=0;x<row;x++,r=r+4)
        {
            for(y=0,c=0;y<col;y++,c=c+4)
            {
                int avg=(((b[x*col*4+c]&0xff)+(b[x*col*4+c+1]&0xff)+(b[x*col*4+c+2]&0xff))/3);

                if(avg>50)
                {
                    b[x*col*4+c+1]=(byte)255;
                    b[x*col*4+c+2]=(byte)255;

                    b[x*col*4+c]=(byte)255;
                }
                else {
                    b[x * col * 4 + c + 1] = 0;
                    b[x * col * 4 + c + 2] = 0;
                    b[x * col * 4 + c] = 0;
                }



            }
        }
        mat.put(0,0,b);
        Utils.matToBitmap(mat,bitmap);

        view.setImageBitmap(bitmap);



    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(OpenCVLoader.initDebug())
        {

            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else
        {

            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,getApplicationContext(),baseLoaderCallback);
        }
    }
}
