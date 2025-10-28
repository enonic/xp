package com.enonic.xp.util;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionsTest
{
    @Test
    void throwUnchecked()
    {
        assertThrows(IOException.class, () -> {throw Exceptions.unchecked( new IOException() ); } );
    }
}
