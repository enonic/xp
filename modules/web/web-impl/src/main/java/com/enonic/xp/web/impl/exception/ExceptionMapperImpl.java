package com.enonic.xp.web.impl.exception;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;

@Component
public final class ExceptionMapperImpl
    implements ExceptionMapper
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

    public void throwIfNeeded( final WebResponse res )
        throws WebException
    {
        final HttpStatus status = res.getStatus();
        final Object body = res.getBody();

        if ( isError( status ) && ( body == null ) )
        {
            throw new WebException( status, status.getReasonPhrase() );
        }
    }

    private boolean isError( final HttpStatus status )
    {
        return ( status != null ) && ( status.is4xxClientError() || status.is5xxServerError() );
    }
}
