package com.video.streaming.api.util;

import jakarta.servlet.http.HttpServletRequest;

public class Url {

    public static String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://"+ request.getServerName() +":"+ request.getServerPort() + request.getContextPath();
    }
}
