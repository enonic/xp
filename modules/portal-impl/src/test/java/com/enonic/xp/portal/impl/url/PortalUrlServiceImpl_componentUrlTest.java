package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_componentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_component", "mycomp" );
        map.put( "a", "1" );

        final String url = this.service.componentUrl( this.context, map );
        assertEquals( "/portal/stage/context/path/_/component/mycomp?a=1", url );
    }
}
