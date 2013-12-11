package com.enonic.wem.portal.exception;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.web.jaxrs.ExtendedStatus;

public final class PortalWebException
    extends WebApplicationException
{
    private final static String DEFAULT_MESSAGE = "An error occured with status code = {0}.";

    private final Response.StatusType status;

    private final String message;

    private PortalWebException( final Builder builder )
    {
        super( builder.status.getStatusCode() );
        this.status = builder.status;
        this.message = builder.message;

        if ( builder.cause != null )
        {
            initCause( builder.cause );
        }
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }

    public Response.StatusType getStatus()
    {
        return this.status;
    }

    @Override
    public StackTraceElement[] getStackTrace()
    {
        final StackTraceElement[] list = super.getStackTrace();
        return Arrays.copyOfRange( list, 1, list.length - 1 );
    }

    public static Builder notFound()
    {
        return new Builder( Response.Status.NOT_FOUND );
    }

    public static Builder methodNotAllowed()
    {
        return new Builder( ExtendedStatus.METHOD_NOT_ALLOWED );
    }

    public static class Builder
    {
        private final Response.StatusType status;

        private Throwable cause;

        private String message;

        private Builder( final Response.StatusType status )
        {
            this.status = status;
            message( DEFAULT_MESSAGE, this.status.getStatusCode() );
        }

        public Builder cause( final Throwable cause )
        {
            this.cause = cause;
            return this;
        }

        public Builder message( final String message, final Object... args )
        {
            this.message = MessageFormat.format( message, args );
            return this;
        }

        public PortalWebException build()
        {
            return new PortalWebException( this );
        }
    }
}
