package com.enonic.wem.portal;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.workspace.Workspace;

public interface PortalRequest
{
    public String getMethod();

    public Workspace getWorkspace();

    public Multimap<String, String> getParams();

    public RenderingMode getMode();

    public String getBaseUri();
}
