package com.enonic.wem.portal.script;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.resource.ResourceKey;

public final class SourceException
    extends RuntimeException
{
    private final String message;

    private final ResourceKey resource;

    private final Path path;

    private final int lineNumber;

    private final ImmutableList<String> callStack;

    private SourceException( final Builder builder )
    {
        if ( builder.cause != null )
        {
            initCause( builder.cause );
        }

        this.message = builder.message;
        this.resource = builder.resource;
        this.path = builder.path;
        this.lineNumber = builder.lineNumber;
        this.callStack = ImmutableList.copyOf( builder.callStack );
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }

    public Path getPath()
    {
        return this.path;
    }

    public int getLineNumber()
    {
        return this.lineNumber;
    }

    public List<String> getCallStack()
    {
        return this.callStack;
    }

    public SourceException getInnerSourceError()
    {
        return getInnerSourceError( getCause() );
    }

    private SourceException getInnerSourceError( final Throwable cause )
    {
        if ( cause == null )
        {
            return this;
        }

        if ( cause instanceof SourceException )
        {
            return (SourceException) cause;
        }

        return getInnerSourceError( cause.getCause() );
    }

    public static class Builder
    {
        private String message;

        private Throwable cause;

        private Path path;

        private ResourceKey resource;

        private int lineNumber;

        private final List<String> callStack;

        private Builder()
        {
            this.callStack = Lists.newArrayList();
            this.lineNumber = -1;
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

        public Builder path( final Path path )
        {
            this.path = path;
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

        public SourceException build()
        {
            if ( this.message == null )
            {
                this.message = this.cause != null ? this.cause.getMessage() : null;
            }

            if ( this.message == null )
            {
                this.message = "Empty message in exception";
            }

            return new SourceException( this );
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }
}
