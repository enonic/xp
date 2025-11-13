package com.enonic.xp.server.internal.deploy;

import java.nio.file.Path;
import java.util.Dictionary;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.home.HomeDirSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApplicationDeployerManagerTest
{
    @TempDir
    public Path temporaryFolder;

    ApplicationDeployerManager applicationDeployerManager;

    private StoredApplicationsDeployer storedApplicationsDeployer;

    private AutoDeployer autoDeployer;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    ApplicationService applicationService;

    @BeforeEach
    void setup()
    {
        applicationService = mock( ApplicationService.class );

        applicationDeployerManager = new ApplicationDeployerManager();

        storedApplicationsDeployer = new StoredApplicationsDeployer();
        storedApplicationsDeployer.setApplicationService( applicationService );
        applicationDeployerManager.setStoredApplicationsDeployer( storedApplicationsDeployer );

        autoDeployer = new AutoDeployer();
        final HashMap<String, String> autoDeployerConfig = new HashMap<>();
        autoDeployerConfig.put( "deploy.1", "http://localhost/url1" );
        autoDeployer.activate( autoDeployerConfig );
        applicationDeployerManager.setAutoDeployer( autoDeployer );
        autoDeployer.setApplicationService( applicationService );

        HomeDirSupport.set(  temporaryFolder );
        final DeployConfig deployConfig = mock( DeployConfig.class );
        deployDirectoryWatcher = new DeployDirectoryWatcher( applicationService, deployConfig);


        applicationDeployerManager.setDeployDirectoryWatcher( deployDirectoryWatcher );
    }

    @Test
    void test_activate()
        throws Exception
    {
        final BundleContext bundleContext = mock( BundleContext.class );
        applicationDeployerManager.activate( bundleContext );
        verify( applicationService ).installAllStoredApplications();

        var captor = ArgumentCaptor.forClass( Dictionary.class );
        verify( bundleContext ).registerService( same( Condition.class ), eq(Condition.INSTANCE), captor.capture());
        assertEquals( "com.enonic.xp.server.deploy.ready", captor.getValue().get( Condition.CONDITION_ID ) );
    }
}
