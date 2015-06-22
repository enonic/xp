package com.enonic.xp.portal.impl.resource.render;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
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
    protected PortalResponse execute( final PortalRequest portalRequest )
        throws Exception
    {
        final PageUrlParams pageUrlParams = new PageUrlParams().id( target.toString() ).portalRequest( portalRequest );
        pageUrlParams.getParams().putAll( portalRequest.getParams() );

        final String targetUrl = this.services.getPortalUrlService().pageUrl( pageUrlParams );

        return PortalResponse.create().status( TEMPORARY_REDIRECT.getStatusCode() ).header( "location", targetUrl ).build();
    }
}
