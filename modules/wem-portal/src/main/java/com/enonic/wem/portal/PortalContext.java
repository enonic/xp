package com.enonic.wem.portal;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutRegions;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.Module;

public interface PortalContext
{
    public PortalRequest getRequest();

    public PortalResponse getResponse();

    public RenderingMode getMode();

    public Content getContent();

    public Site getSite();

    public PageTemplate getPageTemplate();

    public LayoutRegions getLayoutRegions();

    public PageComponent getComponent();

    public void setComponent( PageComponent component );

    public Module getModule();
}
