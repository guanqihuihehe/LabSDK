package com.szu.base.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;

public class ByteUtils {

    /**
     * byte数组合并
     */
    public static byte[] mergeBytes(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }


    /**
     * byte[] to double[]
     *
     */
    public static double[] getDoubles(byte[] input){
        if(null == input ){
            return null;
        }
        DoubleBuffer buffer = ByteBuffer.wrap(input).asDoubleBuffer();
        double[] res = new double[buffer.remaining()];
        buffer.get(res);
        return res;
    }

    /**
     * byte[] to float[]
     *
     */
    public static float[] getFloats(byte[] input){
        if(null == input ){
            return null;
        }
        FloatBuffer buffer = ByteBuffer.wrap(input).asFloatBuffer();
        float[] res = new float[buffer.remaining()];
        buffer.get(res);
        return res;
    }

    /**
     * byte[] to long[]
     *
     */
    public static long[] getLongs(byte[] input){
        if(null == input ){
            return null;
        }
        LongBuffer buffer = ByteBuffer.wrap(input).asLongBuffer();
        long[] res = new long[buffer.remaining()];
        buffer.get(res);
        return res;
    }

    /**
     * byte[] to int[]
     *
     */
    public static int[] getInts(byte[] input){
        if(null == input ){
            return null;
        }
        IntBuffer buffer = ByteBuffer.wrap(input).asIntBuffer();
        int[] res = new int[buffer.remaining()];
        buffer.get(res);
        return res;
    }

    /**
     * byte[] to short[]
     *
     */
    public static short[] getShorts(byte[] input){
        if(null == input ){
            return null;
        }
        ShortBuffer buffer = ByteBuffer.wrap(input).asShortBuffer();
        short[] res = new short[buffer.remaining()];
        buffer.get(res);
        return res;
    }

    /**
     * byte[] to char[]
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }



    /**
     * double[] to byte[]
     *
     */
    public static byte[] getBytes(double[] input){
        if(null == input ){
            return null;
        }
        DoubleBuffer doubleBuffer = DoubleBuffer.wrap(input);
        ByteBuffer buffer = ByteBuffer.allocate(doubleBuffer.capacity()* (Double.SIZE/8));
        while(doubleBuffer.hasRemaining()){
            buffer.putDouble(doubleBuffer.get());
        }
        return buffer.array();
    }

    /**
     * float[] to byte[]
     */
    public static byte[] getBytes(float[] input){
        if(null == input ){
            return null;
        }
        FloatBuffer floatBuffer = FloatBuffer.wrap(input);
        ByteBuffer buffer = ByteBuffer.allocate(floatBuffer.capacity()* (Float.SIZE/8));
        while(floatBuffer.hasRemaining()){
            buffer.putFloat(floatBuffer.get());
        }
        return buffer.array();
    }

    /**
     * long[] to byte[]
     */
    public static byte[] getBytes(long[] input){
        if(null == input ){
            return null;
        }
        LongBuffer longBuffer = LongBuffer.wrap(input);
        ByteBuffer buffer = ByteBuffer.allocate(longBuffer.capacity()* (Long.SIZE/8));
        while(longBuffer.hasRemaining()){
            buffer.putLong(longBuffer.get());
        }
        return buffer.array();
    }

    /**
     * int[] to byte[]
     */
    public static byte[] getBytes(int[] input){
        if(null == input ){
            return null;
        }
        IntBuffer intBuffer = IntBuffer.wrap(input);
        ByteBuffer buffer = ByteBuffer.allocate(intBuffer.capacity()* (Integer.SIZE/8));
        while(intBuffer.hasRemaining()){
            buffer.putInt(intBuffer.get());
        }
        return buffer.array();
    }

    /**
     * short[] to byte[]
     */
    public static byte[] getBytes(short[] input){
        if(null == input ){
            return null;
        }
        ShortBuffer shortBuffer = ShortBuffer.wrap(input);
        ByteBuffer buffer = ByteBuffer.allocate(shortBuffer.capacity()* (Short.SIZE/8));
        while(shortBuffer.hasRemaining()){
            buffer.putShort(shortBuffer.get());
        }
        return buffer.array();
    }

    /**
     * char[] to byte[]
     */
    public static byte[] getBytes(char[] chars) {

        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }



    public static byte[] getBytes(char data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data);
        bytes[1] = (byte) (data >> 8);
        return bytes;
    }

    public static byte[] getBytes(short data)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static byte[] getBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static byte[] getBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static byte[] getBytes(float data)
    {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(double data)
    {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    public static byte[] getBytes(String data, String charsetName)
    {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    public static byte[] getBytes(String data)
    {
        return getBytes(data, "GBK");
    }


    public static short getShort(byte[] bytes)
    {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static char getChar(byte[] bytes)
    {
        return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static int getInt(byte[] bytes)
    {
        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
    }

    public static long getLong(byte[] bytes)
    {
        return(0xffL & (long)bytes[0]) | (0xff00L & ((long)bytes[1] << 8)) | (0xff0000L & ((long)bytes[2] << 16)) | (0xff000000L & ((long)bytes[3] << 24))
                | (0xff00000000L & ((long)bytes[4] << 32)) | (0xff0000000000L & ((long)bytes[5] << 40)) | (0xff000000000000L & ((long)bytes[6] << 48)) | (0xff00000000000000L & ((long)bytes[7] << 56));
    }

    public static float getFloat(byte[] bytes)
    {
        return Float.intBitsToFloat(getInt(bytes));
    }

    public static double getDouble(byte[] bytes)
    {
        long l = getLong(bytes);
        System.out.println(l);
        return Double.longBitsToDouble(l);
    }

    public static String getString(byte[] bytes, String charsetName)
    {
        return new String(bytes, Charset.forName(charsetName));
    }

    public static String getString(byte[] bytes)
    {
        return getString(bytes, "GBK");
    }

}