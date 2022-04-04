package com.szu.file.bean;

public class FileBean {
    /** 文件的路径*/
    public String path;

    public FileBean(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "path='" + path + '\'' +
                '}';
    }
}
