package com.enonic.wem.admin.rest.resource.util;

import org.junit.Test;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Version;

public class SystemInfoResourceTest
    extends AbstractResourceTest
{
    @Override
    protected Object getResourceInstance()
    {
        Version.get().setVersion( "5.0.0" );
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
