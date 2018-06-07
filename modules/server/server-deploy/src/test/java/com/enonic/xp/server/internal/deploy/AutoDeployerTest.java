package com.enonic.xp.server.internal.deploy;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationService;

public class AutoDeployerTest
{
    private com.enonic.xp.server.internal.deploy.AutoDeployer deployer;

    private ApplicationService service;

    @Before
    public void setup()
    {
        this.deployer = new AutoDeployer();
        this.service = Mockito.mock( ApplicationService.class );
        this.deployer.setApplicationService( this.service );
    }

    @Test
    public void test_no_urls()
    {
        this.deployer.activate( Maps.newHashMap() );
        this.deployer.deploy();
        Mockito.verify( this.service, Mockito.times( 0 ) ).installGlobalApplication( Mockito.any() );
    }

    @Test
    public void test_urls()
    {
        final HashMap<String, String> config = Maps.newHashMap();
        config.put( "deploy.1", "http://some.server.com/a/b" );
        config.put( "deploy.2", "http://some.server.com/a/b/c" );
        config.put( "deploy.3", "my://faulty/url" );
        this.deployer.activate( config );

        this.deployer.deploy();
        Mockito.verify( this.service, Mockito.times( 2 ) ).installGlobalApplication( Mockito.any() );
    }
}
