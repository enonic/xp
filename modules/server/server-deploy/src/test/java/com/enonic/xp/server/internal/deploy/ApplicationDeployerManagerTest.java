package com.enonic.xp.server.internal.deploy;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationService;

public class ApplicationDeployerManagerTest
{
    @TempDir
    public Path temporaryFolder;

    ApplicationDeployerManager applicationDeployerManager;

    private StoredApplicationsDeployer storedApplicationsDeployer;

    private AutoDeployer autoDeployer;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    @BeforeEach
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
        System.setProperty( "xp.home", temporaryFolder.toFile().getAbsolutePath() );
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
