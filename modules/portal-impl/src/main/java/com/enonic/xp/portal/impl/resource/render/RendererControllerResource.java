package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.content.page.PageTemplate;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.resource.controller.ControllerResource;

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
    protected final void configure( final PortalContext context )
    {
        context.setContent( this.content );
        context.setComponent( this.component );
        context.setSite( this.site );
        context.setModule( this.moduleKey );
        context.setPageDescriptor( this.pageDescriptor );
        context.setPageTemplate( this.pageTemplate );
    }
}
