package com.enonic.xp.portal.impl.serializer;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.impl.serializer.RequestBodyReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestBodyReaderTest
{
    private static final long LIMIT = 1024;

    private HttpServletRequest req;

    @BeforeEach
    void setup()
    {
        this.req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( this.req.getContentLengthLong() ).thenReturn( -1L );
    }

    @Test
    void isText()
    {
        assertTrue( RequestBodyReader.isText( MediaType.parse( "text/plain" ) ) );
        assertTrue( RequestBodyReader.isText( MediaType.parse( "text/xml;charset=UTF-8" ) ) );
        assertTrue( RequestBodyReader.isText( MediaType.parse( "application/json" ) ) );
        assertTrue( RequestBodyReader.isText( MediaType.parse( "application/json;charset=UTF-8" ) ) );

        assertFalse( RequestBodyReader.isText( MediaType.parse( "application/x-www-form-urlencoded" ) ) );
    }

    private void setText( final String type, final String text )
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( type );
        Mockito.when( this.req.getReader() ).thenReturn( new BufferedReader( new StringReader( text ) ) );
    }

    @Test
    void readNonText()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "application/octet-stream" );

        final Object result = RequestBodyReader.readBody( this.req, LIMIT );
        assertNull( result );
    }

    @Test
    void readNonText_ignoresLimit()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "application/octet-stream" );
        Mockito.when( this.req.getContentLengthLong() ).thenReturn( LIMIT * 100 );

        assertNull( RequestBodyReader.readBody( this.req, LIMIT ) );
        Mockito.verify( this.req, Mockito.never() ).getReader();
    }

    @Test
    void readText()
        throws Exception
    {
        setText( "text/plain", "Hello World" );

        final Object result = RequestBodyReader.readBody( this.req, LIMIT );
        assertNotNull( result );
        assertEquals( "Hello World", result );
    }

    @Test
    void readText_exactlyAtLimit()
        throws Exception
    {
        setText( "text/plain", "Hello World" );

        assertEquals( "Hello World", RequestBodyReader.readBody( this.req, "Hello World".length() ) );
    }

    @Test
    void readText_declaredLengthOverLimit_rejectedWithoutReading()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "application/json" );
        Mockito.when( this.req.getContentLengthLong() ).thenReturn( LIMIT + 1 );

        final WebException e = assertThrows( WebException.class, () -> RequestBodyReader.readBody( this.req, LIMIT ) );
        assertEquals( HttpStatus.PAYLOAD_TOO_LARGE, e.getStatus() );
        Mockito.verify( this.req, Mockito.never() ).getReader();
    }

    @Test
    void readText_streamedNonAscii_countsEncodedBytes()
        throws Exception
    {
        final String fiveTwoByteChars = "\u00e9\u00e9\u00e9\u00e9\u00e9";

        setText( "text/plain", fiveTwoByteChars );
        assertEquals( fiveTwoByteChars, RequestBodyReader.readBody( this.req, 10 ) );

        setText( "text/plain", fiveTwoByteChars );
        final WebException e = assertThrows( WebException.class, () -> RequestBodyReader.readBody( this.req, 9 ) );
        assertEquals( HttpStatus.PAYLOAD_TOO_LARGE, e.getStatus() );
    }

    @Test
    void readText_streamedOverLimit_rejected()
        throws Exception
    {
        setText( "text/plain", "Hello World" );

        final WebException e = assertThrows( WebException.class, () -> RequestBodyReader.readBody( this.req, "Hello World".length() - 1 ) );
        assertEquals( HttpStatus.PAYLOAD_TOO_LARGE, e.getStatus() );
    }
}
