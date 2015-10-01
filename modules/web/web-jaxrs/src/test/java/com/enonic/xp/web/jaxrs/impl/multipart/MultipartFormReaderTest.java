package com.enonic.xp.web.jaxrs.impl.multipart;

import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.Assert.*;

public class MultipartFormReaderTest
{
    private MultipartService service;

    private MultipartFormReader reader;

    @Before
    public void setup()
    {
        this.service = Mockito.mock( MultipartService.class );
        this.reader = new MultipartFormReader();
        this.reader.setMultipartService( this.service );
    }

    @Test
    public void testIsReadable()
        throws Exception
    {
        assertFalse( isReadable( String.class, MediaType.TEXT_PLAIN_TYPE ) );
        assertFalse( isReadable( String.class, MediaType.MULTIPART_FORM_DATA_TYPE ) );
        assertTrue( isReadable( MultipartForm.class, MediaType.MULTIPART_FORM_DATA_TYPE ) );
    }

    @Test
    public void testReadFrom()
        throws Exception
    {
        final InputStream in = Mockito.mock( InputStream.class );

        final MultipartForm form = Mockito.mock( MultipartForm.class );
        Mockito.when( this.service.parse( in, MediaType.MULTIPART_FORM_DATA_TYPE.toString() ) ).thenReturn( form );

        final MultipartForm result = readFrom( in, MediaType.MULTIPART_FORM_DATA_TYPE );
        assertSame( form, result );
    }

    private boolean isReadable( final Class<?> type, final MediaType mediaType )
    {
        return this.reader.isReadable( type, type, new Annotation[0], mediaType );
    }

    private MultipartForm readFrom( final InputStream in, final MediaType type )
        throws Exception
    {
        return this.reader.readFrom( MultipartForm.class, MultipartForm.class, new Annotation[0], type, null, in );
    }
}
