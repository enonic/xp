package com.enonic.xp.web.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;

@Beta
public interface WebRequest
{
    HttpMethod getMethod();

    String getScheme();

    String getHost();

    int getPort();

    String getPath();

    Multimap<String, String> getParams();

    String getUrl();

    String getEndpointPath();

    Map<String, String> getCookies();

    Object getBody();

    String getBodyAsString();

    HttpServletRequest getRawRequest();

    String getContentType();

    boolean isWebSocket();

    Object getAttribute( String name );

    void setAttribute( String name, Object o );

//    String getBaseUri();

//    Branch getBranch();

//    RenderMode getMode();

//    String rewriteUri( final String uri );

//    Site getSite();

//    Content getContent();

//    PageTemplate getPageTemplate();

//    Component getComponent();

//    ApplicationKey getApplicationKey()

//    PageDescriptor getPageDescriptor()

//    ContentPath getContentPath()

//    ControllerScript getControllerScript()

}
