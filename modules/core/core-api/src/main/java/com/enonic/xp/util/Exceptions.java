package com.enonic.xp.util;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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

    public static Builder<RuntimeException> newRuntime( final String message, final Object... args )
    {
        return new Builder<>( new RuntimeException( format( message, args ) ) );
    }

    private static String format( final String message, final Object... args )
    {
        return MessageFormat.format( message, args );
    }

    /**
     * Rethrows a checked exception as unchecked exception. This method tricks the compiler into
     * thinking the exception is unchecked, rather than wrapping the given exception in a new
     * {@code RuntimeException}.
     *
     * This method never returns. Nevertheless, it specifies a return type so it can be invoked as
     * {@code throw unchecked(e)} in contexts where an exception type is syntactically required
     * (e.g. when the enclosing method is non-void).
     *
     * @param e Throwable to be made unchecked.
     * @return This will never return anything. It's here to trick the compiler.
     */
    public static RuntimeException unchecked( final Throwable e )
    {
        Exceptions.adapt( e );
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void adapt( Throwable e )
        throws T
    {
        throw (T) e;
    }
}
