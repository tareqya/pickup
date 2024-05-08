package com.example.pickup.callbacks;

public interface ImageUrlDownloadListener {
    void onImageUrlDownloaded(String imageUrl);
    void onImageUrlDownloadFailed(String errorMessage);
}
