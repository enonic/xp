package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.page.region.Component;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.Renderer;

public final class ComponentControllerResource
    extends RendererControllerResource
{
    protected Renderer<Component> renderer;

    @Override
    protected PortalResponse execute( final PortalRequest portalRequest )
        throws Exception
    {
        return this.renderer.render( this.component, portalRequest );
    }
}
