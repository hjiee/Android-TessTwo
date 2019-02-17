package com.example.android_tesstwo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    private String mDataPath = "" ; //언어데이터가 있는 경로
    private final String[] mLanguage = {"kor","eng"};
    private Button m_btnOCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_btnOCR = findViewById(R.id.btn_OCR);
        m_btnOCR.setOnClickListener(this);

//        //이미지 디코딩을 위한 초기화
        image = BitmapFactory.decodeResource(getResources(), R.drawable.sampledata); //샘플이미지파일

        //언어파일 경로
        mDataPath = getFilesDir()+ "/tesseract/tessdata/";
        Log.e("TessTwo-Log",mDataPath);

        //트레이닝데이터가 카피되어 있는지 체크

        //Tesseract API

        /**
         * 여러 언어를 인식하고 싶을땐 아래와 같이 추가해서 문자열을 만들어준다.
         * 추가한 언어들의 tessdata의 경로를 추가하고 카피 해줘야 문제 없이 작동한다.
         */
        String lang = ""; // 영어와 한글 모두 인식하고 싶을때
        int count=0;
        for(String strLanguage : mLanguage)
        {
            lang += strLanguage;
            checkFile(new File(mDataPath),strLanguage +(".traineddata"));
            if(count != mLanguage.length-1)
                lang += ("+");
            count++;
            Log.e("TessTwo-Log",strLanguage);
        }
        Log.e("TessTwo-Log",lang);
        mTess = new TessBaseAPI();
        mTess.init(mDataPath, lang);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_OCR:
                processImage(v);
                break;
        }
    }

    //Process an Image
    public void processImage(View view) {
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = findViewById(R.id.tv_view);
        OCRTextView.setText(OCRresult);
    }


    //copy file to device
    private void copyFiles(String strLanguage) {
        try{
            String filepath = mDataPath + strLanguage;
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open(strLanguage);
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device
    private void checkFile(File dir,String strLanguage) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles(strLanguage);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = mDataPath+ strLanguage;
            File datafile = new File(datafilepath);
            if(!datafile.exists()) { // 데이터 파일이 없으면 파일을 카피
                copyFiles(strLanguage);
            }
        }
    }
}
