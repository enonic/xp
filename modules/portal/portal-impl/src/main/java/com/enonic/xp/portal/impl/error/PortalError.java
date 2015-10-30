package com.enonic.xp.portal.impl.error;


import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpStatus;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PortalError
{
    private final HttpStatus status;

    private final String message;

    private final Exception exception;

    private final PortalRequest request;

    private PortalError( final Builder builder )
    {
        this.status = checkNotNull( builder.status, "status cannot be null" );
        this.request = checkNotNull( builder.request, "request cannot be null" );
        this.message = builder.message == null ? "" : builder.message;
        this.exception = builder.exception;
    }

    public HttpStatus getStatus()
    {
        return status;
    }

    public String getMessage()
    {
        return message;
    }

    public Exception getException()
    {
        return exception;
    }

    public PortalRequest getRequest()
    {
        return request;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PortalError source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private HttpStatus status;

        private String message;

        private Exception exception;

        private PortalRequest request;

        private Builder()
        {
            this.status = null;
            this.message = "";
            this.exception = null;
            this.request = null;
        }

        private Builder( final PortalError source )
        {
            this.status = source.status;
            this.message = source.message;
            this.exception = source.exception;
            this.request = source.request;
        }

        public Builder status( final HttpStatus status )
        {
            this.status = status;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder exception( final Exception exception )
        {
            this.exception = exception;
            return this;
        }

        public Builder request( final PortalRequest request )
        {
            this.request = request;
            return this;
        }

        public PortalError build()
        {
            return new PortalError( this );
        }
    }
}
