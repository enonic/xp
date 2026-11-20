package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorEditor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateOrUpdateApplicationDescriptorParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.support.ResourceTestHelper;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationDescriptorServiceImplTest
{
    private static final String APP_DESCRIPTOR_FILENAME = "application.yml";

    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    @TempDir
    Path temporaryFolder;

    private ResourceTestHelper resourceTestHelper;

    private ApplicationDescriptorServiceImpl appDescriptorService;

    private NodeService nodeService;

    private ApplicationRepoService repoService;

    private DynamicSchemaAuditLogSupport auditLogSupport;

    private EventPublisher eventPublisher;

    private InstalledBundles installed;

    private ResourceService resourceService;

    // the version of the stubbed application node: every write of the descriptor or icon moves it, as the repo service does
    private final AtomicLong version = new AtomicLong();

    // the mime type of the icon attached to the stubbed descriptor node, followed by the stubbed upsert
    private final AtomicReference<String> iconMimeType = new AtomicReference<>();

    // every mocked bundle is an incarnation of its own, as every installed bundle is
    private final AtomicLong bundleIds = new AtomicLong();

    @BeforeEach
    void setup()
    {
        RunModeSupport.set( RunMode.PROD );
        resourceTestHelper = new ResourceTestHelper( this );

        nodeService = Mockito.mock( NodeService.class );
        repoService = Mockito.mock( ApplicationRepoService.class );
        auditLogSupport = Mockito.mock( DynamicSchemaAuditLogSupport.class );
        eventPublisher = Mockito.mock( EventPublisher.class );
        installed = new InstalledBundles( nodeService );
        resourceService = new ResourceServiceImpl( installed );
        // the running system, whose version the system version range of a descriptor is checked against
        final Bundle systemBundle = Mockito.mock( Bundle.class );
        when( systemBundle.getVersion() ).thenReturn( Version.valueOf( "8.1.0" ) );
        final BundleContext bundleContext = Mockito.mock( BundleContext.class );
        when( bundleContext.getBundle() ).thenReturn( systemBundle );
        appDescriptorService =
            new ApplicationDescriptorServiceImpl( bundleContext, resourceService, installed, repoService, auditLogSupport,
                                                  eventPublisher );
    }

    @AfterEach
    void tearDown()
    {
        RunModeSupport.set( RunMode.PROD );
    }

    @Test
    void get_reads_the_persisted_descriptor_of_a_global_application_never_the_bundle()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.myapp" );
        installed.install( mockAppBundle( key.getName() ) );
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.DESCRIPTOR, "kind: \"Application\"\ndescription: \"From node\"\n" );
        when( nodeService.getByPath( ApplicationRepoServiceImpl.applicationNodePath( key ) ) ).thenReturn(
            Node.create().id( NodeId.from( "app" ) ).name( key.getName() ).parentPath( ApplicationRepoServiceImpl.APPLICATIONS_ROOT ).data( data ).build() );

        final ApplicationDescriptor descriptor = appDescriptorService.get( key );
        assertNotNull( descriptor );
        assertEquals( "From node", descriptor.getDescription() );

        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.nonapp" ) ) );
    }

    @Test
    void get_follows_the_bundle_of_the_application()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.newapp" );
        final Bundle bundle = mockAppBundle( key.getName() );

        assertNull( appDescriptorService.get( key ) );

        installed.install( bundle );
        assertNotNull( appDescriptorService.get( key ) );
        assertEquals( key, appDescriptorService.get( key ).getKey() );

        installed.uninstall( bundle );
        assertNull( appDescriptorService.get( key ) );

        installed.install( bundle );
        assertNotNull( appDescriptorService.get( key ) );
    }

    @Test
    void app_without_descriptor_file()
    {
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Bundle-Type", "application" );
        final Bundle appBundleNoDescriptor = Mockito.mock( Bundle.class );
        Mockito.when( appBundleNoDescriptor.getSymbolicName() ).thenReturn( "com.enonic.nodescriptor" );
        Mockito.when( appBundleNoDescriptor.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( appBundleNoDescriptor.getHeaders() ).thenReturn( headers );
        installed.install( appBundleNoDescriptor );

        final ApplicationDescriptor descriptor = appDescriptorService.get( ApplicationKey.from( "com.enonic.nodescriptor" ) );
        assertNotNull( descriptor );
        assertEquals( ApplicationKey.from( "com.enonic.nodescriptor" ), descriptor.getKey() );
        assertNull( descriptor.getIcon() );
    }

    @Test
    void get_prefers_enonic_yaml_over_yml_over_the_legacy_application_descriptor()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.localapp" );
        final Bundle bundle = mockLocalBundle( key, "application.yml", "application.yaml", "enonic.yml", "enonic.yaml" );
        installed.install( bundle );
        assertEquals( "My app description YAML", appDescriptorService.get( key ).getDescription() );

        installed.install( mockLocalBundle( key, "application.yml", "application.yaml", "enonic.yml" ) );
        assertEquals( "My app description", appDescriptorService.get( key ).getDescription() );

        installed.install( mockLocalBundle( key, "application.yml", "application.yaml" ) );
        assertEquals( "My app description yaml", appDescriptorService.get( key ).getDescription() );

        installed.install( mockLocalBundle( key, "application.yml" ) );
        assertEquals( "My app description", appDescriptorService.get( key ).getDescription() );
    }

    @Test
    void get_reads_the_persisted_descriptor_of_an_application_without_bundle()
    {
        stubDescriptorNode( "kind: \"Application\"\ndescription: \"From node\"\n", null );

        final ApplicationDescriptor descriptor = appDescriptorService.get( MYAPP );

        assertNotNull( descriptor );
        assertEquals( "From node", descriptor.getDescription() );
        assertNull( descriptor.getIcon() );
        // cached for as long as the version of the application node holds
        assertSame( descriptor, appDescriptorService.get( MYAPP ) );
    }

    @Test
    void get_returns_null_for_an_unknown_application()
    {
        assertNull( appDescriptorService.get( MYAPP ) );
    }

    @Test
    void get_returns_null_for_the_reserved_application_keys()
    {
        // the resources of the reserved keys are served by the system application, its descriptor is not theirs
        installed.install( mockAppBundle( ApplicationHelper.SYSTEM_APPLICATION_KEY.getName() ) );
        assertNotNull( appDescriptorService.get( ApplicationHelper.SYSTEM_APPLICATION_KEY ) );

        for ( final ApplicationKey key : ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS )
        {
            assertNull( appDescriptorService.get( key ), key.toString() );
        }
    }

    @Test
    void get_sees_a_descriptor_written_on_another_node_of_the_cluster()
    {
        final AtomicReference<String> persisted = new AtomicReference<>( "kind: \"Application\"\ndescription: \"First\"\n" );
        stubDescriptorNodeFrom( persisted, null );

        final ApplicationDescriptor first = appDescriptorService.get( MYAPP );
        assertEquals( "First", first.getDescription() );

        // nothing is re-read while the version of the application node holds
        persisted.set( "kind: \"Application\"\ndescription: \"Second\"\n" );
        assertSame( first, appDescriptorService.get( MYAPP ) );

        // the write moved the version: the next read sees it, no event needed
        version.incrementAndGet();
        final ApplicationDescriptor second = appDescriptorService.get( MYAPP );
        assertEquals( "Second", second.getDescription() );
        assertSame( second, appDescriptorService.get( MYAPP ) );

        // the icon lives on the same node: a change of it is seen the same way
        iconMimeType.set( "image/svg+xml" );
        version.incrementAndGet();
        assertEquals( "image/svg+xml", appDescriptorService.get( MYAPP ).getIcon().getMimeType() );

        persisted.set( null );
        version.incrementAndGet();
        assertNull( appDescriptorService.get( MYAPP ) );
    }

    @Test
    void get_reads_an_application_in_development_on_every_call()
        throws Exception
    {
        RunModeSupport.set( RunMode.DEV );
        final ApplicationKey key = ApplicationKey.from( "com.enonic.devapp" );
        final Path sources = Files.createDirectory( temporaryFolder.resolve( "devapp" ) );
        Files.writeString( sources.resolve( "enonic.yaml" ), "kind: \"Application\"\ndescription: \"Draft\"\n" );

        final Bundle bundle = mockLocalBundle( key );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( ApplicationManifestConstants.X_SOURCE_PATHS, sources.toString() );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        installed.install( bundle );

        final ApplicationDescriptor draft = appDescriptorService.get( key );
        assertEquals( "Draft", draft.getDescription() );
        assertNotSame( draft, appDescriptorService.get( key ) );

        Files.writeString( sources.resolve( "enonic.yaml" ), "kind: \"Application\"\ndescription: \"Edited\"\n" );
        assertEquals( "Edited", appDescriptorService.get( key ).getDescription() );
    }

    @Test
    void get_returns_null_for_a_descriptor_that_cannot_be_built()
        throws Exception
    {
        RunModeSupport.set( RunMode.DEV );
        final ApplicationKey key = ApplicationKey.from( "com.enonic.brokenapp" );
        final Path sources = Files.createDirectory( temporaryFolder.resolve( "brokenapp" ) );
        Files.writeString( sources.resolve( "enonic.yaml" ), "kind: \"Application\"\ndescription: \"Draft\n" );

        final Bundle bundle = mockLocalBundle( key );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( ApplicationManifestConstants.X_SOURCE_PATHS, sources.toString() );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        installed.install( bundle );

        // a half-written descriptor is logged, not thrown: the application has no descriptor until it is fixed
        assertNull( appDescriptorService.get( key ) );

        Files.writeString( sources.resolve( "enonic.yaml" ), "kind: \"Application\"\ndescription: \"Fixed\"\n" );
        assertEquals( "Fixed", appDescriptorService.get( key ).getDescription() );

        // a descriptor naming another application is rejected the same way
        Files.writeString( sources.resolve( "enonic.yaml" ), "kind: \"Application\"\nname: \"com.enonic.otherapp\"\n" );
        assertNull( appDescriptorService.get( key ) );
    }

    @Test
    void get_retries_a_descriptor_whose_read_failed_without_a_change_of_version()
    {
        final AtomicReference<String> persisted = new AtomicReference<>( "kind: \"Application\"\ndescription: \"Persisted\"\n" );
        stubDescriptorNodeFrom( persisted, "image/svg+xml" );
        // the binary of the icon is out of reach once, the node and its version are untouched
        when( nodeService.getBinary( any( NodeId.class ), eq( BinaryReference.from( "enonic.svg" ) ) ) )
            .thenThrow( new RuntimeException( "blob store unavailable" ) )
            .thenReturn( ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        // the failure is logged and the application has no descriptor for now
        assertNull( appDescriptorService.get( MYAPP ) );

        // nothing was cached for the failure: the next read succeeds under the same version
        final ApplicationDescriptor descriptor = appDescriptorService.get( MYAPP );
        assertNotNull( descriptor );
        assertEquals( "Persisted", descriptor.getDescription() );
        assertEquals( "image/svg+xml", descriptor.getIcon().getMimeType() );
        assertSame( descriptor, appDescriptorService.get( MYAPP ) );
    }

    @Test
    void get_reads_the_descriptor_of_a_stopped_local_application_from_its_bundle()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.localapp" );
        final Bundle bundle = mockLocalBundle( key, "enonic.yaml", "enonic.svg" );
        installed.install( bundle );
        installed.stop( bundle );

        final ApplicationDescriptor descriptor = appDescriptorService.get( key );
        assertNotNull( descriptor );
        assertEquals( "My app description YAML", descriptor.getDescription() );
        assertEquals( "image/svg+xml", descriptor.getIcon().getMimeType() );
    }

    @Test
    void createOrUpdate_creates_the_application_and_descriptor()
    {
        final AtomicReference<String> persisted = stubUpsert();
        stubDescriptorNodeFrom( persisted, null );

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> {
            assertEquals( MYAPP, edit.source.getKey() );
            assertNull( edit.title );
            edit.title = "My App";
            edit.vendorName = "Enonic";
            edit.schemaConfig = GenericValue.newObject().put( "flag", true ).build();
        } ) ) );

        verify( repoService ).createApplicationNode( MYAPP );
        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( IconUpdate.KEEP ) );
        assertEquals( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"My App\"\nvendorName: \"Enonic\"\nconfig:\n  flag: true\n",
                      persisted.get() );

        assertEquals( "My App", result.getTitle() );
        assertEquals( "Enonic", result.getVendorName() );
        assertTrue( result.getSchemaConfig().property( "flag" ).asBoolean() );
        assertSame( result, appDescriptorService.get( MYAPP ) );

        verify( auditLogSupport ).createApplicationDescriptor( MYAPP, persisted.get(), null, 0 );
        verify( auditLogSupport, never() ).updateApplicationDescriptor( any(), any(), any(), Mockito.anyLong(), Mockito.anyBoolean() );

        // the application is listed from now on: the listeners of application events are told
        final ArgumentCaptor<Event> event = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( event.capture() );
        assertEquals( ApplicationEvents.EVENT_TYPE, event.getValue().getType() );
        assertEquals( ApplicationEvents.INSTALLED, event.getValue().getData().get( ApplicationEvents.EVENT_TYPE_KEY ) );
        assertEquals( MYAPP.toString(), String.valueOf( event.getValue().getData().get( ApplicationEvents.APPLICATION_KEY_KEY ) ) );
    }

    @Test
    void createOrUpdate_rejects_system_applications()
    {
        // an installed bundle of the system type is told by its manifest
        final Bundle systemBundle = mockAppBundle( "com.enonic.systembundle" );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Bundle-Type", "system" );
        Mockito.when( systemBundle.getHeaders() ).thenReturn( headers );
        installed.install( systemBundle );

        for ( final ApplicationKey key : List.of( ApplicationKey.BASE, ApplicationKey.PORTAL, ApplicationKey.MEDIA_MOD, ApplicationKey.SYSTEM,
                                                  ApplicationKey.from( "com.enonic.xp.app.system" ),
                                                  ApplicationKey.from( "com.enonic.systembundle" ) ) )
        {
            final CreateOrUpdateApplicationDescriptorParams params =
                CreateOrUpdateApplicationDescriptorParams.create().key( key ).editor( edit -> edit.title = "Hijacked" ).build();
            assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params ) ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "Cannot modify system application: " + key );

            // the system check comes after the role check
            assertThatThrownBy( () -> appDescriptorService.createOrUpdate( params ) ).isInstanceOf( ForbiddenAccessException.class );
        }

        // nothing is created or written for any of them
        verify( repoService, never() ).createApplicationNode( any() );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void createOrUpdate_rejects_local_applications()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.localapp" );
        final Bundle localBundle = mockLocalBundle( key, APP_DESCRIPTOR_FILENAME );
        installed.install( localBundle );

        final CreateOrUpdateApplicationDescriptorParams params =
            CreateOrUpdateApplicationDescriptorParams.create().key( key ).editor( edit -> edit.title = "Hijacked" ).build();
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "Cannot modify local application: " + key );

        // stopped or not
        installed.stop( localBundle );
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "Cannot modify local application: " + key );

        assertThatThrownBy( () -> appDescriptorService.createOrUpdate( params ) ).isInstanceOf( ForbiddenAccessException.class );

        verify( repoService, never() ).createApplicationNode( any() );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void createOrUpdate_rejects_applications_built_with_gradle()
    {
        // the XP gradle plugin builds the manifest with Bnd, which stamps it
        for ( final Map.Entry<String, String> header : Map.of( "Bnd-LastModified", "1783360499694", "Tool", "Bnd-7.3.0.202606021345" )
            .entrySet() )
        {
            final ApplicationKey key = ApplicationKey.from( "com.enonic.gradleapp" );
            final Bundle gradleBundle = mockAppBundle( key.getName() );
            final Hashtable<String, String> headers = new Hashtable<>();
            headers.put( header.getKey(), header.getValue() );
            Mockito.when( gradleBundle.getHeaders() ).thenReturn( headers );
            installed.install( gradleBundle );

            final CreateOrUpdateApplicationDescriptorParams params =
                CreateOrUpdateApplicationDescriptorParams.create().key( key ).editor( edit -> edit.title = "Hijacked" ).build();
            assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params ) ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "Cannot modify application built with Gradle: " + key );

            assertThatThrownBy( () -> appDescriptorService.createOrUpdate( params ) ).isInstanceOf( ForbiddenAccessException.class );
        }

        verify( repoService, never() ).createApplicationNode( any() );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
        verify( eventPublisher, never() ).publish( any() );
    }

    @Test
    void createOrUpdate_sets_locked_alone_for_an_application_built_with_gradle()
    {
        final Bundle gradleBundle = mockAppBundle( MYAPP.getName() );
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "Bnd-LastModified", "1783360499694" );
        Mockito.when( gradleBundle.getHeaders() ).thenReturn( headers );
        installed.install( gradleBundle );

        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\ntitle: \"Built\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        // an edit is refused, with or without the flag: nothing is written, the flag included
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).editor( edit -> edit.title = "Hijacked" ).locked( true ).build() ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "Cannot modify application built with Gradle: myapp" );
        verify( repoService, never() ).updateLocked( any(), Mockito.anyBoolean() );

        // the flag alone is set on the node, the descriptor is left as built
        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate(
            CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).locked( true ).build() ) );

        verify( repoService ).updateLocked( MYAPP, true );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
        verify( repoService, never() ).createApplicationNode( any() );
        assertEquals( "Built", result.getTitle() );
    }

    @Test
    void createOrUpdate_keeps_the_version()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\nversion: \"1.0.0-SNAPSHOT\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        // the version is declared by the descriptor the application ships: it is not editable, an edit leaves it as it is
        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> {
            assertEquals( "1.0.0.SNAPSHOT", edit.source.getVersion() );
            edit.title = "New";
        } ) ) );
        assertEquals( "kind: \"Application\"\nname: \"myapp\"\nversion: \"1.0.0-SNAPSHOT\"\ntitle: \"New\"\n", persisted.get() );
        assertEquals( "1.0.0.SNAPSHOT", result.getVersion() );
    }

    @Test
    void createOrUpdate_checks_the_system_version_range()
    {
        final AtomicReference<String> persisted = stubUpsert();
        stubDescriptorNodeFrom( persisted, null );

        // not a version
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.minSystemVersion = "abc" ) ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageStartingWith( "Invalid minSystemVersion in application descriptor" );
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.maxSystemVersion = "9.x" ) ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageStartingWith( "Invalid maxSystemVersion in application descriptor" );

        // the running system is 8.1.0: the minimum is inclusive, the maximum exclusive
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.minSystemVersion = "8.2.0" ) ) ) )
            .isInstanceOf( ApplicationInvalidVersionException.class );
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.maxSystemVersion = "8.1.0" ) ) ) )
            .isInstanceOf( ApplicationInvalidVersionException.class );

        verify( repoService, never() ).createApplicationNode( any() );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );

        // written as a semantic version or as a bundle version: the file declares semantic versions, the descriptor reports
        // bundle versions
        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> {
            edit.minSystemVersion = "8.0.0-RC1";
            edit.maxSystemVersion = "9.0.0.RC1";
        } ) ) );

        assertEquals( "kind: \"Application\"\nname: \"myapp\"\nminSystemVersion: \"8.0.0-RC1\"\nmaxSystemVersion: \"9.0.0-RC1\"\n",
                      persisted.get() );
        assertEquals( "8.0.0.RC1", result.getMinSystemVersion() );
        assertEquals( "9.0.0.RC1", result.getMaxSystemVersion() );
    }

    @Test
    void createOrUpdate_sets_locked_on_the_node_leaving_the_descriptor()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\ntitle: \"Old\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate(
            CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).locked( true ).build() ) );

        // the flag goes to the application node, the descriptor is neither written nor told to have changed
        verify( repoService ).updateLocked( MYAPP, true );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
        verify( auditLogSupport ).updateLocked( MYAPP, true );
        verify( auditLogSupport, never() ).updateApplicationDescriptor( any(), any(), any(), Mockito.anyLong(), Mockito.anyBoolean() );
        assertEquals( "Old", result.getTitle() );
        assertEquals( "kind: \"Application\"\ntitle: \"Old\"\n", persisted.get() );
    }

    @Test
    void createOrUpdate_resets_locked_along_with_an_edit()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\ntitle: \"Old\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        asAdmin( () -> appDescriptorService.createOrUpdate(
            CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).editor( edit -> edit.title = "New" ).locked( false ).build() ) );

        verify( repoService ).updateLocked( MYAPP, false );
        // never in the descriptor
        assertEquals( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"New\"\n", persisted.get() );
    }

    @Test
    void createOrUpdate_leaves_locked_as_is_when_not_given()
    {
        final AtomicReference<String> persisted = stubUpsert();
        stubDescriptorNodeFrom( persisted, null );

        asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.title = "New" ) ) );

        verify( repoService, never() ).updateLocked( any(), Mockito.anyBoolean() );
    }

    @Test
    void params_require_an_editor_or_the_flag()
    {
        assertThatThrownBy( () -> CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "editor or locked is required" );
    }

    @Test
    void createOrUpdate_keeps_untouched_fields_and_clears_nulls()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\ntitle: \"Old\"\ndescription: {text: \"Desc\", i18n: \"app.desc\"}\nvendorUrl: \"https://old\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> {
            assertEquals( "Old", edit.title );
            assertEquals( "app.desc", edit.descriptionI18nKey );
            edit.title = "New";
            edit.vendorUrl = null;
        } ) ) );

        verify( repoService, never() ).createApplicationNode( any() );
        assertEquals( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"New\"\ndescription:\n  text: \"Desc\"\n  i18n: \"app.desc\"\n",
                      persisted.get() );
        assertEquals( "New", result.getTitle() );
        assertEquals( "Desc", result.getDescription() );
        assertEquals( "app.desc", result.getDescriptionI18nKey() );
        assertNull( result.getVendorUrl() );

        verify( auditLogSupport ).updateApplicationDescriptor( MYAPP, persisted.get(), null, 0, false );
        // the application stays listed, whoever caches its descriptor is told that it changed
        final ArgumentCaptor<Event> event = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher ).publish( event.capture() );
        assertEquals( ApplicationEvents.UPDATED, event.getValue().getData().get( ApplicationEvents.EVENT_TYPE_KEY ) );
        assertEquals( ApplicationEvents.KIND_APPLICATION, event.getValue().getData().get( ApplicationEvents.KIND_KEY ) );
        assertEquals( ApplicationEvents.ACTION_UPDATED, event.getValue().getData().get( ApplicationEvents.ACTION_KEY ) );
        assertNull( event.getValue().getData().get( ApplicationEvents.NAME_KEY ) );
        assertTrue( event.getValue().isDistributed() );
    }

    @Test
    void createOrUpdate_serves_the_written_descriptor_from_a_cache_filled_before_the_write()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\ntitle: \"Old\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        final ApplicationDescriptor before = appDescriptorService.get( MYAPP );
        assertEquals( "Old", before.getTitle() );
        assertSame( before, appDescriptorService.get( MYAPP ) );

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.title = "New" ) ) );

        assertEquals( "New", result.getTitle() );
        assertSame( result, appDescriptorService.get( MYAPP ) );
    }

    @Test
    void createOrUpdate_edits_the_persisted_descriptor_whatever_the_resource_source_of_the_caller()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();
        // a global application: the bundle ships a descriptor of its own, the persisted one is what every reader sees
        installed.install( mockAppBundle( MYAPP.getName() ) );

        asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.title = "Renamed" ) ) );

        // a caller reading the bundle alone still edits the persisted descriptor, and gets the persisted one back
        final Context bundleSource = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.SCHEMA_ADMIN ).user( User.anonymous() ).build() )
            .attribute( "resourceSource", "bundle" )
            .build();
        final ApplicationDescriptor result = bundleSource.callWith( () -> appDescriptorService.createOrUpdate( params( edit -> {
            assertEquals( "Renamed", edit.title );
            edit.description = "Updated";
        } ) ) );

        assertEquals( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"Renamed\"\ndescription: \"Updated\"\n", persisted.get() );
        assertEquals( "Renamed", result.getTitle() );
        assertEquals( "Updated", result.getDescription() );

        // the bundle descriptor is what that caller reads, and it is served from the bundle still
        assertEquals( "My app description", bundleSource.callWith( () -> appDescriptorService.get( MYAPP ) ).getDescription() );
        assertEquals( "Updated", appDescriptorService.get( MYAPP ).getDescription() );
    }

    @Test
    void get_caches_the_descriptor_of_an_application_without_descriptor_file()
    {
        final Hashtable<String, String> headers = new Hashtable<>();
        headers.put( "X-Bundle-Type", "system" );
        final Bundle bundle = mockLocalBundle( ApplicationKey.from( "com.enonic.nodescriptor" ) );
        Mockito.when( bundle.getHeaders() ).thenReturn( headers );
        installed.install( bundle );

        final ApplicationDescriptor descriptor = appDescriptorService.get( ApplicationKey.from( "com.enonic.nodescriptor" ) );
        assertNotNull( descriptor );

        // the descriptor paths and the icon are not looked for again while the bundle holds
        Mockito.clearInvocations( bundle );
        assertSame( descriptor, appDescriptorService.get( ApplicationKey.from( "com.enonic.nodescriptor" ) ) );
        verify( bundle, never() ).getResource( anyString() );
    }

    @Test
    void get_selects_the_descriptor_path_under_the_stamp_of_the_bundle()
    {
        final ApplicationKey key = ApplicationKey.from( "com.enonic.localapp" );
        installed.install( mockLocalBundle( key, "application.yml" ) );
        assertEquals( "My app description", appDescriptorService.get( key ).getDescription() );

        // the bundle replaced by one shipping a descriptor of higher precedence next to the old one: the new one is read
        installed.install( mockLocalBundle( key, "application.yml", "enonic.yaml" ) );
        assertEquals( "My app description YAML", appDescriptorService.get( key ).getDescription() );
    }

    @Test
    void createOrUpdate_keeps_the_icon_left_as_is()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\n" );
        stubDescriptorNodeFrom( persisted, "image/svg+xml" );
        stubApplicationNode();

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> {
            assertNotNull( edit.icon );
            edit.title = "Titled";
        } ) ) );

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( IconUpdate.KEEP ) );
        assertEquals( "image/svg+xml", result.getIcon().getMimeType() );
        verify( auditLogSupport ).updateApplicationDescriptor( MYAPP, persisted.get(), null, 0, false );
    }

    @Test
    void createOrUpdate_removes_the_icon()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\n" );
        stubDescriptorNodeFrom( persisted, "image/svg+xml" );
        stubApplicationNode();

        final ApplicationDescriptor result = asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.icon = null ) ) );

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( IconUpdate.REMOVE ) );
        verify( auditLogSupport ).updateApplicationDescriptor( MYAPP, persisted.get(), null, 0, true );
        assertNull( result.getIcon() );
        assertNull( appDescriptorService.get( MYAPP ).getIcon() );
    }

    @Test
    void createOrUpdate_without_icon_removing_nothing_keeps()
    {
        final AtomicReference<String> persisted = stubUpsert();
        persisted.set( "kind: \"Application\"\n" );
        stubDescriptorNodeFrom( persisted, null );
        stubApplicationNode();

        asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.icon = null ) ) );

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( IconUpdate.KEEP ) );
    }

    @Test
    void createOrUpdate_replaces_the_icon()
    {
        final AtomicReference<String> persisted = stubUpsert();
        stubDescriptorNodeFrom( persisted, "image/png" );

        final byte[] png = {1, 2, 3};
        asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.icon = Icon.from( png, "image/png", Instant.now() ) ) ) );

        final ArgumentCaptor<IconUpdate> captor = ArgumentCaptor.forClass( IconUpdate.class );
        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), captor.capture() );
        final IconUpdate.Replace replace = (IconUpdate.Replace) captor.getValue();
        assertEquals( "image/png", replace.mimeType() );
        assertEquals( List.of( (byte) 1, (byte) 2, (byte) 3 ), bytes( replace.data() ) );

        verify( auditLogSupport ).createApplicationDescriptor( MYAPP, persisted.get(), "image/png", 3 );
    }

    @Test
    void createOrUpdate_rejects_an_unsupported_icon()
    {
        stubUpsert();

        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[]{1}, "image/jpeg", Instant.now() ) ) ) ) )
            .isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[0], "image/png", Instant.now() ) ) ) ) )
            .isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[ApplicationDescriptorServiceImpl.MAX_ICON_SIZE + 1], "image/png",
                                                   Instant.now() ) ) ) ) )
            .isInstanceOf( IllegalArgumentException.class );

        verify( repoService, never() ).createApplicationNode( any() );
        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
    }

    @Test
    void createOrUpdate_requires_the_admin_role()
    {
        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.EVERYONE ).user( User.anonymous() ).build() )
            .build();

        assertThatThrownBy( () -> context.runWith( () -> appDescriptorService.createOrUpdate( params( edit -> edit.title = "x" ) ) ) )
            .isInstanceOf( ForbiddenAccessException.class );

        verify( repoService, never() ).createApplicationNode( any() );
    }

    private static CreateOrUpdateApplicationDescriptorParams params( final ApplicationDescriptorEditor editor )
    {
        return CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).editor( editor ).build();
    }

    private static <T> T asAdmin( final Callable<T> callable )
    {
        return ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.SCHEMA_ADMIN ).user( User.anonymous() ).build() )
            .build()
            .callWith( callable );
    }

    private void stubApplicationNode()
    {
        when( repoService.getApplicationNode( MYAPP ) ).thenReturn(
            Node.create().id( new NodeId() ).name( "myapp" ).parentPath( ApplicationRepoServiceImpl.APPLICATIONS_ROOT ).build() );
    }

    /**
     * The repo service stores the descriptor written by the service, applies the icon update and moves the version of the node.
     */
    private AtomicReference<String> stubUpsert()
    {
        final AtomicReference<String> persisted = new AtomicReference<>();
        when( repoService.upsertApplicationDescriptor( eq( MYAPP ), anyString(), any() ) ).thenAnswer( invocation -> {
            persisted.set( invocation.getArgument( 1 ) );
            final IconUpdate iconUpdate = invocation.getArgument( 2 );
            if ( iconUpdate instanceof IconUpdate.Remove )
            {
                iconMimeType.set( null );
            }
            else if ( iconUpdate instanceof IconUpdate.Replace replace )
            {
                iconMimeType.set( replace.mimeType() );
            }
            version.incrementAndGet();
            return descriptorNode( persisted.get(), iconMimeType.get() );
        } );
        return persisted;
    }

    private void stubDescriptorNode( final String yaml, final String iconMimeType )
    {
        stubDescriptorNodeFrom( new AtomicReference<>( yaml ), iconMimeType );
    }

    /**
     * The descriptor node holds whatever the reference holds at read time, at the version the test holds.
     */
    private void stubDescriptorNodeFrom( final AtomicReference<String> yaml, final String initialIconMimeType )
    {
        this.iconMimeType.set( initialIconMimeType );
        when( nodeService.getByPath( ApplicationRepoServiceImpl.applicationNodePath( MYAPP ) ) ).thenAnswer(
            invocation -> yaml.get() != null ? descriptorNode( yaml.get(), iconMimeType.get() ) : null );
        // the icon binary is attached under its file name
        when( nodeService.getBinary( any( NodeId.class ), eq( BinaryReference.from( "enonic.svg" ) ) ) ).thenReturn(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );
        when( nodeService.getBinary( any( NodeId.class ), eq( BinaryReference.from( "enonic.png" ) ) ) ).thenReturn(
            ByteSource.wrap( new byte[]{1, 2, 3} ) );
    }

    private Node descriptorNode( final String yaml, final String iconMimeType )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.DESCRIPTOR, yaml );
        if ( iconMimeType != null )
        {
            data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourcePaths.appIconBinaryReference( iconMimeType ) );
        }
        return Node.create()
            .id( NodeId.from( "app" ) )
            .name( "myapp" )
            .parentPath( ApplicationRepoServiceImpl.APPLICATIONS_ROOT )
            .data( data )
            .nodeVersionId( NodeVersionId.from( "v" + version.get() ) )
            .timestamp( Instant.parse( "2026-10-08T10:00:00Z" ) )
            .build();
    }

    private static List<Byte> bytes( final ByteSource source )
    {
        try
        {
            final byte[] read = source.read();
            final List<Byte> result = new java.util.ArrayList<>();
            for ( final byte b : read )
            {
                result.add( b );
            }
            return result;
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private Bundle mockAppBundle( final String symbolicName )
    {
        return mockBundle( symbolicName, APP_DESCRIPTOR_FILENAME );
    }

    private Bundle mockLocalBundle( final ApplicationKey key, final String... fileNames )
    {
        final Bundle bundle = mockBundle( key.getName(), fileNames );
        Mockito.when( bundle.getLocation() ).thenReturn( ApplicationHelper.toBundleLocation( key, true ) );
        return bundle;
    }

    /**
     * A bundle shipping the given test files under their own names in the application root.
     */
    private Bundle mockBundle( final String symbolicName, final String... fileNames )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( symbolicName );
        Mockito.when( bundle.getBundleId() ).thenReturn( bundleIds.incrementAndGet() );
        for ( final String fileName : fileNames )
        {
            final URL resource = resourceTestHelper.getTestResource( fileName );
            // getEntry marks the bundle as an application, getResource serves it through the bundle url resolver
            Mockito.when( bundle.getEntry( fileName ) ).thenReturn( resource );
            Mockito.when( bundle.getResource( "/" + fileName ) ).thenReturn( resource );
        }
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        return bundle;
    }

    /**
     * The installed bundles, as the factory service tracks them: the resolver of the running application for an active bundle,
     * the resolver of an inactive one for a stopped bundle, the persisted descriptor of an application without a bundle.
     */
    private static final class InstalledBundles
        implements ApplicationFactoryService
    {
        private final Map<ApplicationKey, Bundle> bundles = new HashMap<>();

        private final Set<ApplicationKey> stopped = new HashSet<>();

        private final ApplicationFactory factory;

        private final NodeService nodeService;

        InstalledBundles( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            this.factory = new ApplicationFactory( nodeService );
        }

        void install( final Bundle bundle )
        {
            final ApplicationKey key = ApplicationHelper.getApplicationKey( bundle );
            bundles.put( key, bundle );
            stopped.remove( key );
        }

        void stop( final Bundle bundle )
        {
            stopped.add( ApplicationHelper.getApplicationKey( bundle ) );
        }

        void uninstall( final Bundle bundle )
        {
            bundles.remove( ApplicationHelper.getApplicationKey( bundle ) );
        }

        @Override
        public ApplicationAdaptor getApplication( final Bundle bundle )
        {
            return factory.create( bundle );
        }

        @Override
        public Optional<ApplicationAdaptor> findActiveApplication( final ApplicationKey applicationKey )
        {
            return Optional.empty();
        }

        @Override
        public Optional<Bundle> findBundle( final ApplicationKey applicationKey )
        {
            return Optional.ofNullable( bundles.get( applicationKey ) );
        }

        @Override
        public ResourceSource findResourceSource( final ApplicationKey applicationKey, final String resolverSource )
        {
            final Bundle bundle = bundles.get( applicationKey );
            if ( bundle != null && !stopped.contains( applicationKey ) )
            {
                return new ResourceSource( bundle, factory.createUrlResolver( bundle, resolverSource ) );
            }
            if ( bundle != null )
            {
                return new ResourceSource( null, factory.createInactiveUrlResolver( bundle ) );
            }
            final Node node = nodeService.getByPath( ApplicationRepoServiceImpl.applicationNodePath( applicationKey ) );
            return node != null && node.data().getString( SchemaNodePropertyNames.DESCRIPTOR ) != null
                ? new ResourceSource( null, ApplicationFactory.createPersistedSchemaResolver( applicationKey, nodeService ) )
                : ResourceSource.NONE;
        }
    }
}
