package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.RewritePathStrategy;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

public class RequestRewritePathStrategy
    implements RewritePathStrategy
{
    private final PortalRequest portalRequest;

    public RequestRewritePathStrategy( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
    }

    @Override
    public String rewritePath( final String path )
    {
        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( portalRequest.getRawRequest(), path );

        if ( rewritingResult.isOutOfScope() )
        {
            throw new OutOfScopeException( "URI out of scope" );
        }

        return rewritingResult.getRewrittenUri();
    }
}
