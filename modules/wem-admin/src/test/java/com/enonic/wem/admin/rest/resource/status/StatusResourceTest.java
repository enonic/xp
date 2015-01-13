package com.enonic.wem.admin.rest.resource.status;

import org.junit.Test;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Version;
import com.enonic.wem.api.context.ContextAccessor;

public class StatusResourceTest
    extends AbstractResourceTest
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
        Version.get().setVersion( "5.0.0" );
        final String json = request().path( "/status" ).get().getAsString();
        assertJson( "status_ok.json", json );
    }
}
