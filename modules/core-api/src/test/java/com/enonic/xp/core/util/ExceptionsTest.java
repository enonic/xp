package com.enonic.xp.core.util;

import java.io.IOException;

import org.junit.Test;

import com.enonic.xp.core.util.Exceptions;

public class ExceptionsTest
{
    @Test(expected = IOException.class)
    public void throwUnchecked()
    {
        throw Exceptions.unchecked( new IOException() );
    }
}
