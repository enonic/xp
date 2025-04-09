package com.enonic.xp.core.internal;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionsTest
{
    @Test
    void throwIfUncheckedWithRuntimeException()
    {
        final RuntimeException exception = new RuntimeException( "Runtime exception" );
        assertThrows( RuntimeException.class, () -> Exceptions.throwIfUnchecked( exception ) );
    }

    @Test
    void throwIfUncheckedWithError()
    {
        final Error error = new Error( "Error" );
        assertThrows( Error.class, () -> Exceptions.throwIfUnchecked( error ) );
    }

    @Test
    void throwIfUncheckedWithCheckedException()
    {
        final Exception exception = new Exception( "Checked exception" );
        assertDoesNotThrow( () -> Exceptions.throwIfUnchecked( exception ) );
    }

    @Test
    void throwCauseWithRuntimeExceptionCause()
    {
        final RuntimeException cause = new RuntimeException( "Runtime cause" );
        final ExecutionException exception = new ExecutionException( cause );
        final RuntimeException runtimeException = assertThrows( RuntimeException.class, () -> Exceptions.throwCause( exception ) );
        assertSame( cause, runtimeException );
    }

    @Test
    void throwCauseWithNonRuntimeExceptionCause()
    {
        final Exception cause = new Exception( "Non-runtime cause" );
        final ExecutionException exception = new ExecutionException( cause );
        final RuntimeException runtimeException = assertThrows( RuntimeException.class, () -> Exceptions.throwCause( exception ) );
        assertSame( cause, runtimeException.getCause() );
    }
}
