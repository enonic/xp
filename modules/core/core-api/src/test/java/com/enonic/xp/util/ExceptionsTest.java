package com.enonic.xp.util;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsTest
{
    @Test
    public void throwUnchecked()
    {
        assertThrows(IOException.class, () -> {throw Exceptions.unchecked( new IOException() ); } );
    }
}
