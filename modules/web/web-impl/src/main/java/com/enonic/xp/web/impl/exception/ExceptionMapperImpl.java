package com.enonic.xp.web.impl.exception;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;

@Component
@NullMarked
public final class ExceptionMapperImpl
    implements ExceptionMapper
{
    @Override
    public WebException map( final Throwable cause )
    {
        return switch ( cause )
        {
            case WebException we -> we;
            default -> new WebException( HttpStatus.INTERNAL_SERVER_ERROR, cause );
        };
    }

    @Override
    public void throwIfNeeded( final WebResponse res )
        throws WebException
    {
        final HttpStatus status = res.getStatus();
        final Object body = res.getBody();

        if ( body == null && status != null && ( status.is4xxClientError() || status.is5xxServerError() ) )
        {
            throw new WebException( status, status.getReasonPhrase() );
        }
    }
}
