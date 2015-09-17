package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class AttachmentUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testId()
    {
        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testName()
    {
        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        assertNull( params.getName() );

        params.name( "" );
        assertNull( params.getName() );

        params.name( "myfile.pdf" );
        assertEquals( "myfile.pdf", params.getName() );
    }

    @Test
    public void testLabel()
    {
        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        assertNull( params.getLabel() );

        params.label( "" );
        assertNull( params.getLabel() );

        params.label( "media" );
        assertEquals( "media", params.getLabel() );
    }

    @Test
    public void testDownload()
    {
        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        assertFalse( params.isDownload() );

        params.download( "" );
        assertFalse( params.isDownload() );

        params.download( "true" );
        assertTrue( params.isDownload() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123456" );
        map.put( "_path", "/a/b" );
        map.put( "_name", "myfile.pdf" );
        map.put( "_label", "media" );
        map.put( "_download", "true" );
        map.put( "a", "1" );

        final AttachmentUrlParams params = configure( new AttachmentUrlParams() );
        params.setAsMap( map );

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
