package com.enonic.xp.server.internal.deploy;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationService;

@Component(service = StoredApplicationsDeployer.class)
public final class StoredApplicationsDeployer
{
    private ApplicationService applicationService;

    public void deploy()
    {
        DeployHelper.runAsAdmin( () -> applicationService.installAllStoredApplications() );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
