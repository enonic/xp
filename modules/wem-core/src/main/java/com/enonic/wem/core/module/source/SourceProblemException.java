package com.enonic.wem.core.module.source;

import java.text.MessageFormat;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class SourceProblemException
    extends RuntimeException
{
    private final String message;

    private final ModuleSource source;

    private final int lineNumber;

    private final ImmutableList<String> callStack;

    private SourceProblemException( final Builder builder )
    {
        if ( builder.cause != null )
        {
            initCause( builder.cause );
        }

        this.message = builder.message;
        this.source = builder.source;
        this.lineNumber = builder.lineNumber;
        this.callStack = ImmutableList.copyOf( builder.callStack );
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }

    public ModuleSource getSource()
    {
        return this.source;
    }

    public int getLineNumber()
    {
        return this.lineNumber;
    }

    public List<String> getCallStack()
    {
        return this.callStack;
    }

    public SourceProblemException getInnerError()
    {
        return getInnerError( getCause() );
    }

    private SourceProblemException getInnerError( final Throwable cause )
    {
        if ( cause == null )
        {
            return this;
        }

        if ( cause instanceof SourceProblemException )
        {
            return (SourceProblemException) cause;
        }

        return getInnerError( cause.getCause() );
    }

    public static class Builder
    {
        private String message;

        private Throwable cause;

        private ModuleSource source;

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
            if ( args.length > 0 )
            {
                this.message = MessageFormat.format( message, args );
            }
            else
            {
                this.message = message;
            }

            return this;
        }

        public Builder source( final ModuleSource source )
        {
            this.source = source;
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

        public SourceProblemException build()
        {
            if ( this.message == null )
            {
                this.message = this.cause != null ? this.cause.getMessage() : null;
            }

            if ( this.message == null )
            {
                this.message = "Empty message in exception";
            }

            return new SourceProblemException( this );
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }
}
