package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public final class ExceptionMapper
{
    public WebException map( final Throwable cause )
    {
        if ( cause instanceof WebException )
        {
            return (WebException) cause;
        }

        if ( cause instanceof NotFoundException )
        {
            return new WebException( HttpStatus.NOT_FOUND, cause );
        }

        if ( cause instanceof IllegalArgumentException )
        {
            return new WebException( HttpStatus.BAD_REQUEST, cause );
        }

        return new WebException( HttpStatus.INTERNAL_SERVER_ERROR, cause );
    }

    public void throwIfNeeded( final PortalResponse res )
        throws WebException
    {
        final HttpStatus status = res.getStatus();
        if ( isError( status ) )
        {
            throw new WebException( status, status.getReasonPhrase() );
        }
    }

    private boolean isError( final HttpStatus status )
    {
        return ( status != null ) && ( status.is4xxClientError() || status.is5xxServerError() );
    }
}
