package com.szu.audio_record.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WavUtils {

    /**
     * PCM文件转WAV文件
     *
     * @param inPcmFilePath  输入PCM文件路径
     * @param outWavFilePath 输出WAV文件路径
     * @param sampleRate     采样率，例如44100
     * @param channels       声道数 单声道：1或双声道：2
     * @param bitNum         采样位数，8或16
     */
    public static void convertPcm2Wav(String inPcmFilePath, String outWavFilePath, int sampleRate,int channels, int bitNum) {

        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];

        try {

            in = new FileInputStream(inPcmFilePath);
            out = new FileOutputStream(outWavFilePath);

            //PCM文件大小
            long totalAudioLen = in.getChannel().size();

            //总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalDataLen = totalAudioLen + 36;

            byte[] header;
            header = generateWavFileHeader((int)totalAudioLen, sampleRate, channels, bitNum);
            out.write(header, 0, 44);

            int length = 0;
            while ((length = in.read(data)) > 0) {
                out.write(data, 0, length);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成wav格式的Header
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk（可选）,Data chunk
     *
     * @param totalAudioLen 不包括header的音频数据总长度
     * @param sampleRate    采样率,也就是录制时使用的频率
     * @param channels      audioRecord的频道数量
     * @param sampleBits    位宽
     */
    public static byte[] generateWavFileHeader(int totalAudioLen, int sampleRate, int channels, int sampleBits) {
        WavHeader wavHeader = new WavHeader(totalAudioLen, sampleRate, (short) channels, (short) sampleBits);
        return wavHeader.getHeader();
    }

    /**
     * 将header写入到pcm文件中 不修改文件名
     *
     * @param file   写入的pcm文件
     * @param header wav头数据
     */
    public static void writeHeader(File file, byte[] header) {

        RandomAccessFile wavRaf = null;
        try {
            wavRaf = new RandomAccessFile(file, "rw");
            wavRaf.seek(0);
            wavRaf.write(header);
            wavRaf.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (wavRaf != null) {
                    wavRaf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}