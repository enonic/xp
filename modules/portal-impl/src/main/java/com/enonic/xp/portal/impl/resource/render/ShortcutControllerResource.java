package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.url.PageUrlParams;

import static javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT;

public final class ShortcutControllerResource
    extends RendererControllerResource
{
    private final ContentId target;

    public ShortcutControllerResource( final ContentId target )
    {
        this.target = target;
    }

    @Override
    protected RenderResult execute( final PortalContext context )
        throws Exception
    {
        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() ).context( context );
        pageUrlParams.getParams().putAll( context.getParams() );

        final String targetUrl = this.services.getPortalUrlService().pageUrl( pageUrlParams );

        return RenderResult.newRenderResult().status( TEMPORARY_REDIRECT.getStatusCode() ).header( "location", targetUrl ).build();
    }
}
