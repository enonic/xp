package com.enonic.xp.web.handler;

import javax.servlet.http.Cookie;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;

interface WebResponse
{
    HttpStatus getStatus();

    MediaType getContentType();

    ImmutableMap<String, String> getHeaders();

    ImmutableList<Cookie> getCookies();

    WebSocketConfig getWebSocket();

    Object getBody();

//    boolean isPostProcess();

//    ImmutableList<String> getContributions( final HtmlTag tag );
//
//    boolean hasContributions();

//    String getAsString();

//    boolean applyFilters();
}
