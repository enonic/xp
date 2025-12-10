package com.enonic.xp.core.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.felix.framework.Felix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.ops4j.pax.tinybundles.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.app.AppConfig;
import com.enonic.xp.core.impl.app.AppFilterServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationListenerHub;
import com.enonic.xp.core.impl.app.ApplicationRegistryImpl;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.VirtualAppService;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class ApplicationServiceTest
    extends AbstractNodeTest
{
    @TempDir
    public Path felixTempFolder;

    private ApplicationService applicationService;

    private Felix felix;

    @BeforeEach
    void setUp()
        throws Exception
    {
        Path cacheDir = Files.createDirectory( this.felixTempFolder.resolve( "cache" ) ).toAbsolutePath();

        this.felix = createFelixInstance( cacheDir );
        this.felix.start();

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        ApplicationRepoServiceImpl repoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();

        BundleContext bundleContext = felix.getBundleContext();

        ApplicationFactoryServiceImpl applicationFactoryService = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        ApplicationAuditLogSupportImpl applicationAuditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        applicationAuditLogSupport.activate( appConfig );

        this.applicationService = new ApplicationServiceImpl( new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(),
                                                                                           applicationFactoryService ), repoService,
                                                              new EventPublisherImpl( Executors.newSingleThreadExecutor() ),
                                                              new AppFilterServiceImpl( appConfig ),
                                                              new VirtualAppService(  nodeService ),
                                                              applicationAuditLogSupport );
    }

    @AfterEach
    public final void destroy()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop( 10_000 );
    }

    @Test
    void testUpdate()
    {
        String applicationName = "appName";
        adminContext().callWith( () -> {
            Application application = applicationService.installGlobalApplication( createByteSource( "7.8.0" ), applicationName );
            assertEquals( "7.8.0", application.getVersion().toString() );

            systemRepoContext().callWith( () -> {
                Node applicationNode = nodeService.getByPath(
                    NodePath.create( NodePath.ROOT ).addElement( "applications" ).addElement( applicationName ).build() );
                assertNotNull( applicationNode );
                assertEquals( "7.8.0", applicationNode.data().getString( "version" ) );
                return null;
            } );

            application = applicationService.installGlobalApplication( createByteSource( "7.8.1" ), applicationName );
            assertEquals( "7.8.1", application.getVersion().toString() );

            systemRepoContext().callWith( () -> {
                Node applicationNode = nodeService.getByPath(
                    NodePath.create( NodePath.ROOT ).addElement( "applications" ).addElement( applicationName ).build() );
                assertNotNull( applicationNode );
                assertEquals( "7.8.1", applicationNode.data().getString( "version" ) );
                return null;
            } );
            return null;
        } );
    }

    private Felix createFelixInstance( final Path cacheDir )
    {
        Map<String, Object> config = new HashMap<>();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.toString() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );

        return new Felix( config );
    }

    private Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.ANONYMOUS ).build() )
            .build();
    }

    private Context systemRepoContext()
    {
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( ContextAccessor.current().getAuthInfo() )
            .build();
    }

    private ByteSource createByteSource( String appVersion )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( TinyBundles.bundle()
                                                             .setHeader( Constants.BUNDLE_SYMBOLICNAME, "appName" )
                                                             .setHeader( Constants.BUNDLE_VERSION, appVersion )
                                                             .setHeader( "X-Bundle-Type", "application" )
                                                             .addResource( "site/site.xml", getClass().getResource( "/myapp/site/site.xml" ) )
                                                             .build() ) );
    }

}
