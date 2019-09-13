package com.enonic.xp.portal.impl.serializer;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.web.impl.serializer.RequestBodyReader;

import static org.junit.jupiter.api.Assertions.*;

public class RequestBodyReaderTest
{
    private HttpServletRequest req;

    @BeforeEach
    public void setup()
    {
        this.req = Mockito.mock( HttpServletRequest.class );
    }

    @Test
    public void isText()
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

    private void setBytes( final String type, final byte[] bytes )
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( type );
    }

    @Test
    public void readNonText()
        throws Exception
    {
        setBytes( "application/octet-stream", new byte[0] );

        final Object result = RequestBodyReader.readBody( this.req );
        assertNull( result );
    }

    @Test
    public void readText()
        throws Exception
    {
        setText( "text/plain", "Hello World" );

        final Object result = RequestBodyReader.readBody( this.req );
        assertNotNull( result );
        assertEquals( "Hello World", result );
    }
}
