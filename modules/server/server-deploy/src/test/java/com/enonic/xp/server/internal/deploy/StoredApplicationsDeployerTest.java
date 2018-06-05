package com.enonic.xp.server.internal.deploy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;

public class StoredApplicationsDeployerTest
{
    private ApplicationService service;

    private StoredApplicationsDeployer deployer;

    @Before
    public void setup()
    {
        this.deployer = new StoredApplicationsDeployer();
        this.service = Mockito.mock( ApplicationService.class );
        this.deployer.setApplicationService( this.service );
    }

    @Test
    public void activate()
    {
        this.deployer.activate();
        Mockito.verify( this.service, Mockito.times( 1 ) ).installAllStoredApplications();
    }
}