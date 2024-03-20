package com.enonic.xp.admin.impl.rest.resource.status;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public class StatusResourceTest
    extends AdminResourceTestSupport
{
    private Properties serverInfo;

    @Override
    protected Object getResourceInstance()
    {
        this.serverInfo = new Properties();

        final StatusResource resource = new StatusResource();
        resource.info = new ServerInfo( this.serverInfo );
        return resource;
    }

    @Test
    public void testGetStatus()
        throws Exception
    {
        initServerInfo();

        final String json = request().path( "status" ).get().getAsString();
        assertJson( "status_ok.json", json );
    }

    @Test
    public void testGetStatus_readonly()
        throws Exception
    {
        initServerInfo();

        final String json = request().path( "status" ).get().getAsString();
        assertJson( "status_readonly.json", json );
    }

    private void initServerInfo()
    {
        VersionInfo.set( "1.1.1-SNAPSHOT" );

        this.serverInfo.put( "xp.build.branch", "master" );
        this.serverInfo.put( "xp.build.hash", "123456" );
        this.serverInfo.put( "xp.build.shortHash", "123" );
        this.serverInfo.put( "xp.build.timestamp", "2015-11-11T22:11:00" );
        this.serverInfo.put( "xp.name", "production" );
    }
}
