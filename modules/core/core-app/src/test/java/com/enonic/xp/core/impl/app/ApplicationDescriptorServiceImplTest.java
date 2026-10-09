package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorEditor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateOrUpdateApplicationDescriptorParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.support.ResourceTestHelper;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private ResourceTestHelper resourceTestHelper;

    private ApplicationDescriptorServiceImpl appDescriptorService;

    private ComponentContext componentContext;

    private NodeService nodeService;

    private ApplicationRepoService repoService;

    private DynamicSchemaAuditLogSupport auditLogSupport;

    @BeforeEach
    void setup()
    {
        RunModeSupport.set( RunMode.PROD );
        resourceTestHelper = new ResourceTestHelper( this );

        final Bundle appBundle = mockAppBundle( "com.enonic.myapp" );
        final Bundle nonAppBundle = mockNonAppBundle( "com.enonic.nonapp" );

        BundleContext bundleContext = Mockito.mock( BundleContext.class );
        Mockito.when( bundleContext.getBundles() ).thenReturn( new Bundle[]{appBundle, nonAppBundle} );

        componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( bundleContext );

        nodeService = Mockito.mock( NodeService.class );
        repoService = Mockito.mock( ApplicationRepoService.class );
        auditLogSupport = Mockito.mock( DynamicSchemaAuditLogSupport.class );
        appDescriptorService = new ApplicationDescriptorServiceImpl( nodeService, repoService, auditLogSupport );
    }

    @Test
    void start()
    {
        appDescriptorService.start( componentContext );

        final ApplicationDescriptor descriptor = appDescriptorService.get( ApplicationKey.from( "com.enonic.myapp" ) );
        assertNotNull( descriptor );
        assertEquals( "My app description", descriptor.getDescription() );

        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.nonapp" ) ) );
    }

    @Test
    void bundle_lifecycle()
    {
        final Bundle bundle = mockAppBundle( "com.enonic.newapp" );

        appDescriptorService.start( componentContext );
        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, bundle ) );
        assertNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );

        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.UPDATED, bundle ) );
        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.newapp" ) ) );
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

        appDescriptorService.start( componentContext );
        appDescriptorService.bundleChanged( new BundleEvent( BundleEvent.INSTALLED, appBundleNoDescriptor ) );

        assertNotNull( appDescriptorService.get( ApplicationKey.from( "com.enonic.nodescriptor" ) ) );
    }

    @Test
    void get_reads_the_persisted_descriptor_of_an_application_without_bundle()
    {
        stubDescriptorNode( "kind: \"Application\"\ndescription: \"From node\"\n", null );

        final ApplicationDescriptor descriptor = appDescriptorService.get( MYAPP );

        assertNotNull( descriptor );
        assertEquals( "From node", descriptor.getDescription() );
        assertNull( descriptor.getIcon() );
        // cached from now on
        assertSame( descriptor, appDescriptorService.get( MYAPP ) );
    }

    @Test
    void get_returns_null_for_an_unknown_application()
    {
        assertNull( appDescriptorService.get( MYAPP ) );
        verify( repoService ).getApplicationDescriptorNode( MYAPP );
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
        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( ApplicationIconUpdate.KEEP ) );
        assertEquals( "kind: \"Application\"\nname: \"myapp\"\ntitle: \"My App\"\nvendorName: \"Enonic\"\nconfig:\n  flag: true\n",
                      persisted.get() );

        assertEquals( "My App", result.getTitle() );
        assertEquals( "Enonic", result.getVendorName() );
        assertTrue( result.getSchemaConfig().property( "flag" ).asBoolean() );
        assertSame( result, appDescriptorService.get( MYAPP ) );

        verify( auditLogSupport ).createApplicationDescriptor( MYAPP, persisted.get(), null, 0 );
        verify( auditLogSupport, never() ).updateApplicationDescriptor( any(), any(), any(), Mockito.anyLong(), Mockito.anyBoolean() );
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

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( ApplicationIconUpdate.KEEP ) );
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

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( ApplicationIconUpdate.REMOVE ) );
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

        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), eq( ApplicationIconUpdate.KEEP ) );
    }

    @Test
    void createOrUpdate_replaces_the_icon()
    {
        final AtomicReference<String> persisted = stubUpsert();
        stubDescriptorNodeFrom( persisted, "image/png" );

        final byte[] png = {1, 2, 3};
        asAdmin( () -> appDescriptorService.createOrUpdate( params( edit -> edit.icon = Icon.from( png, "image/png", Instant.now() ) ) ) );

        final ArgumentCaptor<ApplicationIconUpdate> captor = ArgumentCaptor.forClass( ApplicationIconUpdate.class );
        verify( repoService ).upsertApplicationDescriptor( eq( MYAPP ), anyString(), captor.capture() );
        final ApplicationIconUpdate.Replace replace = (ApplicationIconUpdate.Replace) captor.getValue();
        assertEquals( "image/png", replace.mimeType() );
        assertEquals( List.of( (byte) 1, (byte) 2, (byte) 3 ), bytes( replace.data() ) );

        verify( auditLogSupport ).createApplicationDescriptor( MYAPP, persisted.get(), "image/png", 3 );
    }

    @Test
    void createOrUpdate_rejects_an_unsupported_icon()
    {
        stubUpsert();

        assertThrows( IllegalArgumentException.class, () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[]{1}, "image/jpeg", Instant.now() ) ) ) ) );
        assertThrows( IllegalArgumentException.class, () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[0], "image/png", Instant.now() ) ) ) ) );
        assertThrows( IllegalArgumentException.class, () -> asAdmin( () -> appDescriptorService.createOrUpdate(
            params( edit -> edit.icon = Icon.from( new byte[ApplicationDescriptorServiceImpl.MAX_ICON_SIZE + 1], "image/png",
                                                   Instant.now() ) ) ) ) );

        verify( repoService, never() ).upsertApplicationDescriptor( any(), any(), any() );
    }

    @Test
    void createOrUpdate_requires_the_admin_role()
    {
        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.EVERYONE ).user( User.anonymous() ).build() )
            .build();

        assertThrows( ForbiddenAccessException.class,
                      () -> context.runWith( () -> appDescriptorService.createOrUpdate( params( edit -> edit.title = "x" ) ) ) );

        verify( repoService, never() ).createApplicationNode( any() );
    }

    @Test
    void node_events_evict_the_descriptor()
    {
        final AtomicReference<String> persisted = new AtomicReference<>( "kind: \"Application\"\ndescription: \"First\"\n" );
        stubDescriptorNodeFrom( persisted, null );

        assertEquals( "First", appDescriptorService.get( MYAPP ).getDescription() );
        persisted.set( "kind: \"Application\"\ndescription: \"Second\"\n" );
        assertEquals( "First", appDescriptorService.get( MYAPP ).getDescription() );

        // an event for another application, another repo or an unrelated node changes nothing
        appDescriptorService.onEvent( nodeEvent( "node.updated", "system-repo", "master", "/applications/other/enonic.yaml" ) );
        appDescriptorService.onEvent( nodeEvent( "node.updated", "com.enonic.cms.default", "master", "/applications/myapp/enonic.yaml" ) );
        appDescriptorService.onEvent( nodeEvent( "node.updated", "system-repo", "master", "/applications/myapp/cms/cms.yaml" ) );
        appDescriptorService.onEvent( nodeEvent( "node.pushed", "system-repo", "master", "/applications/myapp/enonic.yaml" ) );
        assertEquals( "First", appDescriptorService.get( MYAPP ).getDescription() );

        // the descriptor node of the application, local or from another cluster node
        appDescriptorService.onEvent( nodeEvent( "node.updated", "system-repo", "master", "/applications/myapp/enonic.yaml" ) );
        assertEquals( "Second", appDescriptorService.get( MYAPP ).getDescription() );

        persisted.set( "kind: \"Application\"\ndescription: \"Third\"\n" );
        appDescriptorService.onEvent( Event.create( nodeEvent( "node.deleted", "system-repo", "master", "/applications/myapp" ) )
                                          .localOrigin( false )
                                          .build() );
        assertEquals( "Third", appDescriptorService.get( MYAPP ).getDescription() );

        // malformed events are ignored
        appDescriptorService.onEvent( Event.create( "node.created" ).value( "nodes", "garbage" ).build() );
        appDescriptorService.onEvent( Event.create( "node.created" ).build() );
    }

    private static Event nodeEvent( final String type, final String repo, final String branch, final String path )
    {
        return Event.create( type )
            .distributed( true )
            .value( "nodes", List.of( Map.of( "id", "id", "path", path, "branch", branch, "repo", repo ) ) )
            .build();
    }

    private static CreateOrUpdateApplicationDescriptorParams params( final ApplicationDescriptorEditor editor )
    {
        return CreateOrUpdateApplicationDescriptorParams.create().key( MYAPP ).editor( editor ).build();
    }

    private static <T> T asAdmin( final java.util.concurrent.Callable<T> callable )
    {
        return ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.SCHEMA_ADMIN ).user( User.anonymous() ).build() )
            .build()
            .callWith( callable );
    }

    private void stubApplicationNode()
    {
        when( repoService.getApplicationNode( MYAPP ) ).thenReturn(
            Node.create().id( new NodeId() ).name( "myapp" ).parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).build() );
    }

    // the mime type of the icon attached to the stubbed descriptor node, followed by the stubbed upsert
    private final AtomicReference<String> iconMimeType = new AtomicReference<>();

    /**
     * The repo service stores the descriptor written by the service and applies the icon update.
     */
    private AtomicReference<String> stubUpsert()
    {
        final AtomicReference<String> persisted = new AtomicReference<>();
        when( repoService.upsertApplicationDescriptor( eq( MYAPP ), anyString(), any() ) ).thenAnswer( invocation -> {
            persisted.set( invocation.getArgument( 1 ) );
            final ApplicationIconUpdate iconUpdate = invocation.getArgument( 2 );
            if ( iconUpdate instanceof ApplicationIconUpdate.Remove )
            {
                iconMimeType.set( null );
            }
            else if ( iconUpdate instanceof ApplicationIconUpdate.Replace replace )
            {
                iconMimeType.set( replace.mimeType() );
            }
            return descriptorNode( persisted.get(), iconMimeType.get() );
        } );
        return persisted;
    }

    private void stubDescriptorNode( final String yaml, final String iconMimeType )
    {
        stubDescriptorNodeFrom( new AtomicReference<>( yaml ), iconMimeType );
    }

    /**
     * The descriptor node holds whatever the reference holds at read time, so the descriptor read after a write is the written one.
     */
    private void stubDescriptorNodeFrom( final AtomicReference<String> yaml, final String initialIconMimeType )
    {
        this.iconMimeType.set( initialIconMimeType );
        when( repoService.getApplicationDescriptorNode( MYAPP ) ).thenAnswer(
            invocation -> yaml.get() != null ? descriptorNode( yaml.get(), iconMimeType.get() ) : null );
        when( nodeService.getByPath( new NodePath( "/applications/myapp/enonic.yaml" ) ) ).thenAnswer(
            invocation -> yaml.get() != null ? descriptorNode( yaml.get(), iconMimeType.get() ) : null );
        when( nodeService.getBinary( any( NodeId.class ), eq( SchemaResourceNames.APP_ICON_BINARY_REFERENCE ) ) ).thenReturn(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );
    }

    private static Node descriptorNode( final String yaml, final String iconMimeType )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, yaml );
        if ( iconMimeType != null )
        {
            data.setString( SchemaNodePropertyNames.MIME_TYPE, iconMimeType );
            data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourceNames.APP_ICON_BINARY_REFERENCE );
        }
        return Node.create()
            .id( NodeId.from( "descriptor" ) )
            .name( "enonic.yaml" )
            .parentPath( new NodePath( "/applications/myapp" ) )
            .data( data )
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
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( symbolicName );
        final URL resource = resourceTestHelper.getTestResource( APP_DESCRIPTOR_FILENAME );
        // getEntry marks the bundle as an application, getResource serves it through the bundle url resolver
        Mockito.when( bundle.getEntry( APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getResource( "/" + APP_DESCRIPTOR_FILENAME ) ).thenReturn( resource );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        return bundle;
    }

    private static Bundle mockNonAppBundle( final String symbolicName )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( symbolicName );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );
        return bundle;
    }
}