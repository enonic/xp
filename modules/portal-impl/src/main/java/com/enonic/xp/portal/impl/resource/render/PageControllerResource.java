package com.enonic.xp.portal.impl.resource.render;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;

public final class PageControllerResource
    extends RendererControllerResource
{
    protected Renderer<Content> renderer;

    @Override
    protected RenderResult execute( final PortalContextImpl context )
        throws Exception
    {
        return this.renderer.render( this.content, context );
    }
}
