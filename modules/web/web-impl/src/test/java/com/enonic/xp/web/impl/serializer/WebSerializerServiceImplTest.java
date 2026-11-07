package com.enonic.xp.web.impl.serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.serializer.WebSerializerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSerializerServiceImplTest
{
    @Test
    void readBodyUsesConfiguredLimit()
        throws Exception
    {
        final WebSerializerConfig config = mock( WebSerializerConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.http_maxRequestBodySize() ).thenReturn( 5L );

        final WebSerializerServiceImpl service = new WebSerializerServiceImpl();
        service.activate( config );

        assertEquals( "Hello", service.readBody( textRequest( "Hello" ) ) );

        final WebException e = assertThrows( WebException.class, () -> service.readBody( textRequest( "Hello World" ) ) );
        assertEquals( HttpStatus.PAYLOAD_TOO_LARGE, e.getStatus() );
    }

    @Test
    void readBodyDefaultReturnsNull()
        throws Exception
    {
        final WebSerializerService service = new WebSerializerService()
        {
            @Override
            public WebRequest request( final HttpServletRequest httpRequest )
            {
                return null;
            }

            @Override
            public void response( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse httpResponse )
            {
            }
        };

        assertNull( service.readBody( textRequest( "Hello" ) ) );
    }

    private static HttpServletRequest textRequest( final String text )
        throws IOException
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getContentType() ).thenReturn( "text/plain" );
        when( req.getContentLengthLong() ).thenReturn( -1L );
        when( req.getReader() ).thenReturn( new BufferedReader( new StringReader( text ) ) );
        return req;
    }
}
