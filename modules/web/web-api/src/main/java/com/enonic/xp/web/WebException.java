package com.enonic.xp.web;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class WebException
    extends RuntimeException
{
    private final HttpStatus status;

    private final boolean loggable;

    public WebException( final @NonNull HttpStatus status, final String message )
    {
        super( message );
        this.status = status;
        this.loggable = this.status.is5xxServerError();
    }

    public WebException( final HttpStatus status, final String message, final boolean loggable )
    {
        super( message );
        this.status = status;
        this.loggable = loggable;
    }

    public WebException( final HttpStatus status, final Throwable cause )
    {
        super( cause.getMessage(), cause );
        this.status = status;
        this.loggable = this.status.is5xxServerError();
    }

    public WebException( final HttpStatus status, final String message, final Throwable cause )
    {
        super( message, cause );
        this.status = status;
        this.loggable = this.status.is5xxServerError();
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public boolean isLoggable()
    {
        return loggable;
    }

    public static WebException badRequest( final String message )
    {
        return new WebException( HttpStatus.BAD_REQUEST, message );
    }

    public static WebException badRequest( final String message, final Exception cause )
    {
        return new WebException( HttpStatus.BAD_REQUEST, message, cause );
    }

    public static WebException forbidden( final String message )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return new WebException( HttpStatus.FORBIDDEN, message );
        }
        else
        {
            return new WebException( HttpStatus.UNAUTHORIZED, message );
        }
    }

    public static WebException notFound( final String message )
    {
        return new WebException( HttpStatus.NOT_FOUND, message );
    }

    public static WebException internalServerError( final String message )
    {
        return new WebException( HttpStatus.INTERNAL_SERVER_ERROR, message );
    }

    public static WebException internalServerError( final String message, final Throwable cause )
    {
        return new WebException( HttpStatus.INTERNAL_SERVER_ERROR, message, cause );
    }
}