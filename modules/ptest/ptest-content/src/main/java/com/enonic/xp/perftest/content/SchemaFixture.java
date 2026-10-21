package com.enonic.xp.perftest.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Constants;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationFactoryServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.impl.app.ApplicationRepoServiceImpl;
import com.enonic.xp.core.impl.app.DynamicSchemaAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.SchemaConfig;
import com.enonic.xp.core.impl.app.descriptor.DescriptorFacetFactoryImpl;
import com.enonic.xp.core.impl.app.descriptor.DescriptorServiceImpl;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorLoader;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.core.impl.content.schema.CmsFormFragmentServiceImpl;
import com.enonic.xp.core.impl.content.schema.ContentTypeServiceImpl;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

/**
 * An application with the services reading and writing its schema, shared by the schema benchmarks: 3 form fragments,
 * {@code contentTypes} content types half of which include two fragments, and a part per five content types, half of which include
 * a fragment. The resolvers read through a node service counting the reads, the check of the application node by the generation
 * through one counting them apart ({@code check:*}).
 * <p>
 * The application is persisted as nodes, as a globally installed one is, or, {@code local}, shipped by a local application bundle
 * only: no node of it exists, it is served from the bundle alone and its schema is not written.
 */
final class SchemaFixture
{
    static final ApplicationKey APP = ApplicationKey.from( "benchapp" );

    static final int FRAGMENTS = 3;

    final Map<String, LongAdder> nodeReads = new ConcurrentHashMap<>();

    final List<ContentTypeName> typesWithFragments = new ArrayList<>();

    final List<ContentTypeName> plainTypes = new ArrayList<>();

    final List<DescriptorKey> parts = new ArrayList<>();

    ContentTypeService contentTypeService;

    PartDescriptorService partDescriptorService;

    ResourceService resourceService;

    DynamicSchemaService dynamicSchemaService;

    private final LongAdder fragmentWrites = new LongAdder();

    private boolean local;

    private SchemaServices services;

    // the schema as written, by the path a bundle ships it at
    private final Map<String, String> bundleResources = new LinkedHashMap<>();

    private Bootstrap bs;

    private Felix felix;

    void start( final int contentTypes )
        throws Exception
    {
        start( contentTypes, false );
    }

    void start( final int contentTypes, final boolean local )
        throws Exception
    {
        this.local = local;
        bs = new Bootstrap();
        bs.start();

        final Path felixDir = Files.createTempDirectory( "ptest-felix-" );
        felix = new Felix( Map.of( Constants.FRAMEWORK_STORAGE, felixDir.toString(), Constants.FRAMEWORK_STORAGE_CLEAN,
                                   Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT ) );
        felix.start();

        // the resolvers read through a node service counting the reads, the schema is written through the plain one
        final ApplicationFactoryServiceImpl applicationFactoryService =
            new ApplicationFactoryServiceImpl( felix.getBundleContext(), counting( bs.nodeService, "" ) );
        applicationFactoryService.activate();

        final DynamicSchemaAuditLogSupportImpl auditLogSupport = new DynamicSchemaAuditLogSupportImpl( Bootstrap.noOp( AuditLogService.class ) );
        auditLogSupport.activate( Bootstrap.annotationDefaults( SchemaConfig.class ) );

        // the registry answers no application for any key: the benchmark app is never a system bundle
        services = new SchemaServices( applicationFactoryService, Bootstrap.noOp( ApplicationRegistry.class ), bs.nodeService,
                                       counting( bs.nodeService, "check:" ), auditLogSupport, bs.eventPublisher );
        resourceService = services.resourceService;
        dynamicSchemaService = services.dynamicSchemaService;

        final CmsFormFragmentServiceImpl formFragmentService = new CmsFormFragmentServiceImpl( resourceService );
        contentTypeService = new ContentTypeServiceImpl( resourceService, null, formFragmentService );

        final DescriptorServiceImpl descriptorService = new DescriptorServiceImpl( new DescriptorFacetFactoryImpl( null, resourceService ) );
        descriptorService.addLoader( new PartDescriptorLoader( resourceService, formFragmentService ) );
        partDescriptorService = new PartDescriptorServiceImpl( descriptorService );

        adminContext().runWith( () -> createApplication( contentTypes, new ApplicationRepoServiceImpl( bs.nodeService ) ) );
        bs.refresh();

        if ( local )
        {
            felix.getBundleContext().installBundle( "local:" + APP, new ByteArrayInputStream( localBundle() ) ).start();
        }
    }

    /**
     * Makes everything cached for the application stale, as a change of its definition does, without changing anything.
     */
    void invalidate()
    {
        services.invalidate( APP );
    }

    private byte[] localBundle()
        throws IOException
    {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put( Attributes.Name.MANIFEST_VERSION, "1.0" );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_MANIFESTVERSION, "2" );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_SYMBOLICNAME, APP.getName() );
        manifest.getMainAttributes().putValue( Constants.BUNDLE_VERSION, "1.0.0" );
        manifest.getMainAttributes().putValue( "X-Bundle-Type", "application" );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JarOutputStream jar = new JarOutputStream( out, manifest ))
        {
            jar.putNextEntry( new JarEntry( "enonic.yaml" ) );
            jar.write( "kind: \"Application\"\ntitle: \"Bench\"\n".getBytes( StandardCharsets.UTF_8 ) );
            jar.closeEntry();
            for ( final Map.Entry<String, String> entry : bundleResources.entrySet() )
            {
                jar.putNextEntry( new JarEntry( entry.getKey() ) );
                jar.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
                jar.closeEntry();
            }
        }
        return out.toByteArray();
    }

    private void createApplication( final int contentTypes, final ApplicationRepoServiceImpl repoService )
    {
        if ( !local )
        {
            repoService.createApplicationNode( APP );
            repoService.upsertApplicationDescriptor( APP, "kind: \"Application\"\ntitle: \"Bench\"\n", IconUpdate.KEEP );
            dynamicSchemaService.createOrUpdateCms( CreateOrUpdateCmsParams.create().key( APP ).resource( "kind: \"CMS\"\n" ).build() );
        }
        bundleResources.put( "cms/cms.yaml", "kind: \"CMS\"\n" );

        for ( int i = 0; i < FRAGMENTS; i++ )
        {
            writeFragment( i, "Label" );
        }

        for ( int i = 0; i < contentTypes; i++ )
        {
            final boolean withFragments = i % 2 == 0;
            final ContentTypeName name = ContentTypeName.from( APP, "type" + i );
            final String form = inputs( "field", 10, "Label" ) + ( withFragments
                ? "  - include: \"fragment" + ( i % FRAGMENTS ) + "\"\n  - include: \"fragment" + ( ( i + 1 ) % FRAGMENTS ) + "\"\n"
                : "" );
            final String resource = "kind: \"ContentType\"\nsuperType: \"base:structured\"\ntitle:\n  text: \"Type " + i + "\"\nform:\n" + form;
            if ( !local )
            {
                dynamicSchemaService.createOrUpdateContentType(
                    CreateOrUpdateContentSchemaParams.create().name( name ).resource( resource ).build() );
            }
            bundleResources.put( "cms/content-types/" + name.getLocalName() + "/" + name.getLocalName() + ".yaml", resource );
            ( withFragments ? typesWithFragments : plainTypes ).add( name );
        }

        for ( int i = 0; i < Math.max( 1, contentTypes / 5 ); i++ )
        {
            final DescriptorKey key = DescriptorKey.from( APP, "part" + i );
            final String form = inputs( "field", 5, "Label" ) + ( i % 2 == 0 ? "  - include: \"fragment" + ( i % FRAGMENTS ) + "\"\n" : "" );
            final String resource = "kind: \"Part\"\ntitle:\n  text: \"Part " + i + "\"\nform:\n" + form;
            if ( !local )
            {
                dynamicSchemaService.createOrUpdatePart( CreateOrUpdateComponentParams.create().key( key ).resource( resource ).build() );
            }
            bundleResources.put( "cms/parts/" + key.getName() + "/" + key.getName() + ".yaml", resource );
            parts.add( key );
        }
    }

    /**
     * Rewrites a form fragment with labels differing from the previous write, as a schema edit through Lib Schema does.
     */
    void writeFragment( final int fragment )
    {
        if ( local )
        {
            throw new IllegalStateException( "the schema of a local application is not written" );
        }
        fragmentWrites.increment();
        adminContext().runWith( () -> writeFragment( fragment, "Label " + fragmentWrites.sum() ) );
    }

    private void writeFragment( final int fragment, final String label )
    {
        final String resource = "kind: \"FormFragment\"\ntitle: \"Fragment\"\nform:\n" + inputs( "f" + fragment, 5, label );
        if ( !local )
        {
            dynamicSchemaService.createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams.create()
                                                                 .name( FormFragmentName.from( APP, "fragment" + fragment ) )
                                                                 .resource( resource )
                                                                 .build() );
        }
        bundleResources.putIfAbsent( "cms/form-fragments/fragment" + fragment + "/fragment" + fragment + ".yaml", resource );
    }

    private static String inputs( final String prefix, final int count, final String label )
    {
        final StringBuilder form = new StringBuilder();
        for ( int i = 0; i < count; i++ )
        {
            form.append( "  - type: \"TextLine\"\n    name: \"" )
                .append( prefix )
                .append( i )
                .append( "\"\n    label: \"" )
                .append( label )
                .append( "\"\n" );
        }
        return form.toString();
    }

    /**
     * Prints the node reads of the resolvers per operation counted by the benchmark.
     */
    void printNodeReads( final String benchmark, final String params, final long ops )
    {
        final long count = Math.max( 1, ops );
        final Map<String, String> perOp = new TreeMap<>();
        long total = 0;
        for ( final Map.Entry<String, LongAdder> entry : nodeReads.entrySet() )
        {
            total += entry.getValue().sum();
            perOp.put( entry.getKey(), String.format( "%.2f (%d)", entry.getValue().sum() / (double) count, entry.getValue().sum() ) );
        }
        System.out.printf( "%n[node reads/op] %s %s: total=%.2f %s (ops=%d, fragment writes=%d)%n", benchmark, params, total / (double) count,
                           perOp, count, fragmentWrites.sum() );
    }

    void stop()
        throws Exception
    {
        felix.stop();
        felix.waitForStop( 10_000 );
        bs.stop();
    }

    private static Context adminContext()
    {
        return ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .build();
    }

    private NodeService counting( final NodeService delegate, final String prefix )
    {
        return (NodeService) Proxy.newProxyInstance( NodeService.class.getClassLoader(), new Class<?>[]{NodeService.class},
                                                     ( proxy, method, args ) -> {
                                                         final String name = method.getName();
                                                         if ( name.startsWith( "get" ) || name.equals( "list" ) ||
                                                             name.equals( "nodeExists" ) || name.startsWith( "find" ) )
                                                         {
                                                             nodeReads.computeIfAbsent( prefix + name, k -> new LongAdder() ).increment();
                                                         }
                                                         try
                                                         {
                                                             return method.invoke( delegate, args );
                                                         }
                                                         catch ( InvocationTargetException e )
                                                         {
                                                             throw e.getCause();
                                                         }
                                                     } );
    }
}
