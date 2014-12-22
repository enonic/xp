package com.enonic.xp.portal;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.workspace.Workspace;

public interface PortalRequest
{
    public String getMethod();

    public Workspace getWorkspace();

    public Multimap<String, String> getParams();

    public Multimap<String, String> getHeaders();

    public RenderMode getMode();

    public String getBaseUri();
}
