package com.enonic.wem.web.rest2.resource.image;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ImageResourceTest
{
    private ImageResource resource;

    @Before
    public void setUp()
        throws Exception
    {
        this.resource = new ImageResource();
    }

    @Test
    public void testGetImage()
        throws Exception
    {
        assertImage( this.resource.getImage( "admin", 10 ), 10 );
        assertImage( this.resource.getImage( "anonymous", 20 ), 20 );
        assertImage( this.resource.getImage( "group", 30 ), 30 );
        assertImage( this.resource.getImage( "role", 40 ), 40 );
        assertImage( this.resource.getImage( "user", 50 ), 50 );
    }

    @Test
    public void testGetImage_notFound()
        throws Exception
    {
        assertNull( this.resource.getImage( "other", 10 ) );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }
}
