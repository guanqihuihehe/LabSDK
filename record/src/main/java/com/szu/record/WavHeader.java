package com.szu.record;

import com.szu.base.utils.ByteUtils;

public class WavHeader {
    /**
     * RIFF数据块
     */
    final String riffChunkId = "RIFF";
    int riffChunkSize;
    final String riffType = "WAVE";

    /**
     * FORMAT 数据块
     */
    final String formatChunkId = "fmt ";
    final int formatChunkSize = 16;
    final short audioFormat = 1;
    short channels;
    int sampleRate;
    int byteRate;
    short blockAlign;
    short sampleBits;

    /**
     * FORMAT 数据块
     */
    final String dataChunkId = "data";
    int dataChunkSize;

    WavHeader(int totalAudioLen, int sampleRate, short channels, short sampleBits) {
        this.riffChunkSize = totalAudioLen;
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.byteRate = sampleRate * sampleBits / 8 * channels;
        this.blockAlign = (short) (channels * sampleBits / 8);
        this.sampleBits = sampleBits;
        this.dataChunkSize = totalAudioLen - 44;
    }

    public byte[] getHeader() {
        byte[] result;
        result = ByteUtils.mergeBytes(ByteUtils.getBytes(riffChunkId), ByteUtils.getBytes(riffChunkSize));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(riffType));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(formatChunkId));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(formatChunkSize));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(audioFormat));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(channels));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(sampleRate));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(byteRate));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(blockAlign));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(sampleBits));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(dataChunkId));
        result = ByteUtils.mergeBytes(result, ByteUtils.getBytes(dataChunkSize));
        return result;
    }
}