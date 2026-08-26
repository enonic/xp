package com.enonic.xp.core.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
import org.ops4j.pax.tinybundles.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
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
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ApplicationServiceTest
    extends AbstractNodeTest
{
    private static final String STATIC_DESCRIPTOR = "kind: \"Application\"\ntype: \"Static\"\n";

    private static final String BUNDLE_DESCRIPTOR = "kind: \"Application\"\n";

    @TempDir
    public Path felixTempFolder;

    private ApplicationService applicationService;

    private ResourceService resourceService;

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
        ApplicationRepoInitializer.create().setIndexService( indexService ).setNodeService( nodeService ).build().initialize();

        BundleContext bundleContext = felix.getBundleContext();

        ApplicationFactoryServiceImpl applicationFactoryService =
            new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        applicationFactoryService.activate();

        this.resourceService = new ResourceServiceImpl( applicationFactoryService );

        ApplicationAuditLogSupportImpl applicationAuditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        applicationAuditLogSupport.activate( appConfig );

        this.applicationService = new ApplicationServiceImpl(
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService ), repoService,
            new EventPublisherImpl( Executors.newSingleThreadExecutor() ), new AppFilterServiceImpl( appConfig ),
            new VirtualAppService( nodeService ), applicationAuditLogSupport );
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
            Application application = applicationService.installGlobalApplication( createByteSource( "7.8.0" ) );
            assertEquals( "7.8.0", application.getVersion().toString() );

            systemRepoContext().callWith( () -> {
                Node applicationNode = nodeService.getByPath(
                    NodePath.create( NodePath.ROOT ).addElement( "applications" ).addElement( applicationName ).build() );
                assertNotNull( applicationNode );
                assertEquals( "7.8.0", applicationNode.data().getString( "version" ) );
                return null;
            } );

            application = applicationService.installGlobalApplication( createByteSource( "7.8.1" ) );
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

    @Test
    void installGlobalStaticApplicationPersistsSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yml", "kind: \"ContentType\"\ndisplayName: \"My type\"", //
                "cms/content-types/mytype/mytype.svg", "<svg/>", //
                "cms/i18n/phrases/phrases_en.properties", "key=value", //
                "i18n/phrases_en.properties", "root=value", //
                "assets/app.js", "console.log()" ) ) );

            // schema resources are persisted below the application node in system-repo
            assertEquals( "kind: \"CMS\"", schemaNode( "staticapp", "cms.yaml" ).data().getString( "resource" ) );
            assertEquals( "kind: \"ContentType\"\ndisplayName: \"My type\"",
                          schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ).data().getString( "resource" ) );
            assertEquals( "key=value", schemaNode( "staticapp", "i18n/phrases/phrases_en.properties" ).data().getString( "resource" ) );
            // icons are persisted as node binaries
            final Node iconNode = schemaNode( "staticapp", "content-types/mytype/mytype.svg" );
            assertEquals( "image/svg+xml", iconNode.data().getString( "mimeType" ) );
            assertNotNull( iconNode.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "icon" ) ) );
            // resources outside cms are not persisted
            assertNull( appChildNode( "staticapp", "i18n" ) );
            assertNull( appChildNode( "staticapp", "assets" ) );

            // descriptors are served from nodes (the bundle contained .yml, node is normalized to .yaml)
            final Resource contentType = resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) );
            assertTrue( contentType.exists() );
            assertEquals( "node", contentType.getResolverName() );
            assertEquals( "kind: \"ContentType\"\ndisplayName: \"My type\"", contentType.readString() );

            final Resource cms = resourceService.getResource( ResourceKey.from( appKey, "/cms/cms.yaml" ) );
            assertEquals( "node", cms.getResolverName() );

            final Resource phrases = resourceService.getResource( ResourceKey.from( appKey, "/cms/i18n/phrases/phrases_en.properties" ) );
            assertEquals( "node", phrases.getResolverName() );
            assertEquals( "key=value", phrases.readString() );

            // the bundle's own schema descriptor is hidden
            assertFalse( resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yml" ) ).exists() );

            // the icon is served from the node as well
            final Resource icon = resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.svg" ) );
            assertEquals( "node", icon.getResolverName() );
            assertEquals( "<svg/>", icon.readString() );

            // non-schema resources are still served from the bundle
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/assets/app.js" ) ).getResolverName() );
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/i18n/phrases_en.properties" ) ).getResolverName() );

            assertTrue( resourceService.findFiles( appKey, "^/cms/.*\\.yaml$" )
                            .contains( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ) );
            assertFalse( resourceService.findFiles( appKey, "^/cms/.*\\.yml$" )
                             .contains( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yml" ) ) );
        } );

        // persisted schema is readable without any privileges (e.g. portal rendering)
        final Resource anonymous = ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.unAuthenticated() )
            .build()
            .callWith( () -> resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ) );
        assertEquals( "node", anonymous.getResolverName() );
        assertTrue( anonymous.exists() );
    }

    @Test
    void reinstallStaticApplicationResetsSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"", //
                "cms/i18n/phrases/phrases_en.properties", "key=value" ) ) );

            final Node appNode = appNode( "staticapp" );
            assertNotNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );

            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.1", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/newtype/newtype.yaml", "kind: \"ContentType\"" ) ) );

            assertEquals( appNode.id(), appNode( "staticapp" ).id() );
            assertNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );
            assertNull( schemaNode( "staticapp", "i18n/phrases/phrases_en.properties" ) );
            assertNotNull( schemaNode( "staticapp", "content-types/newtype/newtype.yaml" ) );

            assertFalse( resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).exists() );
            assertEquals( "node",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/newtype/newtype.yaml" ) ).getResolverName() );
        } );
    }

    @Test
    void reinstallAsBundleWithCmsDescriptorReplacesSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );

            // the new version is not Static, but ships cms/cms.yaml: it owns the schema, the persisted one is replaced
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.1", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/newtype/newtype.yaml", "kind: \"ContentType\"", //
                "cms/parts/mypart/mypart.yaml", "kind: \"Part\"", //
                "cms/parts/mypart/mypart.js", "exports.get = function() {}" ) ) );

            assertNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );
            assertNotNull( schemaNode( "staticapp", "cms.yaml" ) );
            assertNotNull( schemaNode( "staticapp", "content-types/newtype/newtype.yaml" ) );
            assertNotNull( schemaNode( "staticapp", "parts/mypart/mypart.yaml" ) );
            assertNull( schemaNode( "staticapp", "parts/mypart/mypart.js" ) );

            assertFalse( resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).exists() );
            assertEquals( "node",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/newtype/newtype.yaml" ) ).getResolverName() );
            assertEquals( "node", resourceService.getResource( ResourceKey.from( appKey, "/cms/parts/mypart/mypart.yaml" ) ).getResolverName() );
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/cms/parts/mypart/mypart.js" ) ).getResolverName() );
        } );
    }

    @Test
    void reinstallAsBundleWithoutCmsDescriptorKeepsSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "staticapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );

            // the new version ships logic only (no cms/cms.yaml): the persisted schema stays and is still served
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.1", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/parts/mypart/mypart.js", "exports.get = function() {}", //
                "assets/app.js", "console.log()" ) ) );

            assertNotNull( appNode( "staticapp" ) );
            assertNotNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );

            final Resource contentType = resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) );
            assertTrue( contentType.exists() );
            assertEquals( "node", contentType.getResolverName() );
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/cms/parts/mypart/mypart.js" ) ).getResolverName() );
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/assets/app.js" ) ).getResolverName() );
        } );
    }

    @Test
    void reinstallWithBrokenSchemaKeepsPersistedSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "brokenschemaapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "brokenschemaapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( schemaNode( "brokenschemaapp", "content-types/mytype/mytype.yaml" ) );

            // "my?type" passes the schema resource pattern but is not a valid node name: persisting the new schema fails
            // while it is being staged, so the previously persisted schema survives and the staging leftover is cleaned up
            assertThrows( RuntimeException.class, () -> applicationService.installGlobalApplication(
                createAppSource( "brokenschemaapp", "1.0.1", Map.of( //
                    "enonic.yaml", BUNDLE_DESCRIPTOR, //
                    "cms/cms.yaml", "kind: \"CMS\"", //
                    "cms/content-types/my?type/my?type.yaml", "kind: \"ContentType\"" ) ) ) );

            assertNotNull( schemaNode( "brokenschemaapp", "content-types/mytype/mytype.yaml" ) );
            assertNull( appChildNode( "brokenschemaapp", "cms_staging" ) );

            // reinstalling a fixed version repairs the application: the schema is replaced and served
            applicationService.installGlobalApplication( createAppSource( "brokenschemaapp", "1.0.2", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/newtype/newtype.yaml", "kind: \"ContentType\"" ) ) );

            assertNull( schemaNode( "brokenschemaapp", "content-types/mytype/mytype.yaml" ) );
            assertNotNull( schemaNode( "brokenschemaapp", "content-types/newtype/newtype.yaml" ) );
            assertEquals( "node", resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/newtype/newtype.yaml" ) )
                .getResolverName() );
        } );
    }

    @Test
    void installGlobalBundleApplicationWithCmsDescriptorPersistsSchema()
    {
        // own name: the repository is shared by the tests of this class, and a persisted schema outlives a bundle without cms/cms.yaml
        final ApplicationKey appKey = ApplicationKey.from( "schemabundleapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "schemabundleapp", "1.0.0", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"", //
                "cms/parts/mypart/mypart.yaml", "kind: \"Part\"", //
                "cms/parts/mypart/mypart.js", "exports.get = function() {}" ) ) );

            assertNotNull( appNode( "schemabundleapp" ) );
            assertNotNull( schemaNode( "schemabundleapp", "cms.yaml" ) );
            assertNotNull( schemaNode( "schemabundleapp", "content-types/mytype/mytype.yaml" ) );
            assertNotNull( schemaNode( "schemabundleapp", "parts/mypart/mypart.yaml" ) );
            // controllers are not schema resources
            assertNull( schemaNode( "schemabundleapp", "parts/mypart/mypart.js" ) );

            assertEquals( "node", resourceService.getResource( ResourceKey.from( appKey, "/cms/cms.yaml" ) ).getResolverName() );
            assertEquals( "node",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).getResolverName() );
            assertEquals( "node", resourceService.getResource( ResourceKey.from( appKey, "/cms/parts/mypart/mypart.yaml" ) ).getResolverName() );
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/cms/parts/mypart/mypart.js" ) ).getResolverName() );
        } );
    }

    @Test
    void uninstallStaticApplicationRemovesSchema()
    {
        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "staticapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );

            applicationService.uninstallApplication( ApplicationKey.from( "staticapp" ) );

            assertNull( appNode( "staticapp" ) );
            assertNull( schemaNode( "staticapp", "content-types/mytype/mytype.yaml" ) );
        } );
    }

    @Test
    void installGlobalBundleApplicationDoesNotPersistSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "bundleapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "bundleapp", "1.0.0", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( appNode( "bundleapp" ) );
            assertNull( appChildNode( "bundleapp", "cms" ) );

            final Resource contentType = resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) );
            assertTrue( contentType.exists() );
            assertEquals( "bundle", contentType.getResolverName() );
        } );
    }

    @Test
    void installLocalStaticApplicationDoesNotPersistSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "localapp" );

        adminContext().runWith( () -> {
            applicationService.installLocalApplication( createAppSource( "localapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNull( appNode( "localapp" ) );

            // no persisted schema: the static application falls back to the bundle
            final Resource contentType = resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) );
            assertTrue( contentType.exists() );
            assertEquals( "bundle", contentType.getResolverName() );
        } );
    }

    @Test
    void localApplicationWithCmsDescriptorOverridesPersistedSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "overriddenapp" );

        adminContext().runWith( () -> {
            applicationService.installGlobalApplication( createAppSource( "overriddenapp", "1.0.0", Map.of( //
                "enonic.yaml", STATIC_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            assertNotNull( schemaNode( "overriddenapp", "content-types/mytype/mytype.yaml" ) );
            assertEquals( "node",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).getResolverName() );

            // a local build of the same application, shipping its own schema, is deployed on top of the global one
            applicationService.installLocalApplication( createAppSource( "overriddenapp", "1.0.1-SNAPSHOT", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/othertype/othertype.yaml", "kind: \"ContentType\"" ) ) );

            assertTrue( applicationService.isLocalApplication( appKey ) );

            // the persisted schema is untouched...
            assertNotNull( schemaNode( "overriddenapp", "content-types/mytype/mytype.yaml" ) );
            // ...but ignored: the local bundle is the only schema source
            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/cms/cms.yaml" ) ).getResolverName() );
            assertEquals( "bundle",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/othertype/othertype.yaml" ) ).getResolverName() );
            assertFalse( resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).exists() );

            // removing the local application brings the stored one, and its persisted schema, back
            applicationService.uninstallLocalApplication( appKey );

            assertFalse( applicationService.isLocalApplication( appKey ) );
            assertEquals( "node",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).getResolverName() );
            assertFalse( resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/othertype/othertype.yaml" ) ).exists() );
        } );
    }

    @Test
    void installLocalBundleApplicationWithCmsDescriptorDoesNotPersistSchema()
    {
        final ApplicationKey appKey = ApplicationKey.from( "localbundleapp" );

        adminContext().runWith( () -> {
            applicationService.installLocalApplication( createAppSource( "localbundleapp", "1.0.0", Map.of( //
                "enonic.yaml", BUNDLE_DESCRIPTOR, //
                "cms/cms.yaml", "kind: \"CMS\"", //
                "cms/content-types/mytype/mytype.yaml", "kind: \"ContentType\"" ) ) );

            // local applications are never stored: no application node, no persisted schema
            assertNull( appNode( "localbundleapp" ) );

            assertEquals( "bundle", resourceService.getResource( ResourceKey.from( appKey, "/cms/cms.yaml" ) ).getResolverName() );
            assertEquals( "bundle",
                          resourceService.getResource( ResourceKey.from( appKey, "/cms/content-types/mytype/mytype.yaml" ) ).getResolverName() );
        } );
    }

    private Node appNode( final String appName )
    {
        return systemRepoContext().callWith(
            () -> nodeService.getByPath( NodePath.create( NodePath.ROOT ).addElement( "applications" ).addElement( appName ).build() ) );
    }

    private Node appChildNode( final String appName, final String childName )
    {
        return systemRepoContext().callWith( () -> nodeService.getByPath(
            NodePath.create( NodePath.ROOT ).addElement( "applications" ).addElement( appName ).addElement( childName ).build() ) );
    }

    private Node schemaNode( final String appName, final String cmsRelativePath )
    {
        return systemRepoContext().callWith(
            () -> nodeService.getByPath( new NodePath( "/applications/" + appName + "/cms/" + cmsRelativePath ) ) );
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
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
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
                                                             .addResource( "cms/site.yml",
                                                                           getClass().getResource( "/myapp/cms/site.yml" ) )
                                                             .build() ) );
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