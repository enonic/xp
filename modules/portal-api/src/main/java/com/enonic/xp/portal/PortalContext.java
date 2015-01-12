package com.enonic.xp.portal;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.ModuleKey;

@SuppressWarnings("UnusedDeclaration")
public interface PortalContext
    extends PortalRequest
{
    public PortalResponse getResponse();

    public Content getContent();

    public Site getSite();

    public PageTemplate getPageTemplate();

    public Component getComponent();

    public void setComponent( Component component );

    public ModuleKey getModule();

    public PageDescriptor getPageDescriptor();
}
