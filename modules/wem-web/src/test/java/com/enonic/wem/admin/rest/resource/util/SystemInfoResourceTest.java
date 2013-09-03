package com.enonic.wem.admin.rest.resource.util;

import org.junit.Test;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;

public class SystemInfoResourceTest
    extends AbstractResourceTest2
{
    @Override
    protected Object getResourceInstance()
    {
        return new SystemInfoResource();
    }

    @Test
    public void testList()
        throws Exception
    {
        final String json = resource().path( "util/system_info" ).get( String.class );
        assertJson( "system_info.json", json );
    }
}
