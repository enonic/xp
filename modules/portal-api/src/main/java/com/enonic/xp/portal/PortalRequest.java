package com.enonic.xp.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.collect.Multimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;

@Beta
public interface PortalRequest
{
    String getUri();

    String getMethod();

    Branch getBranch();

    ContentPath getContentPath();

    HttpServletRequest getRawRequest();

    String getBaseUri();

    Multimap<String, String> getParams();

    Multimap<String, String> getFormParams();

    Multimap<String, String> getHeaders();

    Map<String, String> getCookies();

    RenderMode getMode();

    String rewriteUri( String uri );
}
