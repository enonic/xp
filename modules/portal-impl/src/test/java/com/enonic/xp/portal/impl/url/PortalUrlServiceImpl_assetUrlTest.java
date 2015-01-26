package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_assetUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_path", "css/my.css" );
        map.put( "a", "1" );

        final String url = this.service.assetUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/asset/mymodule/css/my.css?a=1", url );
    }

    // @Test
    // Not working yet. Will do later on.
    public void createUrl_withModule()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_path", "css/my.css" );
        map.put( "_module", "othermodule" );

        final String url = this.service.assetUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/asset/othermodule/css/my.css", url );
    }
}
