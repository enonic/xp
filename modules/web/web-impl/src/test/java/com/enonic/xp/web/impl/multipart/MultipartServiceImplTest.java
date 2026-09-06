package com.enonic.xp.web.impl.multipart;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultipartServiceImplTest
{
    private MultipartService service;

    private HttpServletRequest req;

    @BeforeEach
    void setup()
    {
        this.req = Mockito.mock( HttpServletRequest.class );
        this.service = new MultipartServiceImpl();
    }

    @Test
    void testParse_multipart()
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "text/plain" );

        final MultipartForm form = this.service.parse( this.req );
        assertNotNull( form );
        assertEquals( true, form.isEmpty() );
        assertEquals( 0, form.getSize() );
    }

    @Test
    void testParse_noMultipart()
        throws Exception
    {
        final Part part = Mockito.mock( Part.class );
        Mockito.when( part.getName() ).thenReturn( "part" );
        Mockito.when( this.req.getParts() ).thenReturn( List.of( part ) );
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data; boundary=----xp" );

        final MultipartForm form = this.service.parse( this.req );
        assertNotNull( form );
        assertEquals( false, form.isEmpty() );
        assertEquals( 1, form.getSize() );
    }

    @Test
    void testParse_malformedContentType()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart" );

        final MultipartForm form = this.service.parse( this.req );
        assertEquals( true, form.isEmpty() );
        Mockito.verify( this.req, Mockito.never() ).getParts();
    }

    @Test
    void testParse_missingBoundary()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data" );

        final WebException e = assertThrows( WebException.class, () -> this.service.parse( this.req ) );
        assertEquals( HttpStatus.BAD_REQUEST, e.getStatus() );
        Mockito.verify( this.req, Mockito.never() ).getParts();
    }

    @Test
    void testParse_limitExceeded()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data; boundary=x" );
        Mockito.when( this.req.getParts() ).thenThrow( new IllegalStateException( "max file size exceeded: 1024" ) );

        final WebException e = assertThrows( WebException.class, () -> this.service.parse( this.req ) );
        assertEquals( HttpStatus.PAYLOAD_TOO_LARGE, e.getStatus() );
    }

    @Test
    void testParse_malformedBody()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data; boundary=x" );
        Mockito.when( this.req.getParts() ).thenThrow( new IOException( "unexpected end of stream" ) );

        final WebException e = assertThrows( WebException.class, () -> this.service.parse( this.req ) );
        assertEquals( HttpStatus.BAD_REQUEST, e.getStatus() );
    }

    @Test
    void testParse_servletException()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data; boundary=x" );
        Mockito.when( this.req.getParts() ).thenThrow( new ServletException( "boom" ) );

        assertThrows( ServletException.class, () -> this.service.parse( this.req ) );
    }
}
