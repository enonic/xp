package com.enonic.xp.portal;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.workspace.Workspace;

public interface PortalRequest
{
    public String getUri();

    public String getMethod();

    public Workspace getWorkspace();

    // String getContentSelector();

    // public HttpServletRequest getRawRequest();

    public Multimap<String, String> getParams();

    public Multimap<String, String> getFormParams();

    public Multimap<String, String> getHeaders();

    public RenderMode getMode();

    public String rewriteUri( String uri );
}
