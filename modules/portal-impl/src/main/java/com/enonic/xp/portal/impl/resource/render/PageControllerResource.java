package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.Renderer;

public final class PageControllerResource
    extends RendererControllerResource
{
    protected Renderer<Content> renderer;

    @Override
    protected PortalResponse execute( final PortalRequest portalRequest )
        throws Exception
    {
        return this.renderer.render( this.content, portalRequest );
    }
}
