package com.enonic.xp.server.internal.deploy;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class ApplicationDeploymentServiceImpl
{
    private StoredApplicationsDeployer storedApplicationsDeployer;

    private AutoDeployer autoDeployer;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    @Activate
    public void activate( final DeployConfig config )
        throws Exception
    {
        storedApplicationsDeployer.deploy();
        autoDeployer.deploy();
        deployDirectoryWatcher.deploy();
    }

    @Reference
    public void setStoredApplicationsDeployer( final StoredApplicationsDeployer storedApplicationsDeployer )
    {
        this.storedApplicationsDeployer = storedApplicationsDeployer;
    }

    @Reference
    public void setAutoDeployer( final AutoDeployer autoDeployer )
    {
        this.autoDeployer = autoDeployer;
    }

    @Reference
    public void setDeployDirectoryWatcher( final DeployDirectoryWatcher deployDirectoryWatcher )
    {
        this.deployDirectoryWatcher = deployDirectoryWatcher;
    }
}
