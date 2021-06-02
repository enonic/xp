package com.enonic.xp.admin.impl.rest;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimitingInputStreamTest
{
    @Test
    void exceed()
    {
        final ByteArrayInputStream original = new ByteArrayInputStream( new byte[]{0, 0} );

        final LimitingInputStream<IllegalStateException> lis =
            new LimitingInputStream<>( original, 1, () -> new IllegalStateException( "Exceeded" ) );
        final IllegalStateException exception = assertThrows( IllegalStateException.class, lis::readAllBytes );
        assertEquals( "Exceeded", exception.getMessage() );
    }

    @Test
    void exceed_via_single_byte_read()
        throws Exception
    {
        final ByteArrayInputStream original = new ByteArrayInputStream( new byte[]{1, 2} );

        final LimitingInputStream<IllegalStateException> lis =
            new LimitingInputStream<>( original, 1, () -> new IllegalStateException( "Exceeded" ) );
        assertEquals( 1, lis.read() );
        final IllegalStateException exception = assertThrows( IllegalStateException.class, lis::read );
        assertEquals( "Exceeded", exception.getMessage() );
    }

    @Test
    void does_not_exceed()
        throws Exception
    {
        final ByteArrayInputStream original = new ByteArrayInputStream( new byte[]{1, 4} );

        final LimitingInputStream<IllegalStateException> lis =
            new LimitingInputStream<>( original, 2, () -> new IllegalStateException( "Exceeded" ) );

        final byte[] bytes = lis.readAllBytes();
        assertArrayEquals( new byte[]{1, 4}, bytes );
    }
}
