package com.enonic.wem.portal.exception;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.web.jaxrs.ExtendedStatus;

public final class PortalWebException
    extends WebApplicationException
{
    private final static String DEFAULT_DESCRIPTION = "An error occured with status code = {0}.";

    private final Response.StatusType status;

    private final String description;

    private PortalWebException( final Builder builder )
    {
        super( builder.status.getStatusCode() );
        this.status = builder.status;
        this.description = builder.description;

        if ( builder.cause != null )
        {
            initCause( builder.cause );
        }
    }

    public Response.StatusType getStatus()
    {
        return this.status;
    }

    public String getDescription()
    {
        return this.description;
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

        private String description;

        private Builder( final Response.StatusType status )
        {
            this.status = status;
            description( DEFAULT_DESCRIPTION, this.status.getStatusCode() );
        }

        public Builder cause( final Throwable cause )
        {
            this.cause = cause;
            return this;
        }

        public Builder description( final String message, final Object... args )
        {
            this.description = MessageFormat.format( message, args );
            return this;
        }

        public PortalWebException build()
        {
            return new PortalWebException( this );
        }
    }
}
