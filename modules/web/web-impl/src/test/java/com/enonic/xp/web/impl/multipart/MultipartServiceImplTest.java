package com.enonic.xp.web.impl.multipart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

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
        Mockito.when( this.req.getParts() ).thenReturn( Lists.newArrayList( part ) );
        Mockito.when( this.req.getContentType() ).thenReturn( "multipart/form-data" );

        final MultipartForm form = this.service.parse( this.req );
        assertNotNull( form );
        assertEquals( false, form.isEmpty() );
        assertEquals( 1, form.getSize() );
    }
}
