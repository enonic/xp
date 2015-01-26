package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_serviceUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_service", "myservice" );
        map.put( "a", "1" );

        final String url = this.service.serviceUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/service/mymodule/myservice?a=1", url );
    }

    // @Test
    // Not working yet. Will do later on.
    public void createUrl_withModule()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_service", "myservice" );
        map.put( "_module", "othermodule" );

        final String url = this.service.serviceUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/service/othermodule/myservice", url );
    }
}
