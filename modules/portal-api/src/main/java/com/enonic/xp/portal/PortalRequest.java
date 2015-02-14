package com.enonic.xp.portal;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Multimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;

public interface PortalRequest
{
    public String getUri();

    public String getMethod();

    public Branch getBranch();

    public ContentPath getContentPath();

    public HttpServletRequest getRawRequest();

    public String getBaseUri();

    public Multimap<String, String> getParams();

    public Multimap<String, String> getFormParams();

    public Multimap<String, String> getHeaders();

    public RenderMode getMode();

    public String rewriteUri( String uri );
}
