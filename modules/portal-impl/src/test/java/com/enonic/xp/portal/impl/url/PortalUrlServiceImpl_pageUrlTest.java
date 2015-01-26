package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_pageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_path", "/a/b" );

        final String url = this.service.pageUrl( this.context, map );
        assertEquals( "/portal/stage/a/b", url );
    }
}
