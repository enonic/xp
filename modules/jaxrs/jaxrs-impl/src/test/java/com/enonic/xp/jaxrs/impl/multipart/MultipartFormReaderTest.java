package com.enonic.xp.jaxrs.impl.multipart;

import java.io.InputStream;
import java.lang.annotation.Annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultipartFormReaderTest
{
    private MultipartService service;

    private MultipartFormReader reader;

    private HttpServletRequest request;

    @BeforeEach
    void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );

        this.service = Mockito.mock( MultipartService.class );

        this.reader = new MultipartFormReader( this.service );
        this.reader.setHttpServletRequest(  this.request);
    }

    @Test
    void testIsReadable()
    {
        assertFalse( isReadable( String.class, MediaType.TEXT_PLAIN_TYPE ) );
        assertFalse( isReadable( String.class, MediaType.MULTIPART_FORM_DATA_TYPE ) );
        assertTrue( isReadable( MultipartForm.class, MediaType.MULTIPART_FORM_DATA_TYPE ) );
    }

    @Test
    void testReadFrom()
        throws Exception
    {
        final InputStream in = Mockito.mock( InputStream.class );

        final MultipartForm form = Mockito.mock( MultipartForm.class );
        Mockito.when( this.service.parse( this.request ) ).thenReturn( form );

        final MultipartForm result = readFrom( MediaType.MULTIPART_FORM_DATA_TYPE );
        assertSame( form, result );
    }

    private boolean isReadable( final Class<?> type, final MediaType mediaType )
    {
        return this.reader.isReadable( type, type, new Annotation[0], mediaType );
    }

    private MultipartForm readFrom( final MediaType type )
        throws Exception
    {
        return this.reader.readFrom( MultipartForm.class, MultipartForm.class, new Annotation[0], type, null, null );
    }
}
