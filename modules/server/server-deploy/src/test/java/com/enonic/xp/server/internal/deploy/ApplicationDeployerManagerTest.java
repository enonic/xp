package com.enonic.xp.server.internal.deploy;

import java.nio.file.Path;
import java.util.Dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.ApplicationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApplicationDeployerManagerTest
{
    @TempDir
    public Path temporaryFolder;

    ApplicationDeployerManager applicationDeployerManager;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    ApplicationService applicationService;

    @BeforeEach
    void setup()
        throws Exception
    {
        applicationService = mock( ApplicationService.class );
        final StoredApplicationsDeployer storedApplicationsDeployer = new StoredApplicationsDeployer( applicationService );

        deployDirectoryWatcher = new DeployDirectoryWatcher();
        final DeployConfig deployConfig = mock( DeployConfig.class );
        System.setProperty( "xp.home", temporaryFolder.toFile().getAbsolutePath() );
        deployDirectoryWatcher.activate( deployConfig );
        applicationDeployerManager = new ApplicationDeployerManager( storedApplicationsDeployer, deployDirectoryWatcher );
    }

    @Test
    void test_activate()
        throws Exception
    {
        deployDirectoryWatcher.setApplicationService( applicationService );

        final BundleContext bundleContext = mock( BundleContext.class );
        applicationDeployerManager.activate( bundleContext );
        verify( applicationService ).installAllStoredApplications( any() );

        var captor = ArgumentCaptor.forClass( Dictionary.class );
        verify( bundleContext ).registerService( same( Condition.class ), eq(Condition.INSTANCE), captor.capture());
        assertEquals( "com.enonic.xp.server.deploy.ready", captor.getValue().get( Condition.CONDITION_ID ) );
    }
}
