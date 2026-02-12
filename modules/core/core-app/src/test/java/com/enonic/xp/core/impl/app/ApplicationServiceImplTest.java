package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.VersionRange;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationMode;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationServiceImplTest
    extends BundleBasedTest
{
    private final ApplicationRepoServiceImpl repoService = mock( ApplicationRepoServiceImpl.class );

    private ApplicationServiceImpl service;

    private ApplicationRegistryImpl applicationRegistry;

    private EventPublisher eventPublisher;

    private AppFilterService appFilterService;

    private NodeService nodeService;

    private VirtualAppService virtualAppService;

    @BeforeEach
    void initService()
    {
        final BundleContext bundleContext = getBundleContext();

        this.applicationRegistry =
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), new ApplicationFactoryServiceMock() );
        this.eventPublisher = mock( EventPublisher.class );
        this.appFilterService = mock( AppFilterService.class );

        AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        AuditLogService auditLogService = mock( AuditLogService.class );

        when( appFilterService.accept( any( ApplicationKey.class ) ) ).thenReturn( true );

        final ApplicationAuditLogSupportImpl auditLogSupport = new ApplicationAuditLogSupportImpl( auditLogService );
        auditLogSupport.activate( appConfig );

        nodeService = mock( NodeService.class );

        virtualAppService = new VirtualAppService( nodeService );

        this.service = new ApplicationServiceImpl( applicationRegistry, repoService, eventPublisher, appFilterService, virtualAppService,
                                                   auditLogSupport );
    }

    @Test
    void get_installed_application()
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        final ApplicationAdaptor result = (ApplicationAdaptor) this.service.getInstalledApplication( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test
    void get_application()
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        final ApplicationAdaptor result = (ApplicationAdaptor) this.service.get( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test
    void get_virtual_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        when( nodeService.nodeExists(
            new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) ) ) ).thenReturn( true );

        final Application virtualApp = this.service.get( applicationKey );

        assertEquals( applicationKey, virtualApp.getKey() );
        assertNull( virtualApp.getUrl() );
        assertEquals( "app1", virtualApp.getDisplayName() );
        assertTrue( virtualApp.getModifiedTime().compareTo( Instant.now() ) <= 0 );
    }

    @Test
    void create_virtual_application()
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).name( "app-node" ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( appNode );

        final Application result = VirtualAppContext.createAdminContext()
            .callWith( () -> this.service.createVirtualApplication( CreateVirtualApplicationParams.create().key( appKey ).build() ) );

        assertEquals( appKey, result.getKey() );
    }

    @Test
    void create_virtual_application_without_admin()
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( appNode );

        assertThrows( ForbiddenAccessException.class,
                      () -> this.service.createVirtualApplication( CreateVirtualApplicationParams.create().key( appKey ).build() ) );
    }

    @Test
    void delete_virtual_application()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        final DeleteNodeResult result = DeleteNodeResult.create()
            .add( new DeleteNodeResult.Result( NodeId.from( "nodeId" ), NodeVersionId.from( "nodeVersionId" ) ) )
            .build();
        when( nodeService.delete( argThat( argument -> new NodePath( "/app1" ).equals( argument.getNodePath() ) ) ) ).thenReturn( result );

        assertTrue( VirtualAppContext.createAdminContext().callWith( () -> this.service.deleteVirtualApplication( appKey ) ) );
    }

    @Test
    void delete_virtual_application_without_admin()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        final DeleteNodeResult result = DeleteNodeResult.create()
            .add( new DeleteNodeResult.Result( NodeId.from( "nodeId" ), NodeVersionId.from( "nodeVersionId" ) ) )
            .build();
        when( nodeService.delete( argThat( argument -> new NodePath( "/app1" ).equals( argument.getNodePath() ) ) ) ).thenReturn( result );

        assertThrows( ForbiddenAccessException.class, () -> this.service.deleteVirtualApplication( appKey ) );
    }

    @Test
    void get_application_not_found()
    {
        assertNull( this.service.getInstalledApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void get_all_applications()
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        deployBundle( "noapp" );

        applicationRegistry.registerApplication( bundle1 );
        applicationRegistry.registerApplication( bundle2 );

        final Applications result = this.service.getInstalledApplications();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    void list()
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        deployBundle( "noapp" );

        applicationRegistry.registerApplication( bundle1 );
        applicationRegistry.registerApplication( bundle2 );

        NodeId virtualAppNodeId = NodeId.from( "virtual-app-id" );

        final NodeIds ids = NodeIds.from( virtualAppNodeId );

        when( nodeService.findByParent( isA( FindNodesByParentParams.class ) ) ).thenReturn(
            FindNodesByParentResult.create().totalHits( 1L ).nodeIds( ids ).build() );

        when( nodeService.getByIds( ids ) ).thenReturn(
            Nodes.from( Node.create().id( new NodeId() ).name( "app3" ).parentPath( NodePath.ROOT ).build() ) );

        final Applications result = this.service.list();
        assertNotNull( result );
        assertEquals( 3, result.getSize() );
        assertEquals( "app3", result.get( 2 ).getKey().toString() );
    }

    @Test
    void start_application()
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.registerApplication( bundle );

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( applicationKey );
        assertEquals( Bundle.ACTIVE, bundle.getState() );

        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    @Test
    void start_missing_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertThrows( ApplicationNotFoundException.class, () -> this.service.startApplication( applicationKey ) );
    }

    @Test
    void start_app_atleast_version()
    {
        // At a time of writing Felix version is 6.0.1. All greater versions should work as well.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "6.0" ) );

        applicationRegistry.registerApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ) );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    void start_app_version_range()
    {
        // At a time of writing Felix version is 6.0.1. Range covers all future versions as well.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "(6.0,9999.0]" ) );

        applicationRegistry.registerApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ) );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    void start_app_invalid_version_range()
    {
        // Version upper bound is too low for current and future Felix version (at a time of writing 6.0.1)
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "[5.1,5.2)" ) );

        applicationRegistry.registerApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class, () -> this.service.startApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void start_ex()
    {
        // There is no version 0.0 of Felix.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "[0.0,0.0]" ) );

        applicationRegistry.registerApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class, () -> this.service.startApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void stop_application()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.registerApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        this.service.stopApplication( applicationKey );
        assertEquals( Bundle.RESOLVED, bundle.getState() );

        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stop( applicationKey ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stopped( applicationKey ) ) ) );
    }

    @Test
    void stop_system_application_ignored()
        throws Exception
    {
        final Bundle bundle = deploySystemAppBundle( "systemApp" );

        applicationRegistry.registerApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "systemApp" );
        this.service.stopApplication( applicationKey );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    void install_global()
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "my-bundle" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installGlobalApplication( byteSource );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstallEvents( ApplicationKey.from( "my-bundle" ), node.id(), times( 1 ) );
        verifyInstalledEvents( ApplicationKey.from( "my-bundle" ), node.id(), times( 1 ) );
        verifyStartedEvent( application.getKey(), times( 1 ) );
    }

    @Test
    void install_global_invalid()
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName, false );

        assertThrows( ApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource ) );
    }

    @Test
    void install_global_denied()
    {
        when( appFilterService.accept( any( ApplicationKey.class ) ) ).thenReturn( false );

        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        assertThrows( ApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource ) );
    }

    @Test
    void install_local()
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "my-bundle" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( ApplicationKey.from( "my-bundle" ), node.id(), never() );
        verifyStartedEvent( application.getKey(), never() );
    }

    @Test
    void install_local_invalid()
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "my-bundle" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource source = createBundleSource( bundleName, false );

        assertThrows( ApplicationInstallException.class, () -> this.service.installLocalApplication( source ) );
    }

    @Test
    void update_installed_application()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "my-bundle" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.upsertApplicationNode( Mockito.isA( AppInfo.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ) );

        mockRepoGetNode( node, bundleName );

        final Application updatedApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ) );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    void update_installed_local_application()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "my-bundle" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.upsertApplicationNode( Mockito.isA( AppInfo.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ) );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ) );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        verifyInstalledEvents( ApplicationKey.from( "my-bundle" ), node.id(), never() );
        verifyStartedEvent( updatedApplication.getKey(), never() );
    }

    @Test
    void install_stored_applications()
    {
        final String bundleName1 = "my-bundle1";
        final String bundleName2 = "my-bundle2";

        ApplicationKey applicationKey1 = ApplicationKey.from( bundleName1 );
        ApplicationKey applicationKey2 = ApplicationKey.from( bundleName2 );

        when( appFilterService.accept( applicationKey2 ) ).thenReturn( false );

        final Node node1 = Node.create()
            .id( NodeId.from( "myNodeId1" ) )
            .name( bundleName1 )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final Node node2 = Node.create()
            .id( NodeId.from( "myNodeId2" ) )
            .name( bundleName2 )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        when( this.repoService.getApplications() ).thenReturn( Nodes.from( node1, node2 ) );

        when( this.repoService.getApplicationSource( node1.id() ) ).thenReturn( createBundleSource( bundleName1 ) );
        when( this.repoService.getApplicationSource( node2.id() ) ).thenReturn( createBundleSource( bundleName2 ) );

        this.service.installAllStoredApplications();

        assertFalse( this.service.isLocalApplication( applicationKey1 ) );
        assertNotNull( this.service.getInstalledApplication( applicationKey1 ) );
        assertNull( this.service.getInstalledApplication( applicationKey2 ) );

        verifyInstalledEvents( applicationKey1, node1.id(), never() );
        verifyStartedEvent( applicationKey1, never() );
    }


    @Test
    void uninstall_global_application()
    {
        final ApplicationKey application = ApplicationKey.from( "myBundle" );

        this.service.uninstallApplication( application );

        assertNull( this.service.getInstalledApplication( application ) );

        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstall( application ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application ) ) ) );
    }

    @Test
    void uninstall_local_application()
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource );
        assertNotNull( this.service.getInstalledApplication( application.getKey() ) );

        this.service.uninstallApplication( application.getKey() );
        assertNull( this.service.getInstalledApplication( application.getKey() ) );

        verify( this.eventPublisher, never() ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstall( application.getKey() ) ) ) );
        verify( this.eventPublisher, never() ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application.getKey() ) ) ) );
    }

    @Test
    void install_local_overriding_global()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.upsertApplicationNode( Mockito.isA( AppInfo.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ) );

        assertFalse( this.service.isLocalApplication( originalApplication.getKey() ) );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ) );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }


    @Test
    void uninstall_local_reinstall_global()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBoolean( ApplicationPropertyNames.STARTED, true );

        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).data( data ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.upsertApplicationNode( Mockito.isA( AppInfo.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn( node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ) );

        final ApplicationKey applicationKey = originalApplication.getKey();

        assertFalse( this.service.isLocalApplication( applicationKey ) );
        assertEquals( "1.0.0", originalApplication.getVersion().toString() );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ) );

        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );

        assertTrue( this.service.isLocalApplication( applicationKey ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( applicationKey ) );
        assertTrue( this.service.isLocalApplication( applicationKey ) );

        when( this.repoService.getApplicationSource( node.id() ) ).thenReturn(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.0" ).build() ) ) );

        this.service.uninstallApplication( updatedApplication.getKey() );

        assertEquals( originalApplication.getVersion(), this.service.getInstalledApplication( applicationKey ).getVersion() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    void install_global_when_local_installed()
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installLocalApplication( byteSource );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        when( this.repoService.getApplicationNode( application.getKey() ) ).thenReturn( node );

        assertThrows( ApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource ) );

        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        verifyInstalledEvents( ApplicationKey.from( "myNode" ), node.id(), never() );
    }

    @Test
    void deactivate()
        throws Exception
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        final Bundle bundle3 = deploySystemAppBundle( "systemApp" );
        bundle3.start();

        applicationRegistry.registerApplication( bundle1 );
        applicationRegistry.registerApplication( bundle2 );
        applicationRegistry.configure( bundle3, mock( Configuration.class ) );

        service.deactivate();
        assertThat( applicationRegistry.getAll() ).map( Application::getKey ).containsOnly( ApplicationKey.from( "systemApp" ) );
    }

    @Test
    void configuration_comes_first()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );
        bundle.start();
        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        final Application app = service.getInstalledApplication( key );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    void configuration_comes_last()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );
        bundle.start();
        applicationRegistry.registerApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    void configuration_comes_twice()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );
        bundle.start();
        applicationRegistry.registerApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        assertEquals( ConfigBuilder.create().add( "c", "d" ).build(), app.getConfig() );
    }

    @Test
    void configuration_comes_twice_invalidators_called()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );
        bundle.start();

        applicationRegistry.registerApplication( bundle );

        service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configure( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        verify( mock, times( 1 ) ).invalidate( eq( key ), eq( ApplicationInvalidationLevel.FULL ) );
    }


    @Test
    void get_application_mode()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        final List<String> appNodeNames = List.of( "site", "content-types", "mixins", "x-data", "parts", "layouts", "pages" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenAnswer( params -> {
            final CreateNodeParams createNodeParams = params.getArgument( 0 );

            if ( applicationKey.toString().equals( createNodeParams.getName().toString() ) )
            {

                when( nodeService.nodeExists(
                    new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) ) ) ).thenReturn(
                    true );

                return Node.create()
                    .id( NodeId.from( createNodeParams.getName() ) )
                    .name( createNodeParams.getName() )
                    .parentPath( NodePath.ROOT )
                    .build();

            }
            if ( appNodeNames.contains( createNodeParams.getName().toString() ) )
            {
                return Node.create()
                    .id( NodeId.from( createNodeParams.getName() ) )
                    .name( createNodeParams.getName() )
                    .parentPath( new NodePath( "/app1" ) )
                    .build();
            }

            return null;
        } );

        VirtualAppContext.createAdminContext()
            .runWith( () -> virtualAppService.create( CreateVirtualApplicationParams.create().key( applicationKey ).build() ) );

        assertThrows( ForbiddenAccessException.class, () -> service.getApplicationMode( applicationKey ) );
        assertEquals( ApplicationMode.VIRTUAL,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        assertEquals( ApplicationMode.AUGMENTED,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

    }

    @Test
    void get_application_mode_bundled()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        when( nodeService.findByQuery( isA( NodeQuery.class ) ) ).thenAnswer( searchParams -> FindNodesByQueryResult.create().build() );

        assertNull( VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        assertEquals( ApplicationMode.BUNDLED,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );
    }

    private void verifyInstalledEvents( final ApplicationKey applicationKey, final NodeId nodeId, final VerificationMode times )
    {
        verify( this.eventPublisher, times ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.installed( applicationKey, nodeId ) ) ) );
    }

    private void verifyInstallEvents( final ApplicationKey applicationKey, final NodeId nodeId, final VerificationMode times )
    {
        verify( this.eventPublisher, times ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.install( applicationKey, nodeId ) ) ) );
    }

    private void verifyStartedEvent( final ApplicationKey applicationKey, final VerificationMode never )
    {
        verify( this.eventPublisher, never ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verify( this.eventPublisher, never ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    private void mockRepoCreateNode( final Node node )
    {
        when( this.repoService.upsertApplicationNode( Mockito.isA( AppInfo.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn( node );
    }

    private void mockRepoGetNode( final Node applicationNode, final String appName )
    {
        when( this.repoService.getApplicationNode( ApplicationKey.from( appName ) ) ).thenReturn( applicationNode );
    }

    private ByteSource createBundleSource( final String bundleName )
    {
        return createBundleSource( bundleName, true );
    }

    private ByteSource createBundleSource( final String bundleName, final boolean isApp )
    {
        final InputStream in = newBundle( bundleName, isApp ).build();

        try
        {
            return ByteSource.wrap( ByteStreams.toByteArray( in ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Bundle deployBundle( final String key )
    {
        final InputStream in = newBundle( key, false ).build();

        return deploy( key, in );
    }

    private Bundle deployAppBundle( final String key )
    {
        final InputStream in = newBundle( key, true ).build();

        return deploy( key, in );
    }

    private Bundle deployAppBundle( final String key, final VersionRange systemVersionRange )
    {
        final InputStream in = newBundle( key, true ).setHeader( ApplicationManifestConstants.X_SYSTEM_VERSION,
                                                                 systemVersionRange != null ? systemVersionRange.toString() : null )
            .build();

        return deploy( key, in );
    }

    private Bundle deploySystemAppBundle( final String key )
    {
        final InputStream in = newBundle( key, true ).setHeader( ApplicationManifestConstants.X_BUNDLE_TYPE, "system" ).build();

        return deploy( key, in );
    }

    private static class ApplicationEventMatcher
        implements ArgumentMatcher<Event>
    {
        Event thisObject;

        public ApplicationEventMatcher( Event thisObject )
        {
            this.thisObject = thisObject;
        }

        @Override
        public boolean matches( Event argument )
        {
            if ( argument == null || thisObject.getClass() != argument.getClass() )
            {
                return false;
            }

            return thisObject.getType().equals( argument.getType() ) && this.thisObject.getData().equals( argument.getData() );
        }
    }
}
