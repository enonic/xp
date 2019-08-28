package com.enonic.xp.xml.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;

import static org.junit.jupiter.api.Assertions.*;

public class ByteOrderMarkHelperTest
{

    @Test
    public void testWithoutSkip()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFFSome Text".getBytes( "UTF-8" );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        try (Reader reader = source.openStream()) {
            final String result = CharStreams.toString(reader);
            assertEquals( "\uFEFFSome Text", result );
        }
    }

    @Test
    public void testSkippingBom()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFFSome Text".getBytes( "UTF-8" );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "Some Text", result );
    }

    @Test
    public void testShort()
        throws IOException
    {
        byte[] utf8_bom = "a".getBytes( "UTF-8" );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "a", result );
    }

    @Test
    public void testNoBom()
        throws IOException
    {
        byte[] utf8_bom = "Some Text".getBytes( "UTF-8" );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "Some Text", result );
    }

}
