package com.enonic.xp.portal.impl.resource.render;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
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
    protected final void configure( final PortalContextImpl context )
    {
        context.setContent( this.content );
        context.setComponent( this.component );
        context.setSite( this.site );
        context.setModule( this.moduleKey );
        context.setPageDescriptor( this.pageDescriptor );
        context.setPageTemplate( this.pageTemplate );
    }
}
