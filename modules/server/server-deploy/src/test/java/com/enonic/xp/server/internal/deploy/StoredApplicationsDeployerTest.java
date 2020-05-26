package com.enonic.xp.server.internal.deploy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;

import static org.mockito.ArgumentMatchers.any;

public class StoredApplicationsDeployerTest
{
    private ApplicationService applicationService;

    private StoredApplicationsDeployer deployer;

    @BeforeEach
    public void setup()
    {
        this.deployer = new StoredApplicationsDeployer();
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.deployer.setApplicationService( this.applicationService );
    }

    @Test
    public void deploy()
    {
        this.deployer.deploy();
        Mockito.verify( this.applicationService, Mockito.times( 1 ) ).installAllStoredApplications( any() );
    }
}
