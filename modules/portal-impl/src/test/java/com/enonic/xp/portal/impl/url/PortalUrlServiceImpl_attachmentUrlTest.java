package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_attachmentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_with_nothing_specified()
    {
        final Multimap<String, String> map = HashMultimap.create();

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment", url );
    }

    @Test
    public void createUrl_with_name()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_name", "mycv.pdf" );

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment/mycv.pdf", url );
    }

    @Test
    public void createUrl_with_label()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_label", "source" );

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment/source", url );
    }

    @Test
    public void createUrl_with_mediaId_and_nothing_specified()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123abc" );

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment/id/123abc", url );
    }

    @Test
    public void createUrl_with_mediaId_and_name()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123abc" );
        map.put( "_name", "mycv.pdf" );

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment/id/123abc/mycv.pdf", url );
    }

    @Test
    public void createUrl_with_mediaId_and_label()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123abc" );
        map.put( "_label", "source" );

        final String url = this.service.attachmentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/attachment/id/123abc/source", url );
    }
}
