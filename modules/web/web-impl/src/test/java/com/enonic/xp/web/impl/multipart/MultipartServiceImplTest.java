package com.enonic.xp.web.impl.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.Assert.*;

public class MultipartServiceImplTest
{
    private MultipartService service;

    private HttpServletRequest req;

    @Before
    public void setup()
        throws Exception
    {
        this.service = new MultipartServiceImpl();
        this.req = Mockito.mock( HttpServletRequest.class );

        final MimeBodyPart part = new MimeBodyPart();
        part.setFileName( "test.txt" );
        part.setText( "hello", "UTF-8" );

        final MimeMultipart multipart = new MimeMultipart( "form-data" );
        multipart.addBodyPart( part );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        multipart.writeTo( out );

        System.out.println( multipart.getContentType() );

        Mockito.when( this.req.getContentType() ).thenReturn( multipart.getContentType() );
        final InputStream in = new ByteArrayInputStream( out.toByteArray() );

        Mockito.when( this.req.getInputStream() ).thenReturn( new ServletInputStream()
        {
            @Override
            public int read()
                throws IOException
            {
                return in.read();
            }
        } );
    }

    @Test
    public void testParseStream()
        throws Exception
    {
        final MultipartForm form = this.service.parse( this.req.getInputStream(), this.req.getContentType() );
        assertNotNull( form );
    }

    @Test
    public void testParseRequest()
        throws Exception
    {
        final MultipartForm form = this.service.parse( req );
        assertNotNull( form );
    }
}
