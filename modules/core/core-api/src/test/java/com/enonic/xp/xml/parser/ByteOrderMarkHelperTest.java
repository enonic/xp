package com.enonic.xp.xml.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;

import static org.junit.Assert.*;

public class ByteOrderMarkHelperTest
{

    @Test
    public void testWithoutSkip()
        throws IOException
    {
        byte[] utf8_bom = "\uFEFFSome Text".getBytes( "UTF-8" );

        final CharSource source = CharSource.wrap( new String( utf8_bom, StandardCharsets.UTF_8 ) );
        final String result = CharStreams.toString( source.openStream() );

        assertEquals( "\uFEFFSome Text", result );
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