package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
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
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationMode;
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
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SecurityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ApplicationServiceImplTest
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
    public void initService()
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

        final IndexService indexService = mock( IndexService.class );
        final RepositoryService repositoryService = mock( RepositoryService.class );
        nodeService = mock( NodeService.class );

        SecurityService securityService = mock( SecurityService.class );

        virtualAppService = new VirtualAppService( indexService, repositoryService, nodeService, securityService );

        this.service = new ApplicationServiceImpl( bundleContext, applicationRegistry, repoService, eventPublisher, appFilterService,
                                                   virtualAppService, auditLogSupport );
    }

    @Test
    public void get_installed_application()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.installApplication( bundle );

        final Application result = this.service.getInstalledApplication( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test
    public void get_application()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.installApplication( bundle );

        final Application result = this.service.get( ApplicationKey.from( "app1" ) );
        assertNotNull( result );
        assertSame( bundle, result.getBundle() );
    }

    @Test
    public void get_virtual_application()
        throws Exception
    {
        NodeId virtualAppNodeId = NodeId.from( "virtual-app-id" );

        when( nodeService.findByQuery( isA( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create()
                                                                                  .totalHits( 1L )
                                                                                  .hits( 1L )
                                                                                  .addNodeHit(
                                                                                      NodeHit.create().nodeId( virtualAppNodeId ).build() )
                                                                                  .build() );

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        final Application virtualApp = this.service.get( applicationKey );

        assertNull( virtualApp.getBundle() );
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
    public void create_virtual_application_without_admin()
        throws Exception
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( appNode );

        assertThrows( ForbiddenAccessException.class,
                      () -> this.service.createVirtualApplication( CreateVirtualApplicationParams.create().key( appKey ).build() ) );
    }

    @Test
    public void delete_virtual_application()
        throws Exception
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.deleteByPath( NodePath.create( "/app1" ).build() ) ).thenReturn( NodeIds.from( appNode.id() ) );

        assertTrue( VirtualAppContext.createAdminContext().callWith( () -> this.service.deleteVirtualApplication( appKey ) ) );
    }

    @Test
    public void delete_virtual_application_without_admin()
        throws Exception
    {
        final Node appNode = Node.create().id( NodeId.from( "app-node" ) ).parentPath( NodePath.ROOT ).build();
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        when( nodeService.deleteByPath( NodePath.create( "/app1" ).build() ) ).thenReturn( NodeIds.from( appNode.id() ) );

        assertThrows( ForbiddenAccessException.class, () -> this.service.deleteVirtualApplication( appKey ) );
    }

    @Test
    public void get_application_not_found()
    {
        assertNull( this.service.getInstalledApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    public void get_all_applications()
        throws Exception
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        deployBundle( "noapp" );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );

        final Applications result = this.service.getInstalledApplications();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }

    @Test
    public void list()
        throws Exception
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        deployBundle( "noapp" );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );

        NodeId virtualAppNodeId = NodeId.from( "virtual-app-id" );

        final NodeIds ids = NodeIds.from( virtualAppNodeId );

        when( nodeService.findByParent( isA( FindNodesByParentParams.class ) ) ).thenReturn(
            FindNodesByParentResult.create().totalHits( 1L ).hits( 1L ).nodeIds( ids ).build() );

        when( nodeService.getByIds( ids ) ).thenReturn(
            Nodes.from( Node.create().id( new NodeId() ).name( "app3" ).parentPath( NodePath.ROOT ).build() ) );

        final Applications result = this.service.list();
        assertNotNull( result );
        assertEquals( 3, result.getSize() );
        assertEquals( "app3", result.get( 2 ).getKey().toString() );
    }

    @Test
    public void get_application_keys()
        throws Exception
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        deployAppBundle( "noapp" );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );

        final ApplicationKeys result = this.service.getInstalledApplicationKeys();
        assertNotNull( result );
        assertEquals( 2, result.getSize() );
        assertTrue( result.contains( ApplicationKey.from( "app1" ) ) );
        assertTrue( result.contains( ApplicationKey.from( "app2" ) ) );
    }

    @Test
    public void start_application()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.installApplication( bundle );

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( applicationKey, true );
        assertEquals( Bundle.ACTIVE, bundle.getState() );

        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    @Test
    public void start_missing_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        this.service.startApplication( applicationKey, true );

        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verifyNoMoreInteractions( this.eventPublisher );
    }

    @Test
    public void start_application_no_triggerEvent()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.installApplication( bundle );

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( applicationKey, false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );

        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    @Test
    public void start_app_atleast_version()
        throws Exception
    {
        // At a time of writing Felix version is 6.0.1. All greater versions should work as well.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "6.0" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void start_app_version_range()
        throws Exception
    {
        // At a time of writing Felix version is 6.0.1. Range covers all future versions as well.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "(6.0,9999.0]" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        this.service.startApplication( ApplicationKey.from( "app1" ), false );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void start_app_invalid_version_range()
        throws Exception
    {
        // Version upper bound is too low for current and future Felix version (at a time of writing 6.0.1)
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "[5.1,5.2)" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> this.service.startApplication( ApplicationKey.from( "app1" ), false ) );
    }

    @Test
    public void start_ex()
        throws Exception
    {
        // There is no version 0.0 of Felix.
        final Bundle bundle = deployAppBundle( "app1", VersionRange.valueOf( "[0.0,0.0]" ) );

        applicationRegistry.installApplication( bundle );

        assertEquals( Bundle.INSTALLED, bundle.getState() );
        assertThrows( ApplicationInvalidVersionException.class,
                      () -> this.service.startApplication( ApplicationKey.from( "app1" ), false ) );
    }

    @Test
    public void stop_application()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.installApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        this.service.stopApplication( applicationKey, true );
        assertEquals( Bundle.RESOLVED, bundle.getState() );

        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stop( applicationKey ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stopped( applicationKey ) ) ) );
    }

    @Test
    public void stop_system_application_ignored()
        throws Exception
    {
        final Bundle bundle = deploySystemAppBundle( "systemApp" );

        applicationRegistry.installApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "systemApp" );
        this.service.stopApplication( applicationKey, true );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    public void stop_application_no_triggerEvent()
        throws Exception
    {
        final Bundle bundle = deployAppBundle( "app1" );

        applicationRegistry.installApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        this.service.stopApplication( applicationKey, false );
        assertEquals( Bundle.RESOLVED, bundle.getState() );

        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stop( applicationKey ) ) ) );
        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.stopped( applicationKey ) ) ) );
    }

    @Test
    public void install_global()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installGlobalApplication( byteSource, bundleName );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, times( 1 ) );
        verifyStartedEvent( application.getKey(), times( 1 ) );
    }

    @Test
    public void install_global_invalid()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName, false );

        assertThrows( ApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource, bundleName ) );
    }

    @Test
    public void install_global_denied()
        throws Exception
    {
        when( appFilterService.accept( any( ApplicationKey.class ) ) ).thenReturn( false );

        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        assertThrows( ApplicationInstallException.class, () -> this.service.installGlobalApplication( byteSource, bundleName ) );
    }

    @Test
    public void install_local()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource, bundleName );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, never() );
        verifyStartedEvent( application.getKey(), never() );
    }

    @Test
    public void install_local_invalid()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource source = createBundleSource( bundleName, false );

        assertThrows( ApplicationInstallException.class, () -> this.service.installLocalApplication( source, bundleName ) );
    }

    @Test
    public void update_installed_application()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn(
            node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ), bundleName );

        mockRepoGetNode( node, bundleName );

        final Application updatedApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    public void update_installed_local_application()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn(
            node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ), bundleName );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        verifyInstalledEvents( node, never() );
        verifyStartedEvent( updatedApplication.getKey(), never() );
    }

    @Test
    public void install_stored_application_not_found()
        throws Exception
    {
        assertThrows( ApplicationInstallException.class, () -> this.service.installStoredApplication( NodeId.from( "dummy" ) ) );
    }

    @Test
    public void install_stored_application()
        throws Exception
    {
        final Node node = Node.create()
            .id( NodeId.from( "myNodeId" ) )
            .name( "myBundle" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final String bundleName = "my-bundle";

        when( this.repoService.getApplicationSource( node.id() ) ).thenReturn( createBundleSource( bundleName ) );

        final Application application = this.service.installStoredApplication( node.id() );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( node, never() );
        verifyStartedEvent( application.getKey(), never() );
    }

    @Test
    public void install_stored_application_denied()
        throws Exception
    {
        when( appFilterService.accept( any( ApplicationKey.class ) ) ).thenReturn( false );

        final Node node = Node.create()
            .id( NodeId.from( "myNodeId" ) )
            .name( "myBundle" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final String bundleName = "my-bundle";

        when( this.repoService.getApplicationSource( node.id() ) ).thenReturn( createBundleSource( bundleName ) );

        final Application application = this.service.installStoredApplication( node.id() );
        assertNull( application );
    }

    @Test
    public void install_stored_applications()
        throws Exception
    {
        final String bundleName1 = "my-bundle1";
        final String bundleName2 = "my-bundle2";

        ApplicationKey applicationKey1 = ApplicationKey.from( bundleName1 );
        ApplicationKey applicationKey2 = ApplicationKey.from( bundleName2 );

        when( appFilterService.accept( applicationKey2 ) ).thenReturn( false );

        final Node node1 = Node.create()
            .id( NodeId.from( "myNodeId1" ) )
            .name( "myBundle1" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final Node node2 = Node.create()
            .id( NodeId.from( "myNodeId2" ) )
            .name( "myBundle2" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        when( this.repoService.getApplications() ).thenReturn( Nodes.from( node1, node2 ) );

        when( this.repoService.getApplicationSource( node1.id() ) ).thenReturn( createBundleSource( bundleName1 ) );
        when( this.repoService.getApplicationSource( node2.id() ) ).thenReturn( createBundleSource( bundleName2 ) );

        this.service.installAllStoredApplications( ApplicationInstallationParams.create().triggerEvent( false ).build() );

        assertFalse( this.service.isLocalApplication( applicationKey1 ) );
        assertNotNull( this.service.getInstalledApplication( applicationKey1 ) );
        assertNull( this.service.getInstalledApplication( applicationKey2 ) );

        verifyInstalledEvents( node1, never() );
        verifyStartedEvent( applicationKey1, never() );
    }


    @Test
    public void uninstall_global_application()
        throws Exception
    {
        final Node node = Node.create()
            .id( NodeId.from( "myNodeId" ) )
            .name( "myBundle" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final String bundleName = "my-bundle";

        when( this.repoService.getApplicationSource( node.id() ) ).thenReturn( createBundleSource( bundleName ) );

        final Application application = this.service.installStoredApplication( node.id() );

        this.service.uninstallApplication( application.getKey(), true );

        assertNull( this.service.getInstalledApplication( application.getKey() ) );

        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstall( application.getKey() ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application.getKey() ) ) ) );
    }

    @Test
    public void uninstall_local_application()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource, bundleName );
        assertNotNull( this.service.getInstalledApplication( application.getKey() ) );

        this.service.uninstallApplication( application.getKey(), false );
        assertNull( this.service.getInstalledApplication( application.getKey() ) );

        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstall( application.getKey() ) ) ) );
        verify( this.eventPublisher, never() ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application.getKey() ) ) ) );
    }

    @Test
    public void install_local_overriding_global()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn(
            node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ), bundleName );

        assertFalse( this.service.isLocalApplication( originalApplication.getKey() ) );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ), bundleName );

        assertEquals( "1.0.0", originalApplication.getVersion().toString() );
        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );
        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( updatedApplication.getKey() ) );

        assertTrue( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }


    @Test
    public void uninstall_local_reinstall_global()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBoolean( ApplicationPropertyNames.STARTED, true );

        final Node node = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).data( data ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( node );

        when( this.repoService.updateApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn(
            node );

        mockRepoGetNode( node, bundleName );

        final Application originalApplication = this.service.installGlobalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.0" ).build() ) ), bundleName );

        final ApplicationKey applicationKey = originalApplication.getKey();

        assertFalse( this.service.isLocalApplication( applicationKey ) );
        assertEquals( "1.0.0", originalApplication.getVersion().toString() );

        final Application updatedApplication = this.service.installLocalApplication(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true, "1.0.1" ).build() ) ), bundleName );

        assertEquals( "1.0.1", updatedApplication.getVersion().toString() );

        assertTrue( this.service.isLocalApplication( applicationKey ) );
        assertEquals( updatedApplication, this.service.getInstalledApplication( applicationKey ) );
        assertTrue( this.service.isLocalApplication( applicationKey ) );

        when( this.repoService.getApplicationSource( node.id() ) ).thenReturn(
            ByteSource.wrap( ByteStreams.toByteArray( newBundle( "my-bundle", true, "1.0.0" ).build() ) ) );

        this.service.uninstallApplication( updatedApplication.getKey(), false );

        assertEquals( originalApplication.getVersion(), this.service.getInstalledApplication( applicationKey ).getVersion() );
        assertFalse( this.service.isLocalApplication( updatedApplication.getKey() ) );
    }

    @Test
    public void install_global_when_local_installed()
        throws Exception
    {
        final Node applicationNode = Node.create().id( NodeId.from( "myNode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my-bundle";

        mockRepoCreateNode( applicationNode );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installLocalApplication( byteSource, bundleName );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        when( this.repoService.getApplicationNode( application.getKey() ) ).thenReturn( applicationNode );

        this.service.installGlobalApplication( byteSource, bundleName );

        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        verifyInstalledEvents( applicationNode, times( 1 ) );
    }

    @Test
    void deactivate()
        throws Exception
    {
        final Bundle bundle1 = deployAppBundle( "app1" );
        final Bundle bundle2 = deployAppBundle( "app2" );
        final Bundle bundle3 = deploySystemAppBundle( "systemApp" );

        applicationRegistry.installApplication( bundle1 );
        applicationRegistry.installApplication( bundle2 );
        applicationRegistry.configureApplication( bundle3, mock( Configuration.class ) );

        service.deactivate();
        assertThat( applicationRegistry.getAll() ).map( Application::getKey ).containsOnly( ApplicationKey.from( "systemApp" ) );
    }

    @Test
    public void configuration_comes_first()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        final Application app = service.getInstalledApplication( key );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_last()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );

        applicationRegistry.installApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        assertEquals( ConfigBuilder.create().add( "a", "b" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_twice()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );

        applicationRegistry.installApplication( bundle );

        final Application app = service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        assertEquals( ConfigBuilder.create().add( "c", "d" ).build(), app.getConfig() );
    }

    @Test
    public void configuration_comes_twice_invalidators_called()
        throws Exception
    {
        final ApplicationKey key = ApplicationKey.from( "myapp" );
        final Bundle bundle = deployAppBundle( "myapp" );

        applicationRegistry.installApplication( bundle );

        service.getInstalledApplication( key );

        final ApplicationInvalidator mock = mock( ApplicationInvalidator.class );
        applicationRegistry.addInvalidator( mock );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "a", "b" ).build() );

        applicationRegistry.configureApplication( bundle, ConfigBuilder.create().add( "c", "d" ).build() );

        verify( mock, times( 1 ) ).invalidate( eq( key ), eq( ApplicationInvalidationLevel.FULL ) );
    }


    @Test
    public void get_application_mode()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        final List<String> appNodeNames = List.of( "site", "content-types", "mixins", "x-data", "parts", "layouts", "pages" );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenAnswer( params -> {
            final CreateNodeParams createNodeParams = params.getArgument( 0 );

            if ( applicationKey.toString().equals( createNodeParams.getName() ) )
            {

                when( nodeService.findByQuery( isA( NodeQuery.class ) ) ).thenAnswer( searchParams -> FindNodesByQueryResult.create()
                    .addNodeHit( NodeHit.create().nodeId( NodeId.from( createNodeParams.getName() ) ).build() )
                    .totalHits( 1 )
                    .hits( 1 )
                    .build() );

                return Node.create()
                    .id( NodeId.from( createNodeParams.getName() ) )
                    .name( createNodeParams.getName() )
                    .parentPath( NodePath.ROOT )
                    .build();

            }
            if ( appNodeNames.contains( createNodeParams.getName() ) )
            {
                return Node.create()
                    .id( NodeId.from( createNodeParams.getName() ) )
                    .name( createNodeParams.getName() )
                    .parentPath( NodePath.create( "/app1" ).build() ).build();
            }

            return null;
        } );

        VirtualAppContext.createAdminContext()
            .runWith( () -> virtualAppService.create( CreateVirtualApplicationParams.create().key( applicationKey ).build() ) );

        assertThrows( ForbiddenAccessException.class, () -> service.getApplicationMode( applicationKey ) );
        assertEquals( ApplicationMode.VIRTUAL,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.installApplication( bundle );

        assertEquals( ApplicationMode.AUGMENTED,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

    }

    @Test
    public void get_application_mode_bundled()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        when( nodeService.findByQuery( isA( NodeQuery.class ) ) ).thenAnswer(
            searchParams -> FindNodesByQueryResult.create().totalHits( 0 ).hits( 0 ).build() );

        assertNull( VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );

        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.installApplication( bundle );

        assertEquals( ApplicationMode.BUNDLED,
                      VirtualAppContext.createAdminContext().callWith( () -> service.getApplicationMode( applicationKey ) ) );
    }

    private void verifyInstalledEvents( final Node node, final VerificationMode never )
    {
        verify( this.eventPublisher, never ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.installed( node ) ) ) );
    }


    private void verifyStartedEvent( final ApplicationKey applicationKey, final VerificationMode never )
    {
        verify( this.eventPublisher, never ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.start( applicationKey ) ) ) );
        verify( this.eventPublisher, never ).publish(
            Mockito.argThat( new ApplicationEventMatcher( ApplicationClusterEvents.started( applicationKey ) ) ) );
    }

    private void mockRepoCreateNode( final Node node )
    {
        when( this.repoService.createApplicationNode( Mockito.isA( Application.class ), Mockito.isA( ByteSource.class ) ) ).thenReturn(
            node );
    }

    private void mockRepoGetNode( final Node applicationNode, final String appName )
    {
        when( this.repoService.getApplicationNode( ApplicationKey.from( appName ) ) ).thenReturn( applicationNode );
    }

    private ByteSource createBundleSource( final String bundleName )
        throws IOException
    {
        return createBundleSource( bundleName, true );
    }

    private ByteSource createBundleSource( final String bundleName, final boolean isApp )
        throws IOException
    {
        final InputStream in = newBundle( bundleName, isApp ).build();

        return ByteSource.wrap( ByteStreams.toByteArray( in ) );
    }

    private Bundle deployBundle( final String key )
        throws Exception
    {
        final InputStream in = newBundle( key, false ).build();

        return deploy( key, in );
    }

    private Bundle deployAppBundle( final String key )
        throws Exception
    {
        final InputStream in = newBundle( key, true ).build();

        return deploy( key, in );
    }

    private Bundle deployAppBundle( final String key, final VersionRange systemVersionRange )
        throws Exception
    {
        final InputStream in = newBundle( key, true ).set( ApplicationManifestConstants.X_SYSTEM_VERSION,
                                                           systemVersionRange != null ? systemVersionRange.toString() : null ).build();

        return deploy( key, in );
    }

    private Bundle deploySystemAppBundle( final String key )
        throws Exception
    {
        final InputStream in = newBundle( key, true ).set( ApplicationManifestConstants.X_BUNDLE_TYPE, "system" ).build();

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
