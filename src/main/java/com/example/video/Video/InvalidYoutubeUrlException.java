package com.example.video.Video;

import com.example.video.Video.InvalidYoutubeUrlException;

public class InvalidYoutubeUrlException extends RuntimeException {

    public InvalidYoutubeUrlException(String message) {
        super(message);
    }

}
