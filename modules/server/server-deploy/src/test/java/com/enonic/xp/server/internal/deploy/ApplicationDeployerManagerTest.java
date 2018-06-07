package com.enonic.xp.server.internal.deploy;

import java.net.URL;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationService;

public class ApplicationDeployerManagerTest
{
    ApplicationDeployerManager applicationDeployerManager;

    private StoredApplicationsDeployer storedApplicationsDeployer;

    private AutoDeployer autoDeployer;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    @Before
    public void setup()
        throws Exception
    {
        applicationDeployerManager = new ApplicationDeployerManager();

        storedApplicationsDeployer = new StoredApplicationsDeployer();
        applicationDeployerManager.setStoredApplicationsDeployer( storedApplicationsDeployer );

        autoDeployer = new AutoDeployer();
        final HashMap<String, String> autoDeployerConfig = Maps.newHashMap();
        autoDeployerConfig.put( "deploy.1", "http://localhost/url1" );
        autoDeployer.activate( autoDeployerConfig );
        applicationDeployerManager.setAutoDeployer( autoDeployer );

        deployDirectoryWatcher = new DeployDirectoryWatcher();
        final DeployConfig deployConfig = Mockito.mock( DeployConfig.class );
        final TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        System.setProperty( "xp.home", temporaryFolder.getRoot().getAbsolutePath() );
        deployDirectoryWatcher.activate( deployConfig );
        applicationDeployerManager.setDeployDirectoryWatcher( deployDirectoryWatcher );
    }

    @Test
    public void test_activate()
        throws Exception
    {
        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        storedApplicationsDeployer.setApplicationService( applicationService );
        autoDeployer.setApplicationService( applicationService );
        deployDirectoryWatcher.setApplicationService( applicationService );

        applicationDeployerManager.activate();
        Mockito.verify( applicationService ).installAllStoredApplications();
        Mockito.verify( applicationService ).installGlobalApplication( new URL( "http://localhost/url1" ) );
    }
}
