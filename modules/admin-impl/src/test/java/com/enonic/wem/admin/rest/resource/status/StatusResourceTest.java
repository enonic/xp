package com.enonic.wem.admin.rest.resource.status;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Version;
import com.enonic.wem.api.server.ServerInfo;

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
        Mockito.when( this.serverInfo.getVersion() ).thenReturn( "5.0.0" );
        Mockito.when( this.serverInfo.getName() ).thenReturn( "production" );

        Version.get().setVersion( "5.0.0" );
        final String json = request().path( "/status" ).get().getAsString();
        assertJson( "status_ok.json", json );
    }
}
