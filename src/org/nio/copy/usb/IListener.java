package org.nio.copy.usb;

public interface IListener {
    void onStart();
    void onComplete();
    void onError();
    void onCancel();
    void onProgress(long cur, long total);
}
