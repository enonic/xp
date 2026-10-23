package com.enonic.xp.core.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.apache.felix.framework.Felix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationRepoConstants;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.app.ApplicationGeneration;
import com.enonic.xp.core.impl.app.AppConfig;
import com.enonic.xp.core.impl.app.AppFilterServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationListenerHub;
import com.enonic.xp.core.impl.app.ApplicationRegistryImpl;
import com.enonic.xp.core.impl.app.ApplicationRepoMigrator;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * The applications persisted in system-repo by an earlier version are moved to the application repository at startup, and end up
 * there exactly as a global installation persists them.
 */
class ApplicationRepoMigratorTest
    extends AbstractNodeTest
{
    private static final NodePath LEGACY_PATH = new NodePath( "/applications" );

    private static final String APP_DESCRIPTOR = "kind: \"Application\"\n";

    private static final Map<String, String> SCHEMA_RESOURCES = Map.of( //
        "enonic.yml", APP_DESCRIPTOR, //
        "enonic.svg", "<svg>app</svg>", //
        "cms/cms.yml", "kind: \"CMS\"", //
        "cms/content-types/mytype/mytype.yml", "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle: \"My type\"", //
        "cms/content-types/mytype/mytype.svg", "<svg/>", //
        "cms/i18n/phrases/phrases_en.properties", "key=value", //
        "assets/app.js", "console.log()" );

    @TempDir
    public Path felixTempFolder;

    private Felix felix;

    private ApplicationRepoServiceImpl repoService;

    private ApplicationServiceImpl applicationService;

    private final List<Event> publishedEvents = new ArrayList<>();

    private ApplicationRepoMigrator migrator;

    // the tests leave applications behind in both repositories: each starts from clean ones
    ApplicationRepoMigratorTest()
    {
        super( true );
    }

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
        final BundleContext bundleContext = felix.getBundleContext();

        this.repoService = new ApplicationRepoServiceImpl( nodeService );

        final ApplicationFactoryServiceImpl applicationFactoryService = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        applicationFactoryService.activate();

        final ApplicationAuditLogSupportImpl auditLogSupport = new ApplicationAuditLogSupportImpl( mock( AuditLogService.class ) );
        auditLogSupport.activate( appConfig );

        this.applicationService = new ApplicationServiceImpl(
            new ApplicationRegistryImpl( bundleContext, new ApplicationListenerHub(), applicationFactoryService ), repoService,
            new EventPublisherImpl( Executors.newSingleThreadExecutor() ), new AppFilterServiceImpl( appConfig ), auditLogSupport,
            new ApplicationGeneration( nodeService ) );

        final EventPublisher eventPublisher = mock( EventPublisher.class );
        doAnswer( invocation -> publishedEvents.add( invocation.getArgument( 0 ) ) ).when( eventPublisher ).publish( any() );
        this.migrator = new ApplicationRepoMigrator( nodeService, repoService, eventPublisher );
    }

    @AfterEach
    void tearDown()
        throws Exception
    {
        felix.stop();
        felix.waitForStop( 5000 );
    }

    @Test
    void migratedApplicationIsPersistedLikeAnInstalledOne()
    {
        adminContext().runWith( () -> applicationService.installGlobalApplication( appSource( "installedapp", SCHEMA_RESOURCES ) ) );
        createLegacyNode( "legacyapp", appSource( "legacyapp", SCHEMA_RESOURCES ), true );

        migrator.migrate();

        // the same tree below the application node, whichever way the application got there
        assertEquals( describe( "/installedapp" ), describe( "/legacyapp" ) );

        final Node migrated = appNode( "legacyapp" );
        assertEquals( Boolean.TRUE, migrated.data().getBoolean( "started" ) );
        assertNotNull( migrated.getAttachedBinaries().getByBinaryReference( BinaryReference.from( "applicationFile" ) ) );
        assertEquals( "kind: \"CMS\"", appChildNode( "legacyapp", "cms/cms" ).data().getString( "resource" ) );

        // the legacy nodes are gone, folder included
        assertNull( legacyNode( "legacyapp" ) );
        assertFalse( legacyContext().callWith( () -> nodeService.nodeExists( LEGACY_PATH ) ) );

        // the other cluster nodes are told to install and start it
        assertEquals( List.of( "install", "start" ), eventSubTypes( "legacyapp" ) );
    }

    @Test
    void stoppedApplicationStaysStopped()
    {
        createLegacyNode( "stoppedapp", appSource( "stoppedapp", SCHEMA_RESOURCES ), false );

        migrator.migrate();

        assertNull( appNode( "stoppedapp" ).data().getBoolean( "started" ) );
        assertEquals( List.of( "install" ), eventSubTypes( "stoppedapp" ) );
    }

    // an application that would not install is not migrated: it is left where it was, and the folder with it
    @Test
    void brokenApplicationIsLeftInSystemRepo()
    {
        createLegacyNode( "goodapp", appSource( "goodapp", SCHEMA_RESOURCES ), true );
        createLegacyNode( "wrongkindapp",
                          appSource( "wrongkindapp", Map.of( "cms/cms.yml", "kind: \"CMS\"", "cms/parts/mypart/mypart.yml", "kind: \"Page\"" ) ),
                          true );
        createLegacyNode( "notajar", ByteSource.wrap( "not a jar".getBytes( StandardCharsets.UTF_8 ) ), true );
        createLegacyNode( "unknownpropertyapp", appSource( "unknownpropertyapp", Map.of( "cms/cms.yml", "kind: \"CMS\"",
                                                                                         "cms/content-types/mytype/mytype.yml",
                                                                                         "kind: \"ContentType\"\nunknownProperty: \"UNKNOWN\"" ) ),
                          true );

        migrator.migrate();

        assertNotNull( appNode( "goodapp" ) );
        assertNull( legacyNode( "goodapp" ) );

        assertNull( appNode( "wrongkindapp" ) );
        assertNotNull( legacyNode( "wrongkindapp" ) );
        assertNull( appNode( "notajar" ) );
        assertNotNull( legacyNode( "notajar" ) );
        assertNull( appNode( "unknownpropertyapp" ) );
        assertNotNull( legacyNode( "unknownpropertyapp" ) );
        assertTrue( legacyContext().callWith( () -> nodeService.nodeExists( LEGACY_PATH ) ) );

        assertEquals( List.of( "install", "start" ), eventSubTypes( "goodapp" ) );
        assertTrue( eventSubTypes( "wrongkindapp" ).isEmpty() );
    }

    // left by a run that failed after writing the new nodes: replaced as a whole on the next run
    @Test
    void rerunReplacesNodesOfAnEarlierRun()
    {
        createLegacyNode( "retriedapp", appSource( "retriedapp", SCHEMA_RESOURCES ), false );
        ctxAppRepo().runWith( () -> {
            final Node stale = nodeService.create( CreateNodeParams.create().parent( NodePath.ROOT ).name( "retriedapp" ).build() );
            nodeService.create( CreateNodeParams.create().parent( stale.path() ).name( "stale.yaml" ).refresh( RefreshMode.ALL ).build() );
        } );

        migrator.migrate();

        assertNull( appChildNode( "retriedapp", "stale.yaml" ) );
        assertNotNull( appNode( "retriedapp" ).data().getString( "descriptor" ) );
        assertNull( legacyNode( "retriedapp" ) );
    }

    @Test
    void emptyLegacyFolderIsRemoved()
    {
        legacyContext().runWith( () -> nodeService.create(
            CreateNodeParams.create().parent( NodePath.ROOT ).name( "applications" ).refresh( RefreshMode.ALL ).build() ) );

        migrator.migrate();

        assertFalse( legacyContext().callWith( () -> nodeService.nodeExists( LEGACY_PATH ) ) );
        assertTrue( publishedEvents.isEmpty() );
    }

    @Test
    void nothingToMigrateOnAFreshInstallation()
    {
        migrator.migrate();

        assertFalse( legacyContext().callWith( () -> nodeService.nodeExists( LEGACY_PATH ) ) );
        assertTrue( ctxAppRepo().callWith(
            () -> nodeService.findByParent( FindNodesByParentParams.create().parentPath( NodePath.ROOT ).build() ).getNodeIds().isEmpty() ) );
        assertTrue( publishedEvents.isEmpty() );
    }

    /**
     * The tree below a node as text: relative path, data properties but the modified time, and the attached binaries by name.
     */
    private List<String> describe( final String rootPath )
    {
        return ctxAppRepo().callWith( () -> {
            final Node root = nodeService.getByPath( new NodePath( rootPath ) );
            assertNotNull( root, rootPath );
            final List<String> lines = new ArrayList<>();
            describe( root, root.path().toString().length(), lines );
            return lines;
        } );
    }

    private void describe( final Node node, final int rootPathLength, final List<String> lines )
    {
        final PropertyTree data = node.data().copy();
        data.removeProperties( "modifiedTime" );
        final Set<String> binaries = node.getAttachedBinaries()
            .stream()
            .map( AttachedBinary::getBinaryReference )
            .map( BinaryReference::toString )
            .collect( Collectors.toCollection( TreeSet::new ) );
        lines.add( node.path().toString().substring( rootPathLength ) + " " + data + " " + binaries );

        nodeService.findByParent( FindNodesByParentParams.create().parentPath( node.path() ).build() )
            .getNodeIds()
            .stream()
            .map( nodeService::getById )
            .sorted( ( a, b ) -> a.path().toString().compareTo( b.path().toString() ) )
            .forEach( child -> describe( child, rootPathLength, lines ) );
    }

    private List<String> eventSubTypes( final String appName )
    {
        return publishedEvents.stream()
            .filter( event -> appName.equals( event.getValue( "key" ).orElse( null ) ) )
            .map( event -> event.getValue( "eventType" ).orElse( "" ).toString() )
            .toList();
    }

    // an application node as the earlier versions persisted it: the manifest data and the bundle, nothing below
    private void createLegacyNode( final String name, final ByteSource bundle, final boolean started )
    {
        legacyContext().runWith( () -> {
            if ( !nodeService.nodeExists( LEGACY_PATH ) )
            {
                nodeService.create( CreateNodeParams.create().parent( NodePath.ROOT ).name( "applications" ).build() );
            }
            final PropertyTree data = new PropertyTree();
            data.setString( "displayName", name );
            data.setString( "version", "1.0.0" );
            data.setBoolean( "started", started );
            data.setBinaryReference( "applicationFile", BinaryReference.from( "applicationFile" ) );
            nodeService.create( CreateNodeParams.create()
                                    .parent( LEGACY_PATH )
                                    .name( name )
                                    .data( data )
                                    .attachBinary( BinaryReference.from( "applicationFile" ), bundle )
                                    .refresh( RefreshMode.ALL )
                                    .build() );
        } );
    }

    private Node legacyNode( final String name )
    {
        return legacyContext().callWith( () -> nodeService.getByPath( new NodePath( LEGACY_PATH, NodeName.from( name ) ) ) );
    }

    private Node appNode( final String name )
    {
        return ctxAppRepo().callWith( () -> nodeService.getByPath( new NodePath( "/" + name ) ) );
    }

    private Node appChildNode( final String name, final String relativePath )
    {
        return ctxAppRepo().callWith( () -> nodeService.getByPath( new NodePath( "/" + name + "/" + relativePath ) ) );
    }

    private static Context legacyContext()
    {
        return ContextBuilder.create()
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .authInfo( adminAuth() )
            .build();
    }

    private static Context ctxAppRepo()
    {
        return ContextBuilder.create()
            .repositoryId( ApplicationRepoConstants.APP_REPO_ID )
            .branch( ApplicationRepoConstants.APP_REPO_BRANCH )
            .authInfo( adminAuth() )
            .build();
    }

    private static Context adminContext()
    {
        return ContextBuilder.create().authInfo( adminAuth() ).build();
    }

    private static AuthenticationInfo adminAuth()
    {
        return AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build();
    }

    private static ByteSource appSource( final String name, final Map<String, String> resources )
    {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( Attributes.Name.MANIFEST_VERSION, "1.0" );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_SYMBOLICNAME, name );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_VERSION, "1.0.0" );
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
