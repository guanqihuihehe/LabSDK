package com.szu.upload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UploadTestActivity extends AppCompatActivity {

    IUploadService mUploadService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test);
        EditText urlEditText = findViewById(R.id.upload_url);
        EditText srcPathEditText = findViewById(R.id.src_path);
        String url = urlEditText.getText().toString();
        String srcPath = srcPathEditText.getText().toString();
        mUploadService = new UploadService();
        findViewById(R.id.start_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (url.equals("")) {
                    Toast.makeText(UploadTestActivity.this, "url为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (srcPath.equals("")) {
                    Toast.makeText(UploadTestActivity.this, "srcPath为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                 mUploadService.startUpload(srcPath, url, new IUploadListener() {
                     @Override
                     public void onStart() {
                         Toast.makeText(UploadTestActivity.this, "start upload", Toast.LENGTH_SHORT).show();
                     }

                     @Override
                     public void onResult(int resultCode, String resultMsg) {
                         Toast.makeText(UploadTestActivity.this, "onResult:"+resultCode+" Msg:"+resultMsg, Toast.LENGTH_SHORT).show();
                     }

                     @Override
                     public void onFailed(int errorCode, String errorMsg) {
                         Toast.makeText(UploadTestActivity.this, "onResult:"+errorCode+" Msg:"+errorMsg, Toast.LENGTH_SHORT).show();
                     }
                 });
            }
        });
    }
}
