package com.szu.python_api;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.tensorflow.lite.Interpreter;

public class PythonService {
    // load infer model
    private Interpreter load_model(String model, Context context) {
        try {
            Interpreter tflite;
            tflite = new Interpreter(loadModelFile(model, context).asReadOnlyBuffer());
            Log.d(TAG, model + " model load success");
            return tflite;
        } catch (IOException e) {
            Log.d(TAG, model + " model load fail");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(String model, Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(model + ".tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
