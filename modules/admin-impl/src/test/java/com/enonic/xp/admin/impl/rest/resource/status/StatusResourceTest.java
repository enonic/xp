package com.enonic.xp.admin.impl.rest.resource.status;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.server.ServerInfo;

public class StatusResourceTest
    extends AbstractResourceTest
{
    private ServerInfo serverInfo;

    @Override
    protected Object getResourceInstance()
    {
        this.serverInfo = Mockito.mock( ServerInfo.class );
        final StatusResource resource = new StatusResource();
        resource.setServerInfo( this.serverInfo );
        return resource;
    }

    @Test
    public void testGetStatus()
        throws Exception
    {
        System.setProperty( "xp.version", "5.0.0" );
        System.setProperty( "xp.build.hash", "123" );
        System.setProperty( "xp.build.number", "789" );

        Mockito.when( this.serverInfo.getName() ).thenReturn( "production" );

        final String json = request().path( "/status" ).get().getAsString();
        assertJson( "status_ok.json", json );
    }
}
