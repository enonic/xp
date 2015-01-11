package com.enonic.xp.portal.impl.resource.render;

import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

public final class ComponentControllerResource
    extends RendererControllerResource
{
    protected Renderer<Component> renderer;

    @Override
    protected RenderResult execute( final PortalContextImpl context )
        throws Exception
    {
        return this.renderer.render( this.component, context );
    }
}
