package com.enonic.xp.server.internal.deploy;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationService;

public class AutoDeployerTest
{
    private com.enonic.xp.server.internal.deploy.AutoDeployer deployer;

    private ApplicationService service;

    private Map<String, String> config;

    @Before
    public void setup()
    {
        this.deployer = new AutoDeployer();
        this.service = Mockito.mock( ApplicationService.class );
        this.deployer.setApplicationService( this.service );
        this.config = Maps.newHashMap();
    }

    @Test
    public void test_no_urls()
    {
        this.deployer.activate( this.config );
        Mockito.verify( this.service, Mockito.times( 0 ) ).installGlobalApplication( Mockito.any() );
    }

    @Test
    public void test_urls()
    {
        this.config.put( "deploy.1", "http://some.server.com/a/b" );
        this.config.put( "deploy.2", "http://some.server.com/a/b/c" );
        this.config.put( "deploy.3", "my://faulty/url" );

        this.deployer.activate( this.config );
        Mockito.verify( this.service, Mockito.times( 2 ) ).installGlobalApplication( Mockito.any() );
    }
}
