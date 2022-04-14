package com.szu.python_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class PythonTestActivity extends AppCompatActivity {

    static final String TAG = "PythonOnAndroid";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_test);
        initPython();
        callPythonCode();
    }


    // 初始化Python环境
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    // 调用python代码
    void callPythonCode(){
        Python py = Python.getInstance();
        // 调用hello.py模块中的greet函数，并传一个参数
        // 等价用法：py.getModule("hello").get("greet").call("Android");
        py.getModule("hello").callAttr("greet", "Android");

        // 调用python内建函数help()，输出了帮助信息
        py.getBuiltins().get("help").call();

        PyObject obj1 = py.getModule("hello").callAttr("add", 2,3);
        // 将Python返回值换为Java中的Integer类型
        Integer sum = obj1.toJava(Integer.class);
        Log.d(TAG,"add = "+sum.toString());

        // 调用python函数，命名式传参，等同 sub(10,b=1,c=3)
        PyObject obj2 = py.getModule("hello").callAttr("sub", 10,new Kwarg("b", 1), new Kwarg("c", 3));
        Integer result = obj2.toJava(Integer.class);
        Log.d(TAG,"sub = "+result.toString());

        // 调用Python函数，将返回的Python中的list转为Java的list
        PyObject obj3 = py.getModule("hello").callAttr("get_list", 10,"xx",5.6,'c');
        List<PyObject> pyList = obj3.asList();
        Log.d(TAG,"get_list = "+pyList.toString());

        // 将Java的ArrayList对象传入Python中使用
        List<PyObject> params = new ArrayList<PyObject>();
        params.add(PyObject.fromJava("alex"));
        params.add(PyObject.fromJava("bruce"));
        py.getModule("hello").callAttr("print_list", params);

        // Python中调用Java类
        PyObject obj4 = py.getModule("hello").callAttr("get_java_bean");
        JavaBean data = obj4.toJava(JavaBean.class);
        data.print();

        //加载tflite文件
        Interpreter mTflite = load_model("model_test");
        if (mTflite != null) {
            Toast.makeText(this, " model load success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, " model load fail", Toast.LENGTH_SHORT).show();
        }
    }

    // load infer model
    private Interpreter load_model(String model) {
        try {
            Interpreter tflite;
            tflite = new Interpreter(loadModelFile(model).asReadOnlyBuffer());
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
    private MappedByteBuffer loadModelFile(String model) throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd(model + ".tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}