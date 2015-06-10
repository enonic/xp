package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.content.page.PageTemplate;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.resource.controller.ControllerResource;
import com.enonic.xp.site.Site;

public abstract class RendererControllerResource
    extends ControllerResource
{
    protected Content content;

    protected Component component;

    protected Site site;

    protected ModuleKey moduleKey;

    protected PageTemplate pageTemplate;

    protected PageDescriptor pageDescriptor;

    @Override
    protected final void configure( final PortalRequest portalRequest )
    {
        portalRequest.setContent( this.content );
        portalRequest.setComponent( this.component );
        portalRequest.setSite( this.site );
        portalRequest.setModule( this.moduleKey );
        portalRequest.setPageDescriptor( this.pageDescriptor );
        portalRequest.setPageTemplate( this.pageTemplate );
    }
}
