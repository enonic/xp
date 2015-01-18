package com.enonic.wem.api.util;

import java.io.IOException;

import org.junit.Test;

public class ExceptionsTest
{
    @Test(expected = IOException.class)
    public void throwUnchecked()
    {
        throw Exceptions.unchecked( new IOException() );
    }
}
