package com.ftp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BandwidthLimiter {
    private static final Logger logger = LoggerFactory.getLogger(BandwidthLimiter.class);
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final long BYTES_PER_KILOBYTE = 1024L;
    private static final long DEFAULT_BUCKET_SIZE = BYTES_PER_KILOBYTE * BYTES_PER_KILOBYTE;
    private static final long REFILL_INTERVAL_MS = 100L;
    
    private volatile long uploadSpeedBps = -1;
    private volatile long downloadSpeedBps = -1;
    
    private long uploadBucketTokens;
    private long downloadBucketTokens;
    private long uploadBucketSize;
    private long downloadBucketSize;
    private long lastUploadRefillTime;
    private long lastDownloadRefillTime;
    
    private final Object uploadLock = new Object();
    private final Object downloadLock = new Object();

    public BandwidthLimiter() {
        this.uploadBucketTokens = DEFAULT_BUCKET_SIZE;
        this.downloadBucketTokens = DEFAULT_BUCKET_SIZE;
        this.uploadBucketSize = DEFAULT_BUCKET_SIZE;
        this.downloadBucketSize = DEFAULT_BUCKET_SIZE;
        this.lastUploadRefillTime = System.currentTimeMillis();
        this.lastDownloadRefillTime = System.currentTimeMillis();
    }

    /**
     * 设置上传速度限制（字节/秒）
     * @param speedBps 速度，-1 表示不限制
     */
    public void setUploadSpeed(long speedBps) {
        synchronized (uploadLock) {
            this.uploadSpeedBps = speedBps;
            if (speedBps > 0) {
                this.uploadBucketSize = Math.max(speedBps / 10, 1024);
                this.uploadBucketTokens = uploadBucketSize;
            }
            logger.info("上传速度限制设置为：" + (speedBps > 0 ? speedBps + " B/s" : "无限制"));
        }
    }

    /**
     * 设置下载速度限制（字节/秒）
     * @param speedBps 速度，-1 表示不限制
     */
    public void setDownloadSpeed(long speedBps) {
        synchronized (downloadLock) {
            this.downloadSpeedBps = speedBps;
            if (speedBps > 0) {
                this.downloadBucketSize = Math.max(speedBps / 10, 1024);
                this.downloadBucketTokens = downloadBucketSize;
            }
            logger.info("下载速度限制设置为：" + (speedBps > 0 ? speedBps + " B/s" : "无限制"));
        }
    }

    public long getUploadSpeed() {
        return uploadSpeedBps;
    }

    public long getDownloadSpeed() {
        return downloadSpeedBps;
    }

    public boolean isUploadLimited() {
        return uploadSpeedBps > 0;
    }

    public boolean isDownloadLimited() {
        return downloadSpeedBps > 0;
    }

    /**
     * 限制上传速度
     * @param out 输出流
     * @param bytesToWrite 要写入的字节数
     */
    public void limitUpload(OutputStream out, int bytesToWrite) throws IOException {
        if (uploadSpeedBps <= 0) {
            out.flush();
            return;
        }

        synchronized (uploadLock) {
            refillUploadBucket();
            
            while (uploadBucketTokens < bytesToWrite) {
                long waitTime = Math.max(1, REFILL_INTERVAL_MS);
                try {
                    uploadLock.wait(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("上传被中断", e);
                }
                refillUploadBucket();
            }
            
            uploadBucketTokens -= bytesToWrite;
        }
        
        out.flush();
    }

    /**
     * 限制下载速度
     * @param in 输入流
     * @param bytesRead 已读取的字节数
     */
    public void limitDownload(InputStream in, int bytesRead) throws IOException {
        if (downloadSpeedBps <= 0) {
            return;
        }

        synchronized (downloadLock) {
            refillDownloadBucket();
            
            while (downloadBucketTokens < bytesRead) {
                long waitTime = Math.max(1, REFILL_INTERVAL_MS);
                try {
                    downloadLock.wait(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("下载被中断", e);
                }
                refillDownloadBucket();
            }
            
            downloadBucketTokens -= bytesRead;
        }
    }

    /**
     * 补充上传令牌桶
     */
    private void refillUploadBucket() {
        long now = System.currentTimeMillis();
        long timePassed = now - lastUploadRefillTime;
        
        if (timePassed >= REFILL_INTERVAL_MS) {
            long tokensToAdd = (uploadSpeedBps * timePassed) / MILLIS_PER_SECOND;
            uploadBucketTokens = Math.min(uploadBucketSize, uploadBucketTokens + tokensToAdd);
            lastUploadRefillTime = now;
        }
    }

    /**
     * 补充下载令牌桶
     */
    private void refillDownloadBucket() {
        long now = System.currentTimeMillis();
        long timePassed = now - lastDownloadRefillTime;
        
        if (timePassed >= REFILL_INTERVAL_MS) {
            long tokensToAdd = (downloadSpeedBps * timePassed) / MILLIS_PER_SECOND;
            downloadBucketTokens = Math.min(downloadBucketSize, downloadBucketTokens + tokensToAdd);
            lastDownloadRefillTime = now;
        }
    }

    /**
     * 重置所有限制
     */
    public void reset() {
        synchronized (uploadLock) {
            this.uploadSpeedBps = -1;
            this.uploadBucketTokens = DEFAULT_BUCKET_SIZE;
        }
        synchronized (downloadLock) {
            this.downloadSpeedBps = -1;
            this.downloadBucketTokens = DEFAULT_BUCKET_SIZE;
        }
    }

    /**
     * 获取当前上传可用令牌数
     */
    public long getAvailableUploadTokens() {
        synchronized (uploadLock) {
            refillUploadBucket();
            return uploadBucketTokens;
        }
    }

    /**
     * 获取当前下载可用令牌数
     */
    public long getAvailableDownloadTokens() {
        synchronized (downloadLock) {
            refillDownloadBucket();
            return downloadBucketTokens;
        }
    }
}
