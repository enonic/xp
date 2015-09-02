package com.enonic.xp.portal.impl;

import com.enonic.xp.web.HttpStatus;

public final class PortalException
    extends RuntimeException
{
    private final HttpStatus status;

    public PortalException( final HttpStatus status, final String message )
    {
        super( message );
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public static PortalException notFound( final String message )
    {
        return new PortalException( HttpStatus.NOT_FOUND, message );
    }
}
