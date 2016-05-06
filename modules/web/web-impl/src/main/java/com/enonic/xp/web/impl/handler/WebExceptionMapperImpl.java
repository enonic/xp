package com.enonic.xp.web.impl.handler;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebExceptionMapper;
import com.enonic.xp.web.handler.WebResponse;

@Component
public final class WebExceptionMapperImpl implements WebExceptionMapper
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
