package com.szu.python_api;

import android.content.Context;
import org.tensorflow.lite.Interpreter;

public interface IPythonService {
    /**
     * 加载tflite文件类型的模型
     * @param model 模型的文件名，不需要带tflite后缀
     * @param context 当前context
     * @return 返回tensorflow.lite库的Interpreter类型
     * */
    Interpreter load_model(String model, Context context);
}
