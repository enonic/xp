package com.enonic.xp.admin.impl.rest.resource.status;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.server.BuildInfo;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public class StatusResourceTest
    extends AdminResourceTestSupport
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
        VersionInfo.set( "1.1.1-SNAPSHOT" );

        final BuildInfo buildInfo = Mockito.mock( BuildInfo.class );
        Mockito.when( buildInfo.getBranch() ).thenReturn( "master" );
        Mockito.when( buildInfo.getHash() ).thenReturn( "123456" );
        Mockito.when( buildInfo.getShortHash() ).thenReturn( "123" );
        Mockito.when( buildInfo.getTimestamp() ).thenReturn( "2015-11-11T22:11:00" );

        Mockito.when( this.serverInfo.getName() ).thenReturn( "production" );
        Mockito.when( this.serverInfo.getBuildInfo() ).thenReturn( buildInfo );

        final String json = request().path( "/status" ).get().getAsString();
        assertJson( "status_ok.json", json );
    }
}
