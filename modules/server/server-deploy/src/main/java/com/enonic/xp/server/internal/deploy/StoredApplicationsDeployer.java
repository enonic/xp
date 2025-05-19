package com.enonic.xp.server.internal.deploy;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationService;

@Component(service = StoredApplicationsDeployer.class)
public final class StoredApplicationsDeployer
{
    private final ApplicationService applicationService;

    @Activate
    public StoredApplicationsDeployer( @Reference final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    public void deploy()
    {
        DeployHelper.runAsAdmin( () -> applicationService.installAllStoredApplications( ApplicationInstallationParams.create().
            build() ) );
    }
}
