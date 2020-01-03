package com.enonic.xp.web.impl.multipart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MultipartServiceImplTest
{
    private MultipartService service;

    private HttpServletRequest req;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.req = Mockito.mock( HttpServletRequest.class );
        this.service = new MultipartServiceImpl();
    }

    @Test
    public void testParse_multipart()
        throws Exception
    {
        Mockito.when( this.req.getContentType() ).thenReturn( "text/plain" );

        final MultipartForm form = this.service.parse( this.req );
        assertNotNull( form );
        assertEquals( true, form.isEmpty() );
        assertEquals( 0, form.getSize() );
    }

    @Test
    public void testParse_noMultipart()
        throws Exception
    {
        final Part part = Mockito.mock( Part.class );
        Mockito.when( part.getName() ).thenReturn( "part" );
        Mockito.when( this.req.getParts() ).thenReturn( List.of( part ) );
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data" );

        final MultipartForm form = this.service.parse( this.req );
        assertNotNull( form );
        assertEquals( false, form.isEmpty() );
        assertEquals( 1, form.getSize() );
    }
}
