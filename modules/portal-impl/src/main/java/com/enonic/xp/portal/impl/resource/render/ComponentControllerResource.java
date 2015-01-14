package com.enonic.xp.portal.impl.resource.render;

import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;

public final class ComponentControllerResource
    extends RendererControllerResource
{
    protected Renderer<Component> renderer;

    @Override
    protected RenderResult execute( final PortalContext context )
        throws Exception
    {
        return this.renderer.render( this.component, context );
    }
}
