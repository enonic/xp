package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_imageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrlWithoutFilters()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_name", "myimage.png" );

        final String url = this.service.imageUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/image/myimage.png", url );
    }

    @Test
    public void createUrlWithFilters()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_name", "myimage.png" );
        map.put( "_filter", "scalemax(120);rounded(40);block(3,3);sepia()" );
        map.put( "_background", "00ff00" );
        map.put( "_quality", "33" );

        final String url = this.service.imageUrl( this.context, map );
        assertEquals(
            "/portal/stage/context/path/_/image/myimage.png?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
            url );
    }

    @Test
    public void createUrlByIdWithoutFilters()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "abc" );

        final String url = this.service.imageUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/image/id/abc", url );
    }
}
