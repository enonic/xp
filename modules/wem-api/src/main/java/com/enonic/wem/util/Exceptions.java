package com.enonic.wem.util;

import java.text.MessageFormat;

public abstract class Exceptions
{
    public final static class Builder<T extends Throwable>
    {
        private final T exception;

        private Builder( final T exception )
        {
            this.exception = exception;
        }

        public T withCause( final Throwable cause )
        {
            this.exception.initCause( cause );
            return this.exception;
        }

        public T withoutCause()
        {
            return this.exception;
        }
    }

    public static Builder<RuntimeException> newRutime( final String message, final Object... args )
    {
        return new Builder<>( new RuntimeException( format( message, args ) ) );
    }

    private static String format( final String message, final Object... args )
    {
        return MessageFormat.format( message, args );
    }
}
