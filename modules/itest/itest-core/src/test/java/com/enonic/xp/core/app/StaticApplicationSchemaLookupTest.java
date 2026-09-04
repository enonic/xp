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
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationListenerHub;
import com.enonic.xp.core.impl.app.ApplicationRegistryImpl;
import com.enonic.xp.core.impl.app.ApplicationRepoInitializer;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.VirtualAppService;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.core.impl.content.schema.CmsFormFragmentServiceImpl;
import com.enonic.xp.core.impl.content.schema.ContentTypeServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Verifies that schema services see the schemas of a {@code type: Static} application, which are served from nodes.
 */
class StaticApplicationSchemaLookupTest
    extends AbstractNodeTest
{
    private static final String STATIC_DESCRIPTOR = "kind: \"Application\"\ntype: \"Static\"\n";

    private static final String CONTENT_TYPE = "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"My type\"\n";

    private static final String INVALID_CONTENT_TYPE = "kind: \"ContentType\"\ndisplayName: \"Unknown property\"\n";

    @TempDir
    public Path felixTempFolder;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private ContentTypeService contentTypeService;

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

        final ApplicationRepoServiceImpl repoService = new ApplicationRepoServiceImpl( nodeService );
        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        final BundleContext bundleContext = felix.getBundleContext();

        final ApplicationFactoryServiceImpl applicationFactoryService =
            new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        final ApplicationAuditLogSupportImpl auditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        auditLogSupport.activate( appConfig );

        this.applicationService = new ApplicationServiceImpl(
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService ), repoService,
            new EventPublisherImpl( Executors.newSingleThreadExecutor() ), new AppFilterServiceImpl( appConfig ),
            new VirtualAppService( nodeService ), auditLogSupport );

        this.contentTypeService =
            new ContentTypeServiceImpl( resourceService, applicationService, new CmsFormFragmentServiceImpl( resourceService ) );
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
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", CONTENT_TYPE, //
                "cms/content-types/othertype/othertype.yml", CONTENT_TYPE ) ) );

            final ContentTypes byApplication = contentTypeService.getByApplication( appKey );

            assertEquals( List.of( "staticapp:mytype", "staticapp:othertype" ),
                          byApplication.stream().map( type -> type.getName().toString() ).sorted().toList() );
            assertTrue( byApplication.stream().map( ContentType::getTitle ).allMatch( "My type"::equals ) );
        } );
    }

    @Test
    void getByApplication_skips_content_types_that_fail_to_parse()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", CONTENT_TYPE, //
                "cms/content-types/broken/broken.yaml", INVALID_CONTENT_TYPE ) ) );

            // the descriptor is discovered, but loading it fails and ContentTypeRegistry logs the error and drops the type
            assertEquals( List.of( "staticapp:broken", "staticapp:mytype" ), resourceService.findFiles( appKey,
                                                                                                        "^/cms/content-types/(?<name>[^/]+)/\\k<name>\\.(?:yaml|yml)$" )
                .stream()
                .map( key -> key.getApplicationKey() + ":" + key.getPath().split( "/" )[3] )
                .sorted()
                .toList() );

            assertEquals( List.of( "staticapp:mytype" ),
                          contentTypeService.getByApplication( appKey ).stream().map( type -> type.getName().toString() ).toList() );
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
