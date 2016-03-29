package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;

public final class ExceptionMapper
{
    public PortalException map( final Throwable cause )
    {
        if ( cause instanceof PortalException )
        {
            return (PortalException) cause;
        }

        if ( cause instanceof NotFoundException )
        {
            return new PortalException( HttpStatus.NOT_FOUND, cause );
        }

        if ( cause instanceof IllegalArgumentException )
        {
            return new PortalException( HttpStatus.BAD_REQUEST, cause );
        }

        return new PortalException( HttpStatus.INTERNAL_SERVER_ERROR, cause );
    }

    public void throwIfNeeded( final PortalResponse res )
        throws PortalException
    {
        final HttpStatus status = res.getStatus();
        if ( isError( status ) )
        {
            throw new PortalException( status, status.getReasonPhrase() );
        }
    }

    private boolean isError( final HttpStatus status )
    {
        return ( status != null ) && ( status.is4xxClientError() || status.is5xxServerError() );
    }
}
