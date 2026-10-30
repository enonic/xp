package com.enonic.xp.core.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.StreamSupport;

import org.apache.felix.framework.Felix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.app.AppConfig;
import com.enonic.xp.core.impl.app.AppFilterServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.ApplicationBundleException;
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationHelper;
import com.enonic.xp.core.impl.app.ApplicationListenerHub;
import com.enonic.xp.core.impl.app.ApplicationRegistryImpl;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.DynamicSchemaAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.SchemaConfig;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.core.impl.content.schema.CmsFormFragmentServiceImpl;
import com.enonic.xp.core.impl.content.schema.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Verifies that schema services see the schemas of an application shipping {@code cms/cms.yaml}, which are served from nodes.
 */
class PersistedSchemaLookupTest
    extends AbstractNodeTest
{
    private static final String APP_DESCRIPTOR = "kind: \"Application\"\n";

    private static final String CONTENT_TYPE = "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"My type\"\n";

    private static final String INVALID_CONTENT_TYPE = "kind: \"ContentType\"\ndisplayName: \"Unknown property\"\n";

    private static final String CONTENT_TYPE_WITH_FRAGMENT =
        "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"With fragment\"\nform:\n  - include: \"address\"\n";

    private static final String FRAGMENT_V1 = "kind: \"FormFragment\"\ntitle: \"Address\"\nform:\n  - type: \"TextLine\"\n    name: \"street\"\n    label: \"Street\"\n";

    private static final String FRAGMENT_V2 =
        "kind: \"FormFragment\"\ntitle: \"Address\"\nform:\n  - type: \"TextLine\"\n    name: \"street\"\n    label: \"Street\"\n" +
            "  - type: \"TextLine\"\n    name: \"city\"\n    label: \"City\"\n";

    @TempDir
    public Path felixTempFolder;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private ContentTypeService contentTypeService;

    private DynamicSchemaService dynamicSchemaService;

    private ApplicationFactoryServiceImpl applicationFactoryService;

    private DynamicSchemaAuditLogSupportImpl schemaAuditLogSupport;

    private ApplicationRegistryImpl applicationRegistry;

    private ApplicationRepoServiceImpl repoService;

    private Felix felix;

    @BeforeEach
    void setUp()
        throws Exception
    {
        final Path cacheDir = Files.createDirectory( this.felixTempFolder.resolve( "cache" ) ).toAbsolutePath();

        final Map<String, Object> config = new HashMap<>();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.toString() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );
        this.felix = new Felix( config );
        this.felix.start();

        final AppConfig appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        this.repoService = new ApplicationRepoServiceImpl( nodeService );

        final BundleContext bundleContext = felix.getBundleContext();

        this.applicationFactoryService = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        applicationFactoryService.activate();

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        final ApplicationAuditLogSupportImpl auditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        auditLogSupport.activate( appConfig );

        this.applicationRegistry = new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService );
        this.applicationService = new ApplicationServiceImpl( applicationRegistry, repoService,
                                                              new EventPublisherImpl( Executors.newSingleThreadExecutor() ),
                                                              new AppFilterServiceImpl( appConfig ), auditLogSupport );

        this.contentTypeService =
            new ContentTypeServiceImpl( resourceService, applicationService, new CmsFormFragmentServiceImpl( resourceService ) );

        this.schemaAuditLogSupport = new DynamicSchemaAuditLogSupportImpl( mock( AuditLogService.class ) );
        schemaAuditLogSupport.activate( mock( SchemaConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );
        this.dynamicSchemaService =
            new DynamicSchemaServiceImpl( nodeService, schemaAuditLogSupport, mock( EventPublisher.class ), applicationRegistry );
    }

    @AfterEach
    void destroy()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop( 10_000 );
    }

    @Test
    void getByApplication_returns_persisted_content_types()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", CONTENT_TYPE, //
                "cms/content-types/othertype/othertype.yml", CONTENT_TYPE ) ) );

            final ContentTypes byApplication = contentTypeService.getByApplication( appKey );

            assertEquals( List.of( "staticapp:mytype", "staticapp:othertype" ),
                          byApplication.stream().map( type -> type.getName().toString() ).sorted().toList() );
            assertTrue( byApplication.stream().map( ContentType::getTitle ).allMatch( "My type"::equals ) );
        } );
    }

    // the content type is cached with its form fragments inlined: a write of a fragment is seen by the next read of the content
    // type, the generation of the application having moved with the write
    @Test
    void content_type_follows_a_form_fragment_written_through_the_schema_service()
    {
        final ContentTypeName typeName = ContentTypeName.from( "staticapp:withfragment" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/form-fragments/address.yaml", FRAGMENT_V1, //
                "cms/content-types/withfragment.yaml", CONTENT_TYPE_WITH_FRAGMENT ) ) );

            final ContentType before = contentTypeService.getByName( GetContentTypeParams.from( typeName ) );
            assertEquals( List.of( "street" ), inputNames( before ) );
            // served from the cache from now on
            assertEquals( List.of( "street" ), inputNames( contentTypeService.getByName( GetContentTypeParams.from( typeName ) ) ) );

            dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                 .name( FormFragmentName.from( "staticapp:address" ) )
                                                                 .resource( FRAGMENT_V2 )
                                                                 .build() );

            final ContentType after = contentTypeService.getByName( GetContentTypeParams.from( typeName ) );
            assertEquals( List.of( "street", "city" ), inputNames( after ) );
            // one change of the schema since the install
            assertEquals( 1, schemaRevision( "staticapp" ) );

            // and the content type itself, written through the schema service, replaces the one shipped by the bundle
            dynamicSchemaService.createOrUpdateContentType( CreateOrUpdateContentSchemaParams.create()
                                                                .name( typeName )
                                                                .resource( CONTENT_TYPE.replace( "My type", "Rewritten" ) )
                                                                .build() );
            assertEquals( "Rewritten", contentTypeService.getByName( GetContentTypeParams.from( typeName ) ).getTitle() );
            assertTrue( inputNames( contentTypeService.getByName( GetContentTypeParams.from( typeName ) ) ).isEmpty() );
            assertEquals( 2, schemaRevision( "staticapp" ) );

            // a new version of the bundle ships its schema anew: nothing changed since
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.1", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/form-fragments/address.yaml", FRAGMENT_V1, //
                "cms/content-types/withfragment.yaml", CONTENT_TYPE_WITH_FRAGMENT ) ) );
            assertEquals( 0, schemaRevision( "staticapp" ) );
            assertEquals( List.of( "street" ), inputNames( contentTypeService.getByName( GetContentTypeParams.from( typeName ) ) ) );
        } );
    }

    // how many times the schema of the application was changed through the schema service since its install
    private long schemaRevision( final String appName )
    {
        return ApplicationHelper.runAsAdmin( () -> repoService.getApplicationNode( ApplicationKey.from( appName ) ) )
            .data()
            .getLong( "schemaRevision" );
    }

    // a fragment the form includes but which the application does not have: the content type installs, reading it reports the
    // fragment, as it did before the fragments were inlined into the cached value
    @Test
    void missing_form_fragment_is_reported_on_read()
    {
        final ContentTypeName typeName = ContentTypeName.from( "staticapp:withfragment" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/withfragment.yaml", CONTENT_TYPE_WITH_FRAGMENT ) ) );

            final IllegalArgumentException e = assertThrows( IllegalArgumentException.class,
                                                          () -> contentTypeService.getByName( GetContentTypeParams.from( typeName ) ) );
            assertEquals( "Form fragment [staticapp:address] not found", e.getMessage() );

            // written afterwards, the fragment makes the content type readable: nothing of the failed read was cached
            dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                 .name( FormFragmentName.from( "staticapp:address" ) )
                                                                 .resource( FRAGMENT_V1 )
                                                                 .build() );
            assertEquals( List.of( "street" ), inputNames( contentTypeService.getByName( GetContentTypeParams.from( typeName ) ) ) );
        } );
    }

    // another node of the cluster writes a fragment and its event never arrives: the version of the application node, moved by the
    // write and read from the shared storage on every use, is what tells this node that the content type it cached is stale
    @Test
    void change_written_on_another_node_is_seen_without_the_event()
    {
        final ContentTypeName typeName = ContentTypeName.from( "staticapp:withfragment" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/form-fragments/address.yaml", FRAGMENT_V1, //
                "cms/content-types/withfragment.yaml", CONTENT_TYPE_WITH_FRAGMENT ) ) );

            final ContentTypeService thisNode = contentTypeService();
            assertEquals( List.of( "street" ), inputNames( thisNode.getByName( GetContentTypeParams.from( typeName ) ) ) );

            final DynamicSchemaService otherNode =
                new DynamicSchemaServiceImpl( nodeService, schemaAuditLogSupport, mock( EventPublisher.class ), applicationRegistry );
            otherNode.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                      .name( FormFragmentName.from( "staticapp:address" ) )
                                                      .resource( FRAGMENT_V2 )
                                                      .build() );

            assertEquals( List.of( "street", "city" ), inputNames( thisNode.getByName( GetContentTypeParams.from( typeName ) ) ) );
        } );
    }

    // the services of a cluster node of its own, caching the schema it reads
    private ContentTypeService contentTypeService()
    {
        final ResourceService resourceService = new ResourceServiceImpl( applicationFactoryService );
        return new ContentTypeServiceImpl( resourceService, applicationService, new CmsFormFragmentServiceImpl( resourceService ) );
    }

    private static List<String> inputNames( final ContentType contentType )
    {
        return StreamSupport.stream( contentType.getForm().spliterator(), false ).map( FormItem::getName ).toList();
    }

    // a descriptor that would not load is rejected before anything is written, the application is not installed
    @Test
    void install_rejects_content_type_that_fails_to_parse()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            final ByteSource source = createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", APP_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", CONTENT_TYPE, //
                "cms/content-types/broken/broken.yaml", INVALID_CONTENT_TYPE ) );

            final ApplicationBundleException exception =
                assertThrows( ApplicationBundleException.class, () -> applicationService.installGlobalApplication( source ) );
            assertTrue( exception.getMessage().contains( "cms/content-types/broken.yaml: " ), exception.getMessage() );
            assertTrue( exception.getMessage().contains( "displayName" ), exception.getMessage() );

            assertNull( applicationService.get( appKey ) );
            assertTrue( resourceService.findFiles( appKey, "^/cms/.*$" ).isEmpty() );
            assertTrue( contentTypeService.getByApplication( appKey ).isEmpty() );
        } );
    }

    private Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    private static ByteSource createAppSource( final String name, final String version, final Map<String, String> resources )
    {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( Attributes.Name.MANIFEST_VERSION, "1.0" );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_SYMBOLICNAME, name );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_VERSION, version );
        manifest.getMainAttributes().putValue( "X-Bundle-Type", "application" );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JarOutputStream jar = new JarOutputStream( out, manifest ))
        {
            for ( final Map.Entry<String, String> entry : resources.entrySet() )
            {
                jar.putNextEntry( new JarEntry( entry.getKey() ) );
                jar.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
                jar.closeEntry();
            }
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( e );
        }
        return ByteSource.wrap( out.toByteArray() );
    }
}
