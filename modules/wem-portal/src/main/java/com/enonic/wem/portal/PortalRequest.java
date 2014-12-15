package com.enonic.wem.portal;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.workspace.Workspace;

public interface PortalRequest
{
    public String getMethod();

    public Workspace getWorkspace();

    public Multimap<String, String> getParams();

    public RenderMode getMode();

    public String getBaseUri();
}
