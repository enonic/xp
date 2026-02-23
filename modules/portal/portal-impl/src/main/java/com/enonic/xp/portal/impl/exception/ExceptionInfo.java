package com.enonic.xp.portal.impl.exception;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

final class ExceptionInfo
{
    final HttpStatus status;

    String tip;

    WebException cause;

    boolean withDebugInfo;

    private ExceptionInfo( final @NonNull HttpStatus status )
    {
        this.status = status;
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public boolean shouldLogAsError()
    {
        return cause.isLoggable();
    }

    public String getMessage()
    {
        final String str = this.cause != null ? this.cause.getMessage() : null;
        return str != null ? str : this.status.getReasonPhrase();
    }

    public String getDescription()
    {
        String str = this.getMessage();
        final Throwable innerCause = this.cause != null ? this.cause.getCause() : null;
        if ( innerCause != null )
        {
            str += " (" + innerCause.getClass().getName() + ")";
        }

        return str;
    }

    public ExceptionInfo tip( final String tip )
    {
        this.tip = tip;
        return this;
    }

    public ExceptionInfo withDebugInfo( boolean withDebugInfo )
    {
        this.withDebugInfo = withDebugInfo;
        return this;
    }

    public WebException getCause()
    {
        return this.cause;
    }

    public ExceptionInfo cause( final WebException cause )
    {
        this.cause = cause;
        return this;
    }

    public static ExceptionInfo create( final HttpStatus status )
    {
        return new ExceptionInfo( status );
    }
}
