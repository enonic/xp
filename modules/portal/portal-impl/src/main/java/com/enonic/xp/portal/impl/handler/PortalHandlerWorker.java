package com.enonic.xp.portal.impl.handler;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.web.HttpStatus;

public abstract class PortalHandlerWorker
{
    protected PortalRequest request;

    protected PortalResponse.Builder response;

    public abstract void execute()
        throws Exception;

    protected final PortalException notFound( final String message, final Object... args )
    {
        return PortalException.notFound( String.format( message, args ) );
    }

    protected final PortalException forbidden( final String message, final Object... args )
    {
        return new PortalException( HttpStatus.FORBIDDEN, String.format( message, args ) );
    }

    protected void setResponseCacheable()
    {
        this.response.header( HttpHeaders.CACHE_CONTROL, "private, max-age=31536000" );
    }
}
