package com.enonic.xp.core.impl.app.resource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.MockApplication;
import com.enonic.xp.core.impl.app.ResourceSource;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.resource.MultiResourceProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResourceServiceImplTest
{
    @TempDir
    Path temporaryFolder;

    ApplicationKey appKey;

    ResourceServiceImpl resourceService;

    ApplicationFactoryService applicationFactoryService;

    Bundle bundle;

    Path appDir;

    @BeforeEach
    void setup()
        throws Exception
    {
        appDir = Files.createDirectory( this.temporaryFolder.resolve( "myapp" ) );

        appKey = ApplicationKey.from( "myapp" );
        applicationFactoryService = mock( ApplicationFactoryService.class );

        bundle = mock( Bundle.class );
        when( bundle.getHeaders() ).thenReturn( Dictionaries.of() );
        when( bundle.getSymbolicName() ).thenReturn( "myapp" );

        final MockApplication app = new MockApplication();
        app.setStarted( true );
        app.setBundle( bundle );
        app.setResourcePath( appDir );

        when( applicationFactoryService.findActiveApplication( appKey ) ).thenReturn( Optional.of( app ) );
        when( applicationFactoryService.findResourceSource( any(), any() ) ).thenReturn( ResourceSource.NONE );
        when( applicationFactoryService.findResourceSource( appKey, null ) ).thenReturn( new ResourceSource( bundle, app.getUrlResolver() ) );

        resourceService = new ResourceServiceImpl( applicationFactoryService );
    }

    private Bundle newBundle( final long bundleId )
    {
        final Bundle newBundle = mock( Bundle.class );
        when( newBundle.getBundleId() ).thenReturn( bundleId );
        return newBundle;
    }

    private void newFile( final String name )
        throws Exception
    {
        final Path file = appDir.resolve( name );
        Files.createDirectories( file.getParent() );
        Files.createFile( file );
    }

    @Test
    void testFindFiles()
        throws Exception
    {
        newFile( "a.txt" );
        newFile( "b/c.txt" );
        newFile( "c/d/e.png" );

        final ResourceKeys keys1 = this.resourceService.findFiles( this.appKey, ".+" );
        assertThat( keys1 ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp:/b/c.txt", "myapp:/a.txt", "myapp:/c/d/e.png" );

        final ResourceKeys keys2 = this.resourceService.findFiles( this.appKey, "b/c\\.txt" );
        assertThat( keys2 ).map( Objects::toString ).containsExactly( "myapp:/b/c.txt" );

        final ResourceKeys keys3 = this.resourceService.findFiles( this.appKey, ".+\\.txt" );
        assertThat( keys3 ).map( Objects::toString ).containsExactlyInAnyOrder( "myapp:/b/c.txt", "myapp:/a.txt" );
    }

    private String processResource( final String segment, final String key, final String suffix )
    {
        final ResourceProcessor.Builder<String, String> processor = new ResourceProcessor.Builder<>();
        processor.key( key );
        processor.keyTranslator( name -> ResourceKey.from( "myapp:/" + name ) );
        processor.segment( segment );
        processor.processor( res -> res.getKey().toString() + "->" + suffix );

        return this.resourceService.processResource( processor.build() );
    }

    @Test
    void testProcessResource()
        throws Exception
    {
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value1 = processResource( "segment1", "a.txt", "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        final String value2 = processResource( "segment1", "a.txt", "2" );
        assertEquals( value1, value2 );

        // a new bundle incarnation of the application invalidates cached entries
        final Bundle newBundle = newBundle( 42 );
        doReturn( new ResourceSource( newBundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value3 = processResource( "segment1", "a.txt", "3" );
        assertEquals( "myapp:/a.txt->3", value3 );

        final String value4 = processResource( "segment1", "a.txt", "4" );
        assertEquals( value3, value4 );

        final String value5 = processResource( "segment2", "a.txt", "5" );
        assertEquals( "myapp:/a.txt->5", value5 );
    }

    // the application node is read on every use of a persisted application, hit or miss: its version is the stamp of the value
    @Test
    void applicationNode_readOnEveryUse()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        processResource( "segment1", "cms/parts/my-part.yaml", "1" );
        verify( nodeService, times( 1 ) ).getByPath( new NodePath( "/myapp" ) );

        processResource( "segment1", "cms/parts/my-part.yaml", "2" );
        verify( nodeService, times( 2 ) ).getByPath( new NodePath( "/myapp" ) );
    }

    // an application served from its bundle alone (a local one, a system one) has no node to read: the resolver knows its source
    @Test
    void applicationNode_notReadForBundleResolver()
        throws Exception
    {
        newFile( "a.txt" );
        final NodeService nodeService = mock( NodeService.class );
        when( bundle.findEntries( "/", "*", true ) ).thenReturn( Collections.emptyEnumeration() );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        assertEquals( "myapp:/a.txt->1", processResource( "segment1", "a.txt", "1" ) );
        assertEquals( "myapp:/a.txt->1", processResource( "segment1", "a.txt", "2" ) );
        this.resourceService.findFiles( this.appKey, ".+" );

        verify( nodeService, never() ).getByPath( any() );
    }

    @Test
    void testProcessResource_bundleResolverIgnoresTimestamps()
        throws Exception
    {
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value1 = processResource( "segment1", "a.txt", "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        // timestamps of bundle-backed resources are unreliable (constant with reproducible builds):
        // under the same bundle they are not even probed
        Files.setLastModifiedTime( appDir.resolve( "a.txt" ), FileTime.fromMillis( System.currentTimeMillis() + 10_000 ) );

        final String value2 = processResource( "segment1", "a.txt", "2" );
        assertEquals( value1, value2 );

        // a new bundle is the only signal that files may be new
        final Bundle newBundle = newBundle( 42 );
        doReturn( new ResourceSource( newBundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value3 = processResource( "segment1", "a.txt", "3" );
        assertEquals( "myapp:/a.txt->3", value3 );
    }

    @Test
    void testProcessResources_bundleResolverIgnoresTimestamps()
        throws Exception
    {
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final List<String> names = List.of( "a.txt", "b.txt" );

        final String value1 = processResources( "segment1", "bundle", names, "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        Files.setLastModifiedTime( appDir.resolve( "a.txt" ), FileTime.fromMillis( System.currentTimeMillis() + 10_000 ) );

        final String value2 = processResources( "segment1", "bundle", names, "2" );
        assertEquals( value1, value2 );

        final Bundle newBundle = newBundle( 42 );
        doReturn( new ResourceSource( newBundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value3 = processResources( "segment1", "bundle", names, "3" );
        assertEquals( "myapp:/a.txt->3", value3 );
    }

    @Test
    void testProcessResourceWithParticularResolver()
        throws Exception
    {
        newFile( "a.txt" );

        final Instant timestamp = Instant.parse( "2021-12-03T10:15:30.00Z" );

        final PropertyTree data = new PropertyTree();
        data.addXml( "resource", "<xml><my-xml hello='world'/></xml>" );

        final Node appNode = createNode( "myapp", new NodePath( "/myapp" ), timestamp, new PropertyTree() );
        final Node partSchemaNode = createNode( "part:a", new NodePath( "/myapp/cms" ), timestamp, data );

        final NodeService nodeService = mock( NodeService.class );

        when( nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn( appNode );
        when( nodeService.getByPath( new NodePath( "/myapp/cms/part:a" ) ) ).thenReturn( partSchemaNode );

        ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( ResourceConstants.RESOURCE_SOURCE_ATTRIBUTE, "node" )
            .build()
            .runWith( () -> {
                assertNull( processResource( "segment1", "/cms/parts/a.yaml", "1" ) );

                final ApplicationUrlResolver applicationUrlResolver = nodeResolver( nodeService );

                doReturn( new ResourceSource( bundle, applicationUrlResolver ) ).when( applicationFactoryService )
                    .findResourceSource( ApplicationKey.from( "myapp" ), "node" );

                final String value1 = processResource( "segment1", "/cms/parts/a.yaml", "1" );
                assertEquals( "myapp:/cms/parts/a.yaml->1", value1 );
            } );
    }

    // a read asking for the bundle alone, by the attribute of its context, is a value of its own: what was read from the persisted
    // schema is neither served for it nor evicted by it
    @Test
    void resolverSource_isPartOfTheCacheIdentity()
        throws Exception
    {
        newFile( "cms/parts/my-part.yaml" );
        when( bundle.getResource( "/cms/parts/my-part.yaml" ) ).thenReturn( appDir.resolve( "cms/parts/my-part.yaml" ).toUri().toURL() );
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, "bundle" );

        final AtomicInteger computed = new AtomicInteger();
        final ResourceProcessor<String, String> processor = new ResourceProcessor.Builder<String, String>().key( "my-part" )
            .segment( "segment1" )
            .keyTranslator( name -> ResourceKey.from( "myapp:/cms/parts/my-part.yaml" ) )
            .processor( res -> {
                computed.incrementAndGet();
                return res.getResolverName();
            } )
            .build();
        final Context bundleSource =
            ContextBuilder.copyOf( ContextAccessor.current() ).attribute( ResourceConstants.RESOURCE_SOURCE_ATTRIBUTE, "bundle" ).build();

        assertEquals( "node", this.resourceService.processResource( processor ) );
        assertEquals( "bundle", bundleSource.callWith( () -> this.resourceService.processResource( processor ) ) );
        assertEquals( "node", this.resourceService.processResource( processor ) );
        assertEquals( "bundle", bundleSource.callWith( () -> this.resourceService.processResource( processor ) ) );
        assertEquals( 2, computed.get() );
    }

    @Test
    void testProcessProjectResource()
        throws Exception
    {
        final ProjectName projectName = ProjectName.from( "my-project" );
        final Instant timestamp = Instant.parse( "2021-12-03T10:15:30.00Z" );

        final PropertyTree data = new PropertyTree();
        data.addXml( "resource", "<xml><my-xml hello='world'/></xml>" );

        final Node appNode = createNode( "myapp", new NodePath( "/myapp" ), timestamp, new PropertyTree() );

        final Node partSchemaNode = createNode( "part:my-part", new NodePath( "/myapp/cms" ), timestamp, data );

        when( bundle.getHeaders() ).thenReturn( Dictionaries.of() );

        final RepositoryService repositoryService = mock( RepositoryService.class );
        final NodeService nodeService = mock( NodeService.class );

        when( repositoryService.get( projectName.getRepoId() ) ).thenAnswer( invocation -> {
            final RepositoryId repositoryId = invocation.getArgument( 0 );
            return Repository.create()
                .id( repositoryId )
                .branches( Branches.from( Branch.from( "master" ) ) )
                .build();
        } );

        when( nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn( appNode );
        when( nodeService.getByPath( new NodePath( "/myapp/cms/part:my-part" ) ) ).thenReturn( partSchemaNode );

        final ApplicationUrlResolver applicationUrlResolver = nodeResolver( nodeService );

        doReturn( new ResourceSource( bundle, applicationUrlResolver ) ).when( applicationFactoryService )
            .findResourceSource( ApplicationKey.from( "myapp" ), null );

        final Resource resource =
            resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "/cms/parts/my-part.yaml" ) );

        assertEquals( "node", resource.getResolverName() );

        assertEquals( timestamp.toEpochMilli(), resource.getTimestamp() );

        final String bytes = new String( resource.getBytes().read(), StandardCharsets.UTF_8 );
        assertEquals( data.getString( "resource" ), bytes );
        assertEquals( 34, resource.getSize() );

        final String value = processResource( "segment1", "cms/parts/my-part.yaml", "1" );
        assertEquals( "myapp:/cms/parts/my-part.yaml->1", value );
    }

    // resources of "myapp" stored as nodes below the application node /myapp
    private static ApplicationUrlResolver nodeResolver( final NodeService nodeService )
    {
        return new NodeResourceApplicationUrlResolver( ApplicationKey.from( "myapp" ), nodeService, new NodePath( "/myapp" ),
                                                       ContextAccessor::current );
    }

    @Test
    void testProcessResource_notFound()
    {
        final String value = processResource( "segment1", "a.txt", "1" );
        assertNull( value );
    }

    private String processResources( final String segment, final String key, final List<String> names, final String suffix )
    {
        final MultiResourceProcessor<String, String> processor = new MultiResourceProcessor.Builder<String, String>().key( key )
            .segment( segment )
            .keysTranslator( k -> names.stream().map( name -> ResourceKey.from( "myapp:/" + name ) ).toList() )
            .processor( resources -> resources.stream()
                .filter( Resource::exists )
                .map( res -> res.getKey().toString() )
                .collect( Collectors.joining( ",", "", "->" + suffix ) ) )
            .build();

        return this.resourceService.processResources( processor );
    }

    @Test
    void testProcessResources()
        throws Exception
    {
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final List<String> names = List.of( "a.txt", "b.txt" );

        final String value1 = processResources( "segment1", "bundle", names, "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        final String value2 = processResources( "segment1", "bundle", names, "2" );
        assertEquals( value1, value2 );

        // a new bundle incarnation of the application invalidates the cached value: a resource that appeared with it is seen
        newFile( "b.txt" );
        when( bundle.getResource( "/b.txt" ) ).thenReturn( appDir.resolve( "b.txt" ).toUri().toURL() );
        final Bundle newBundle = newBundle( 42 );
        doReturn( new ResourceSource( newBundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value3 = processResources( "segment1", "bundle", names, "3" );
        assertEquals( "myapp:/a.txt,myapp:/b.txt->3", value3 );

        final String value4 = processResources( "segment1", "bundle", names, "4" );
        assertEquals( value3, value4 );
    }

    @Test
    void testProcessResources_nullValueNotCached()
        throws Exception
    {
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final MultiResourceProcessor<String, String> processor = new MultiResourceProcessor.Builder<String, String>().key( "bundle" )
            .segment( "segment1" )
            .keysTranslator( k -> List.of( ResourceKey.from( "myapp:/a.txt" ) ) )
            .processor( resources -> null )
            .build();

        assertNull( this.resourceService.processResources( processor ) );
        assertNull( this.resourceService.processResources( processor ) );
    }

    @Test
    void testProcessResources_allMissingCachedUnderTheStamp()
        throws Exception
    {
        doReturn( new ResourceSource( bundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );
        final List<String> names = List.of( "a.txt" );

        final String value1 = processResources( "segment1", "bundle", names, "1" );
        assertEquals( "->1", value1 );

        final String value2 = processResources( "segment1", "bundle", names, "2" );
        assertEquals( value1, value2 );

        // the resource appears with a new bundle
        newFile( "a.txt" );
        when( bundle.getResource( "/a.txt" ) ).thenReturn( appDir.resolve( "a.txt" ).toUri().toURL() );
        final Bundle newBundle = newBundle( 42 );
        doReturn( new ResourceSource( newBundle, new BundleApplicationUrlResolver( bundle ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value3 = processResources( "segment1", "bundle", names, "3" );
        assertEquals( "myapp:/a.txt->3", value3 );
    }

    @Test
    void nodeResolver_hitCostsNoNodeRead()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final AtomicInteger translations = new AtomicInteger();
        final String value1 = processResource( "segment1", "cms/parts/my-part.yaml", "1", translations );
        assertEquals( "myapp:/cms/parts/my-part.yaml->1", value1 );
        verify( nodeService, times( 1 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );

        // the persisted resources are stable under the generation: neither the key translation nor the node is looked at again
        final String value2 = processResource( "segment1", "cms/parts/my-part.yaml", "2", translations );
        assertEquals( value1, value2 );
        verify( nodeService, times( 1 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );
        assertEquals( 1, translations.get() );
    }

    @Test
    void nodeResolver_newApplicationNodeVersionRecomputes()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value1 = processResource( "segment1", "cms/parts/my-part.yaml", "1" );
        assertEquals( "myapp:/cms/parts/my-part.yaml->1", value1 );

        // a write of the schema moves the version of the application node, whatever the timestamps of the nodes
        stubPartNode( nodeService, Instant.parse( "2021-12-03T10:15:30.00Z" ), NodeVersionId.from( "v2" ) );

        final String value2 = processResource( "segment1", "cms/parts/my-part.yaml", "2" );
        assertEquals( "myapp:/cms/parts/my-part.yaml->2", value2 );
        verify( nodeService, times( 2 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );

        // the same version again is a hit
        assertEquals( value2, processResource( "segment1", "cms/parts/my-part.yaml", "3" ) );
        verify( nodeService, times( 2 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );
    }

    // a computation finishing after a newer one filled the cache replaces the newer value, but under the stamp it was computed
    // under: the next read rejects it and recomputes, nothing older than the current version is ever served
    @Test
    void nodeResolver_lateComputationCannotFillTheCacheWithAStaleValue()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final AtomicInteger computed = new AtomicInteger();
        final AtomicReference<String> stored = new AtomicReference<>( "v1" );
        final ResourceProcessor<String, String> late = new ResourceProcessor.Builder<String, String>().key( "my-part" )
            .segment( "segment1" )
            .keyTranslator( name -> ResourceKey.from( "myapp:/cms/parts/my-part.yaml" ) )
            .processor( res -> {
                final String value = stored.get();
                // the schema is written while this computation is under way, and a reader of the new version fills the cache
                stubPartNode( nodeService, Instant.parse( "2021-12-03T10:15:30.00Z" ), NodeVersionId.from( "v2" ) );
                stored.set( "v2" );
                assertEquals( "v2", processResourceNamed( "segment1", "my-part", stored, computed ) );
                return value;
            } )
            .build();

        // the late computation returns what it read, under the old version
        assertEquals( "v1", this.resourceService.processResource( late ) );
        assertEquals( 1, computed.get() );

        // what it left in the cache is stamped with the old version: the next read computes the current value again, once
        assertEquals( "v2", processResourceNamed( "segment1", "my-part", stored, computed ) );
        assertEquals( 2, computed.get() );
        assertEquals( "v2", processResourceNamed( "segment1", "my-part", new AtomicReference<>( "v3" ), computed ) );
        assertEquals( 2, computed.get() );
    }

    private String processResourceNamed( final String segment, final String key, final AtomicReference<String> stored,
                                         final AtomicInteger computed )
    {
        return this.resourceService.processResource( new ResourceProcessor.Builder<String, String>().key( key )
                                                         .segment( segment )
                                                         .keyTranslator( name -> ResourceKey.from( "myapp:/cms/parts/" + name + ".yaml" ) )
                                                         .processor( res -> {
                                                             computed.incrementAndGet();
                                                             return stored.get();
                                                         } )
                                                         .build() );
    }

    @Test
    void nodeResolver_timestampChangeAloneIsNoSignal()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final String value1 = processResource( "segment1", "cms/parts/my-part.yaml", "1" );

        // a node rewritten under the same version of the application node is not seen until the version moves: writers move it
        stubPartNode( nodeService, Instant.parse( "2022-01-01T00:00:00.00Z" ) );
        assertEquals( value1, processResource( "segment1", "cms/parts/my-part.yaml", "2" ) );

        stubPartNode( nodeService, Instant.parse( "2022-01-01T00:00:00.00Z" ), NodeVersionId.from( "v2" ) );
        assertEquals( "myapp:/cms/parts/my-part.yaml->3", processResource( "segment1", "cms/parts/my-part.yaml", "3" ) );
    }

    // the application in development is served by its classloader: files change without any signal, so nothing derived from them
    // is cached
    @Test
    void classLoaderResolver_notCached()
        throws Exception
    {
        newFile( "a.txt" );

        assertEquals( "myapp:/a.txt->1", processResource( "segment1", "a.txt", "1" ) );
        assertEquals( "myapp:/a.txt->2", processResource( "segment1", "a.txt", "2" ) );

        final List<String> names = List.of( "a.txt", "b.txt" );
        assertEquals( "myapp:/a.txt->1", processResources( "segment1", "multi", names, "1" ) );
        newFile( "b.txt" );
        assertEquals( "myapp:/a.txt,myapp:/b.txt->2", processResources( "segment1", "multi", names, "2" ) );
    }

    // the key translation of a schema prefers .yaml to .yml: in development, a .yaml appearing beside the .yml is seen
    @Test
    void classLoaderResolver_descriptorAppearingWhereTheTranslationLooksFirstIsSeen()
        throws Exception
    {
        newFile( "a.yml" );
        final ResourceProcessor<String, String> processor = new ResourceProcessor.Builder<String, String>().key( "a" )
            .segment( "segment1" )
            .keyTranslator( name -> {
                final ResourceKey yaml = ResourceKey.from( "myapp:/" + name + ".yaml" );
                return this.resourceService.getResource( yaml ).exists() ? yaml : ResourceKey.from( "myapp:/" + name + ".yml" );
            } )
            .processor( res -> res.getKey().toString() )
            .build();

        assertEquals( "myapp:/a.yml", this.resourceService.processResource( processor ) );

        newFile( "a.yaml" );

        assertEquals( "myapp:/a.yaml", this.resourceService.processResource( processor ) );
    }

    @Test
    void processor_readsOtherResourcesThroughTheService()
    {
        // a content type inlining its form fragments reads them through this service while being computed itself
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        doReturn( new ResourceSource( bundle, nodeResolver( nodeService ) ) ).when( applicationFactoryService )
            .findResourceSource( appKey, null );

        final ResourceProcessor<String, String> outer = new ResourceProcessor.Builder<String, String>().key( "outer" )
            .segment( "segment1" )
            .keyTranslator( name -> ResourceKey.from( "myapp:/cms/parts/my-part.yaml" ) )
            .processor( res -> "outer(" + processResource( "segment1", "cms/parts/my-part.yaml", "inner" ) + ")" )
            .build();

        // computed: both values read the node, the outer and the inner one
        assertEquals( "outer(myapp:/cms/parts/my-part.yaml->inner)", this.resourceService.processResource( outer ) );
        verify( nodeService, times( 2 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );

        // stable: what the outer value read is not looked at again on a hit
        assertEquals( "outer(myapp:/cms/parts/my-part.yaml->inner)", this.resourceService.processResource( outer ) );
        verify( nodeService, times( 2 ) ).getByPath( new NodePath( "/myapp/cms/part:my-part" ) );
    }

    @Test
    void findFiles_nodeResolverListedOncePerApplicationNodeVersion()
    {
        final NodeService nodeService = stubPartNode( Instant.parse( "2021-12-03T10:15:30.00Z" ) );
        final ApplicationUrlResolver resolver = spy( nodeResolver( nodeService ) );
        doReturn( new ResourceSource( bundle, resolver ) ).when( applicationFactoryService ).findResourceSource( appKey, null );

        assertThat( this.resourceService.findFiles( appKey, "\\.yaml$" ) ).map( Objects::toString )
            .containsExactly( "myapp:/cms/parts/my-part.yaml" );
        assertThat( this.resourceService.findFiles( appKey, "parts" ) ).map( Objects::toString )
            .containsExactly( "myapp:/cms/parts/my-part.yaml" );
        verify( resolver, times( 1 ) ).findFiles();

        stubPartNode( nodeService, Instant.parse( "2021-12-03T10:15:30.00Z" ), NodeVersionId.from( "v2" ) );
        this.resourceService.findFiles( appKey, ".+" );
        verify( resolver, times( 2 ) ).findFiles();
    }

    @Test
    void findFiles_classLoaderResolverListedEveryTime()
        throws Exception
    {
        newFile( "a.txt" );
        assertThat( this.resourceService.findFiles( appKey, ".+" ) ).map( Objects::toString ).containsExactly( "myapp:/a.txt" );

        newFile( "b.txt" );
        assertThat( this.resourceService.findFiles( appKey, ".+" ) ).map( Objects::toString )
            .containsExactlyInAnyOrder( "myapp:/a.txt", "myapp:/b.txt" );
    }

    private String processResource( final String segment, final String key, final String suffix, final AtomicInteger translations )
    {
        final ResourceProcessor<String, String> processor = new ResourceProcessor.Builder<String, String>().key( key )
            .segment( segment )
            .keyTranslator( name -> {
                translations.incrementAndGet();
                return ResourceKey.from( "myapp:/" + name );
            } )
            .processor( res -> res.getKey().toString() + "->" + suffix )
            .build();
        return this.resourceService.processResource( processor );
    }

    // "myapp" stored as nodes: the application node and one part below cms
    private NodeService stubPartNode( final Instant timestamp )
    {
        final NodeService nodeService = mock( NodeService.class );
        stubPartNode( nodeService, timestamp );
        return nodeService;
    }

    private void stubPartNode( final NodeService nodeService, final Instant timestamp )
    {
        stubPartNode( nodeService, timestamp, NodeVersionId.from( "v1" ) );
    }

    // the version is that of the application node, the revision of the resolver
    private void stubPartNode( final NodeService nodeService, final Instant timestamp, final NodeVersionId version )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "resource", "kind: Part" );
        final Node appNode = createNode( "myapp", new NodePath( "/myapp" ), timestamp, new PropertyTree(), version );
        final Node partSchemaNode = createNode( "part:my-part", new NodePath( "/myapp/cms" ), timestamp, data, version );
        when( nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn( appNode );
        when( nodeService.getByPath( new NodePath( "/myapp/cms/part:my-part" ) ) ).thenReturn( partSchemaNode );
        when( nodeService.list( any( ListNodesParams.class ) ) ).thenAnswer(
            invocation -> Stream.of( new NodeListEntry( partSchemaNode.id(), partSchemaNode.path(), timestamp ) ) );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenReturn( Nodes.from( partSchemaNode ) );
    }

    private Node createNode( final String name, final NodePath root, final Instant timestamp, final PropertyTree data )
    {
        return createNode( name, root, timestamp, data, null );
    }

    private Node createNode( final String name, final NodePath root, final Instant timestamp, final PropertyTree data,
                             final NodeVersionId version )
    {
        return Node.create()
            .id( NodeId.from( name ) )
            .name( NodeName.from( name ) )
            .parentPath( root )
            .timestamp( timestamp )
            .data( data )
            .nodeVersionId( version )
            .build();
    }
}
