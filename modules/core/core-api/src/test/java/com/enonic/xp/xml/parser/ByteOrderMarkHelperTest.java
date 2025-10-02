package com.enonic.xp.xml.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteOrderMarkHelperTest
{

    @Test
    void testWithoutSkip()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFFSome Text".getBytes( StandardCharsets.UTF_8 );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        try (Reader reader = source.openStream())
        {
            final String result = CharStreams.toString( reader );
            assertEquals( "\uFEFFSome Text", result );
        }
    }

    @Test
    void testSkippingBom()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFFSome Text".getBytes( StandardCharsets.UTF_8 );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "Some Text", result );
    }

    @Test
    void testShort()
        throws IOException
    {
        byte[] utf8_bom = "a".getBytes( StandardCharsets.UTF_8 );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "a", result );
    }

    @Test
    void testNoBom()
        throws IOException
    {
        byte[] utf8_bom = "Some Text".getBytes( StandardCharsets.UTF_8 );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "Some Text", result );
    }

    @Test
    void testEmpty()
        throws IOException
    {
        byte[] empty = {};

        final CharSource source = CharSource.wrap( new String( empty, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "", result );
    }

    @Test
    void testEmptyWithBom()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFF".getBytes( StandardCharsets.UTF_8 );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( ByteOrderMarkHelper.openStreamSkippingBOM( source ) );

        assertEquals( "", result );
    }

}
