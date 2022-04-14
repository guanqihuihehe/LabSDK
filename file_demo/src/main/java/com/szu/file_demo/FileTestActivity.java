package com.szu.file_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

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
            mFileManager.getInternalFilePath();
        });
        findViewById(R.id.get_external_path).setOnClickListener(v -> {
            mFileManager.getExternalFilePath();
        });
        findViewById(R.id.get_external_storage).setOnClickListener(v -> {
            mFileManager.getExternalStoragePath();
        });

        findViewById(R.id.create_new_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            mFileManager.createDir(dirName);
        });
        findViewById(R.id.create_outside_dir).setOnClickListener(v -> {
            EditText dirNameEditText = findViewById(R.id.store_dir);
            String dirName = dirNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.createDir(dirName, parentPath);
        });

        findViewById(R.id.create_new_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            mFileManager.createFile(fileName);
        });
        findViewById(R.id.create_outside_file).setOnClickListener(v -> {
            EditText fileNameEditText = findViewById(R.id.store_file);
            String fileName = fileNameEditText.getText().toString();
            String parentPath = mFileManager.getExternalStoragePath();
            mFileManager.createFile(fileName, parentPath);
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