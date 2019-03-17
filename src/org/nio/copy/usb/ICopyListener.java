package org.nio.copy.usb;

public interface ICopyListener {
    void onStart();
    void onComplete();
    void onError();
    void onProgress(long cur, long total);
}
