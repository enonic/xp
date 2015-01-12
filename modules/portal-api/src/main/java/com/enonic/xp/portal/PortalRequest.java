package com.enonic.xp.portal;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.workspace.Workspace;

public interface PortalRequest
{
    public String getUri();

    public String getMethod();

    public Workspace getWorkspace();

    public ContentPath getContentPath();

    public HttpServletRequest getRawRequest();

    public String getBaseUri();

    public Multimap<String, String> getParams();

    public Multimap<String, String> getFormParams();

    public Multimap<String, String> getHeaders();

    public RenderMode getMode();

    public String rewriteUri( String uri );
}
