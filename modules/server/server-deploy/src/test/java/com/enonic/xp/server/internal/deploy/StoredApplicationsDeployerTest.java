package com.enonic.xp.server.internal.deploy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;

import static org.mockito.ArgumentMatchers.any;

class StoredApplicationsDeployerTest
{
    private ApplicationService applicationService;

    private StoredApplicationsDeployer deployer;

    @BeforeEach
    void setup()
    {
        this.applicationService = Mockito.mock( ApplicationService.class );
        this.deployer = new StoredApplicationsDeployer( this.applicationService );
    }

    @Test
    void deploy()
    {
        this.deployer.deploy();
        Mockito.verify( this.applicationService, Mockito.times( 1 ) ).installAllStoredApplications( any() );
    }
}
