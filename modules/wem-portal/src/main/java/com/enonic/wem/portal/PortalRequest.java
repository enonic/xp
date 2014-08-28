package com.enonic.wem.portal;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.rendering.RenderingMode;

public interface PortalRequest
{
    public String getMethod();

    public Workspace getWorkspace();

    public Multimap<String, String> getParams();

    public RenderingMode getMode();
}
