package com.szu.file_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.szu.file.FileManager;

public class FileTestActivity extends AppCompatActivity {

    FileManager mFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_test);
        initUI();
        mFileManager = FileManager.getInstance(this);
    }

    public void initUI() {

        findViewById(R.id.get_internal_path).setOnClickListener(v -> {
            Toast.makeText(FileTestActivity.this, mFileManager.getInternalFilePath(), Toast.LENGTH_SHORT);
        });
        findViewById(R.id.get_external_path).setOnClickListener(v -> {
            Toast.makeText(FileTestActivity.this, mFileManager.getExternalFilePath(), Toast.LENGTH_SHORT);
        });
        findViewById(R.id.get_external_storage).setOnClickListener(v -> {
            Toast.makeText(FileTestActivity.this, mFileManager.getExternalStoragePath(), Toast.LENGTH_SHORT);
        });

        findViewById(R.id.create_new_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            mFileManager.createDir(dirName);
            Toast.makeText(FileTestActivity.this, "创建成功:"+dirName, Toast.LENGTH_SHORT);
        });
        findViewById(R.id.create_outside_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.createDir(dirName, parentPath);
            Toast.makeText(FileTestActivity.this, "创建成功:"+parentPath+"/"+dirName, Toast.LENGTH_SHORT);
        });

        findViewById(R.id.create_new_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            mFileManager.createFile(fileName);
            Toast.makeText(FileTestActivity.this, "创建成功:"+fileName, Toast.LENGTH_SHORT);
        });
        findViewById(R.id.create_outside_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.createFile(fileName, parentPath);
            Toast.makeText(FileTestActivity.this, "创建成功:"+parentPath+"/"+fileName, Toast.LENGTH_SHORT);
        });

        findViewById(R.id.delete_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            mFileManager.deleteDir(dirName);
        });
        findViewById(R.id.delete_outside_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.deleteDir(dirName, parentPath);
        });

        findViewById(R.id.delete_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            mFileManager.deleteFile(fileName);
        });
        findViewById(R.id.delete_outside_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.deleteFile(fileName, parentPath);
        });

    }
}