package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttachmentUrlParamsTest
{
    @Test
    public void testId()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testName()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        assertNull( params.getName() );

        params.name( "" );
        assertNull( params.getName() );

        params.name( "myfile.pdf" );
        assertEquals( "myfile.pdf", params.getName() );
    }

    @Test
    public void testLabel()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        assertNull( params.getLabel() );

        params.label( "" );
        assertNull( params.getLabel() );

        params.label( "media" );
        assertEquals( "media", params.getLabel() );
    }

    @Test
    public void testDownload()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        assertFalse( params.isDownload() );

        params.download( "" );
        assertFalse( params.isDownload() );

        params.download( "true" );
        assertTrue( params.isDownload() );
    }

    @Test
    public void testSetAsMap()
    {
        final AttachmentUrlParams params = new AttachmentUrlParams();
        params.id( "123456" );
        params.path( "/a/b" );
        params.name( "myfile.pdf" );
        params.label( "media" );
        params.download( true );
        params.param( "a", "1" );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "myfile.pdf", params.getName() );
        assertEquals( "media", params.getLabel() );
        assertEquals( true, params.isDownload() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "AttachmentUrlParams{type=server, params={a=[1]}, id=123456, path=/a/b, name=myfile.pdf, label=media, download=true}",
                      params.toString() );
    }
}
