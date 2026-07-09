package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

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
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.Applications;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.schema.NamespaceConstants;
import com.enonic.xp.core.impl.schema.NamespaceAppService;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final CreateNodeParams params = invocation.getArgument( 0 );
            return Node.create().id( NodeId.from( params.getName() ) ).name( params.getName() ).parentPath( params.getParent() ).build();
        } );

        final NamespaceAppService namespaceAppService = new NamespaceAppService( nodeService );

        this.service = new ApplicationServiceImpl( applicationRegistry, repoService, eventPublisher, appFilterService, namespaceAppService,
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
    void get_application_not_found()
    {
        assertNull( this.service.getInstalledApplication( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void get_prefers_installed_application()
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        final Application result = this.service.get( ApplicationKey.from( "app1" ) );

        assertSame( applicationRegistry.get( ApplicationKey.from( "app1" ) ), result );
    }

    @Test
    void get_returns_namespace_backed_application()
    {
        final ApplicationKey key = ApplicationKey.from( "app1" );

        final NodePath appPath = new NodePath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( key.toString() ) );
        when( nodeService.getByPath( appPath ) ).thenReturn(
            Node.create().id( NodeId.from( "app-node" ) ).name( key.toString() ).parentPath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
                .build() );

        final Application result = this.service.get( key );

        assertNotNull( result );
        assertEquals( key, result.getKey() );
        assertTrue( result.isStarted() );
    }

    @Test
    void get_not_installed_and_no_namespace()
    {
        assertNull( this.service.get( ApplicationKey.from( "app1" ) ) );
    }

    @Test
    void list_merges_installed_applications_and_namespaces()
    {
        final Bundle bundle = deployAppBundle( "app1" );
        applicationRegistry.registerApplication( bundle );

        final NodeIds ids = NodeIds.from( NodeId.from( "ns1" ), NodeId.from( "ns2" ) );
        when( nodeService.list( isA( ListNodesParams.class ) ) ).thenReturn( ListNodesResult.create()
                                                                                 .addEntry( new NodeListEntry( NodeId.from( "ns1" ),
                                                                                                               new NodePath( "/app1" ),
                                                                                                               Instant.EPOCH ) )
                                                                                 .addEntry( new NodeListEntry( NodeId.from( "ns2" ),
                                                                                                               new NodePath( "/app2" ),
                                                                                                               Instant.EPOCH ) )
                                                                                 .build() );
        when( nodeService.getByIds( ids ) ).thenReturn(
            Nodes.from( Node.create().id( new NodeId() ).name( "app1" ).parentPath( NodePath.ROOT ).build(),
                        Node.create().id( new NodeId() ).name( "app2" ).parentPath( NodePath.ROOT ).build() ) );

        final Applications result = this.service.list();

        assertEquals( 2, result.getSize() );

        final Application app1 =
            result.stream().filter( app -> app.getKey().equals( ApplicationKey.from( "app1" ) ) ).findFirst().orElseThrow();
        assertSame( applicationRegistry.get( ApplicationKey.from( "app1" ) ), app1 );

        assertTrue( result.stream().anyMatch( app -> app.getKey().equals( ApplicationKey.from( "app2" ) ) ) );
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
    void start_system_application_ignored()
        throws Exception
    {
        final Bundle bundle = deploySystemAppBundle( "systemApp" );

        applicationRegistry.registerApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "systemApp" );
        this.service.startApplication( applicationKey );
        assertEquals( Bundle.ACTIVE, bundle.getState() );
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
    void stopApplication_systemApp_throws()
        throws Exception
    {
        final Bundle bundle = deploySystemAppBundle( "systemApp" );

        applicationRegistry.registerApplication( bundle );

        bundle.start();

        assertEquals( Bundle.ACTIVE, bundle.getState() );
        final ApplicationKey applicationKey = ApplicationKey.from( "systemApp" );

        assertThatThrownBy( () -> this.service.stopApplication( applicationKey ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "system application" );

        assertEquals( Bundle.ACTIVE, bundle.getState() );
    }

    @Test
    void install_global()
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installGlobalApplication( byteSource );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertFalse( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstallEvents( ApplicationKey.from( "my.bundle" ), node.id(), times( 1 ) );
        verifyInstalledEvents( ApplicationKey.from( "my.bundle" ), node.id(), times( 1 ) );
        verifyStartedEvent( application.getKey(), times( 1 ) );
    }

    @Test
    void install_global_persists_schema()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final String cmsResource = "kind: \"CMS\"\nform: [ ]\n";
        final String contentTypeResource = "kind: \"ContentType\"\nform: [ ]\n";
        final String phrasesResource = "action.save=Save\n";

        final ByteSource byteSource = ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true )
                                                                                    .addResource( "cms/cms.yaml", new ByteArrayInputStream(
                                                                                        cmsResource.getBytes( StandardCharsets.UTF_8 ) ) )
                                                                                    .addResource(
                                                                                        "cms/content-types/mytype/mytype.yaml",
                                                                                        new ByteArrayInputStream(
                                                                                            contentTypeResource.getBytes(
                                                                                                StandardCharsets.UTF_8 ) ) )
                                                                                    .addResource(
                                                                                        "cms/i18n/phrases/phrases_en.properties",
                                                                                        new ByteArrayInputStream(
                                                                                            phrasesResource.getBytes(
                                                                                                StandardCharsets.UTF_8 ) ) )
                                                                                    .build() ) );

        this.service.installGlobalApplication( byteSource );

        verify( nodeService ).create( argThat( ( CreateNodeParams params ) -> bundleName.equals( params.getName().toString() ) &&
            NamespaceConstants.NAMESPACE_APP_ROOT_PARENT.equals( params.getParent() ) ) );

        verify( nodeService ).create( argThat( ( CreateNodeParams params ) -> "cms.yaml".equals( params.getName().toString() ) &&
            cmsResource.equals( params.getData().getString( "resource" ) ) ) );

        verify( nodeService ).create( argThat( ( CreateNodeParams params ) -> "mytype.yaml".equals( params.getName().toString() ) &&
            contentTypeResource.equals( params.getData().getString( "resource" ) ) ) );

        verify( nodeService ).create( argThat( ( CreateNodeParams params ) -> "phrases_en.properties".equals( params.getName().toString() ) &&
            phrasesResource.equals( params.getData().getString( "resource" ) ) ) );
    }

    @Test
    void install_global_without_cms_yaml_does_not_persist_schema()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final String contentTypeResource = "kind: \"ContentType\"\nform: [ ]\n";

        final ByteSource byteSource = ByteSource.wrap( ByteStreams.toByteArray( newBundle( bundleName, true )
                                                                                    .addResource(
                                                                                        "cms/content-types/mytype/mytype.yaml",
                                                                                        new ByteArrayInputStream(
                                                                                            contentTypeResource.getBytes(
                                                                                                StandardCharsets.UTF_8 ) ) )
                                                                                    .build() ) );

        this.service.installGlobalApplication( byteSource );

        verify( nodeService, never() ).create( any( CreateNodeParams.class ) );
    }

    @Test
    void install_local_does_not_persist_schema()
        throws Exception
    {
        final ByteSource byteSource = createBundleSource( "my.bundle" );

        this.service.installLocalApplication( byteSource );

        verify( nodeService, never() ).create( any( CreateNodeParams.class ) );
    }

    @Test
    void install_global_invalid()
    {
        final Node applicationNode = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName, false );

        assertThrows( ApplicationBundleException.class, () -> this.service.installGlobalApplication( byteSource ) );
    }

    @Test
    void install_global_denied()
    {
        when( appFilterService.accept( any( ApplicationKey.class ) ) ).thenReturn( false );

        final Node applicationNode = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );

        assertThrows( ApplicationBundleException.class, () -> this.service.installGlobalApplication( byteSource ) );
    }

    @Test
    void install_local()
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( node );
        mockRepoGetNode( node, bundleName );

        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource );

        assertNotNull( application );
        assertEquals( bundleName, application.getKey().getName() );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );
        assertEquals( application, this.service.getInstalledApplication( application.getKey() ) );

        verifyInstalledEvents( ApplicationKey.from( "my.bundle" ), node.id(), never() );
        verifyStartedEvent( application.getKey(), never() );
    }

    @Test
    void install_local_invalid()
    {
        final Node applicationNode = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( applicationNode );
        mockRepoGetNode( applicationNode, bundleName );

        final ByteSource source = createBundleSource( bundleName, false );

        assertThrows( ApplicationBundleException.class, () -> this.service.installLocalApplication( source ) );
    }

    @Test
    void update_installed_application()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

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
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "my.bundle" ).build();

        final String bundleName = "my.bundle";

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

        verifyInstalledEvents( ApplicationKey.from( "my.bundle" ), node.id(), never() );
        verifyStartedEvent( updatedApplication.getKey(), never() );
    }

    @Test
    void install_stored_applications()
    {
        final String bundleName1 = "my.bundle1";
        final String bundleName2 = "my.bundle2";

        ApplicationKey applicationKey1 = ApplicationKey.from( bundleName1 );
        ApplicationKey applicationKey2 = ApplicationKey.from( bundleName2 );

        when( appFilterService.accept( applicationKey2 ) ).thenReturn( false );

        final Node node1 = Node.create()
            .id( NodeId.from( "mynodeid1" ) )
            .name( bundleName1 )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        final Node node2 = Node.create()
            .id( NodeId.from( "mynodeid2" ) )
            .name( bundleName2 )
            .parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .build();

        when( this.repoService.getApplications() ).thenReturn( Nodes.from( node1, node2 ) );

        when( this.repoService.getApplicationNode( applicationKey1 ) ).thenReturn( node1 );
        when( this.repoService.getApplicationNode( applicationKey2 ) ).thenReturn( node2 );
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
        final Bundle bundle = deployAppBundle( "myBundle" );
        applicationRegistry.registerApplication( bundle );

        final ApplicationKey application = ApplicationKey.from( "myBundle" );

        this.service.uninstallApplication( application );

        assertNull( this.service.getInstalledApplication( application ) );

        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstall( application ) ) ) );
        verify( this.eventPublisher, times( 1 ) ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.uninstalled( application ) ) ) );
    }

    @Test
    void uninstall_missing_application()
    {
        final ApplicationKey application = ApplicationKey.from( "myBundle" );

        assertThrows( ApplicationNotFoundException.class, () -> this.service.uninstallApplication( application ) );
    }

    @Test
    void install_local_overriding_global()
        throws Exception
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my.bundle";

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
    void install_global_system_application_throws()
    {
        final ByteSource byteSource;
        try
        {
            byteSource = ByteSource.wrap( ByteStreams.toByteArray(
                newBundle( "my.system.bundle", true ).setHeader( ApplicationManifestConstants.X_BUNDLE_TYPE, "system" ).build() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        assertThrows( ApplicationBundleException.class, () -> this.service.installGlobalApplication( byteSource ) );
    }

    @Test
    void uninstall_local_application()
    {
        final String bundleName = "my.bundle";
        final ByteSource byteSource = createBundleSource( bundleName );
        final Application application = this.service.installLocalApplication( byteSource );

        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        this.service.uninstallLocalApplication( application.getKey() );

        assertFalse( this.service.isLocalApplication( application.getKey() ) );
    }

    @Test
    void uninstall_local_application_not_local_throws()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "non.local.app" );

        assertThrows( ApplicationNotFoundException.class, () -> this.service.uninstallLocalApplication( applicationKey ) );
    }

    @Test
    void uninstall_local_reinstall_global_throws()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBoolean( ApplicationPropertyNames.STARTED, true );

        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).data( data ).build();

        final String bundleName = "my.bundle";

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

        assertThrows( ApplicationBundleException.class, () -> this.service.uninstallApplication( updatedApplication.getKey() ) );
        assertTrue( this.service.isLocalApplication( applicationKey ) );
    }

    @Test
    void install_global_when_local_installed()
    {
        final Node node = Node.create().id( NodeId.from( "mynode" ) ).parentPath( NodePath.ROOT ).name( "myNode" ).build();

        final String bundleName = "my.bundle";

        mockRepoCreateNode( node );

        final ByteSource byteSource = createBundleSource( bundleName );

        final Application application = this.service.installLocalApplication( byteSource );
        assertTrue( this.service.isLocalApplication( application.getKey() ) );

        when( this.repoService.getApplicationNode( application.getKey() ) ).thenReturn( node );

        assertThrows( ApplicationBundleException.class, () -> this.service.installGlobalApplication( byteSource ) );

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
        assertThat( applicationRegistry.getAll() ).isEmpty();
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

    private void verifyInstalledEvents( final ApplicationKey applicationKey, final NodeId nodeId, final VerificationMode times )
    {
        verify( this.eventPublisher, times ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.installed( applicationKey ) ) ) );
    }

    private void verifyInstallEvents( final ApplicationKey applicationKey, final NodeId nodeId, final VerificationMode times )
    {
        verify( this.eventPublisher, times ).publish(
            argThat( new ApplicationEventMatcher( ApplicationClusterEvents.install( applicationKey ) ) ) );
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
