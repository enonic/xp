package com.enonic.xp.resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.BaseException;

@PublicApi
public final class ResourceProblemException
    extends BaseException
{
    private final ResourceKey resource;

    private final int lineNumber;

    private final ImmutableList<String> callStack;

    private ResourceProblemException( final Builder builder )
    {
        super( builder.message );

        if ( builder.cause != null )
        {
            initCause( builder.cause );
        }

        this.resource = builder.resource;
        this.lineNumber = builder.lineNumber;
        this.callStack = ImmutableList.copyOf( builder.callStack );
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }

    public int getLineNumber()
    {
        return this.lineNumber;
    }

    public List<String> getCallStack()
    {
        return this.callStack;
    }

    public ResourceProblemException getInnerError()
    {
        return getInnerError( getCause() );
    }

    private ResourceProblemException getInnerError( final Throwable cause )
    {
        if ( cause == null )
        {
            return this;
        }

        if ( cause instanceof ResourceProblemException )
        {
            return (ResourceProblemException) cause;
        }

        return getInnerError( cause.getCause() );
    }

    public static class Builder
    {
        private String message;

        private Throwable cause;

        private ResourceKey resource;

        private int lineNumber;

        private final List<String> callStack;

        private Builder()
        {
            this.callStack = new ArrayList<>();
            this.lineNumber = -1;
        }

        public Builder cause( final Throwable cause )
        {
            this.cause = cause;
            return this;
        }

        public Builder message( final String message, final Object... args )
        {
            this.message = args.length > 0 ? MessageFormat.format( message, args ) : message;
            return this;
        }

        public Builder resource( final ResourceKey resource )
        {
            this.resource = resource;
            return this;
        }

        public Builder lineNumber( final int lineNumber )
        {
            this.lineNumber = lineNumber;
            return this;
        }

        public Builder callLine( final String name, final int lineNumber )
        {
            this.callStack.add( MessageFormat.format( "{0} at line {1}", name, lineNumber ) );
            return this;
        }

        public ResourceProblemException build()
        {
            if ( this.message == null )
            {
                this.message = this.cause != null ? this.cause.getMessage() : null;
            }

            if ( this.message == null )
            {
                this.message = "Empty message in exception";
            }

            return new ResourceProblemException( this );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }
}
