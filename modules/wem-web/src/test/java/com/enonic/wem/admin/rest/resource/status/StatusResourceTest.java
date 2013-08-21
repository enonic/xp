package com.enonic.wem.admin.rest.resource.status;

import org.junit.Test;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;

public class StatusResourceTest
    extends AbstractResourceTest2
{
    @Override
    protected Object getResourceInstance()
    {
        return new StatusResource();
    }

    @Test
    public void testGetStatus()
        throws Exception
    {
        final String json = resource().path( "/status" ).get( String.class );
        assertJson( "status_ok.json", json );
    }
}
