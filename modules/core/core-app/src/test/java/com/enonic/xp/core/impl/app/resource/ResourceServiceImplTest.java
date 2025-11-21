package com.enonic.xp.core.impl.app.resource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Dictionary;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.MockApplication;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceProcessor;

import static com.enonic.xp.core.impl.app.ApplicationManifestConstants.X_PROJECT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
        when( bundle.getHeaders() ).thenReturn( mock( Dictionary.class ) );
        when( bundle.getSymbolicName() ).thenReturn( "myapp" );

        final MockApplication app = new MockApplication();
        app.setStarted( true );
        app.setBundle( bundle );
        app.setResourcePath( appDir );

        when( applicationFactoryService.findActiveApplication( appKey ) ).thenReturn( Optional.of( app ) );
        when( applicationFactoryService.findResolver( appKey, null ) ).thenReturn( Optional.of( app.getUrlResolver() ) );

        resourceService = new ResourceServiceImpl( applicationFactoryService );
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

        final String value1 = processResource( "segment1", "a.txt", "1" );
        assertEquals( "myapp:/a.txt->1", value1 );

        final String value2 = processResource( "segment1", "a.txt", "2" );
        assertEquals( value1, value2 );

        this.resourceService.invalidate( ApplicationKey.from( "myapp" ), ApplicationInvalidationLevel.FULL );

        final String value3 = processResource( "segment1", "a.txt", "3" );
        assertEquals( "myapp:/a.txt->3", value3 );

        final String value4 = processResource( "segment1", "a.txt", "4" );
        assertEquals( value3, value4 );

        final String value5 = processResource( "segment2", "a.txt", "5" );
        assertEquals( "myapp:/a.txt->5", value5 );
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
        final Node partSchemaNode = createNode( "a.xml", new NodePath( "/schemas/cms/parts/a" ), timestamp, data );

        final NodeService nodeService = mock( NodeService.class );

        when( nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn( appNode );
        when( nodeService.getByPath( new NodePath( "/myapp/cms/parts/a/a.xml" ) ) ).thenReturn( partSchemaNode );

        ContextBuilder.copyOf( ContextAccessor.current() )
            .attribute( ResourceConstants.RESOURCE_SOURCE_ATTRIBUTE, "node" )
            .build()
            .runWith( () -> {
                assertNull( processResource( "segment1", "/cms/parts/a/a.xml", "1" ) );

                final ApplicationUrlResolver applicationUrlResolver =
                    new NodeResourceApplicationUrlResolver( ApplicationKey.from( "myapp" ), nodeService );

                doReturn( Optional.of( applicationUrlResolver ) ).when( applicationFactoryService )
                    .findResolver( ApplicationKey.from( "myapp" ), "node" );

                final String value1 = processResource( "segment1", "/cms/parts/a/a.xml", "1" );
                assertEquals( "myapp:/cms/parts/a/a.xml->1", value1 );
            } );
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

        final Node partSchemaNode = createNode( "my-part.xml", new NodePath( "/schemas/cms/parts/my-part" ), timestamp, data );

        final Dictionary<String, String> headers = mock( Dictionary.class );
        when( bundle.getHeaders() ).thenReturn( headers );
        when( headers.get( X_PROJECT_NAME ) ).thenReturn( "my-project" );

        final RepositoryService repositoryService = mock( RepositoryService.class );
        final NodeService nodeService = mock( NodeService.class );

        when( repositoryService.get( projectName.getRepoId() ) ).thenAnswer( invocation -> {
            final RepositoryId repositoryId = (RepositoryId) invocation.getArguments()[0];
            return Repository.create()
                .id( repositoryId )
                .branches( Branches.from( VirtualAppConstants.VIRTUAL_APP_BRANCH, Branch.from( "master" ) ) )
                .build();
        } );

        when( nodeService.getByPath( new NodePath( "/myapp" ) ) ).thenReturn( appNode );
        when( nodeService.getByPath( new NodePath( "/myapp/cms/parts/my-part/my-part.xml" ) ) ).thenReturn(
            partSchemaNode );

        final ApplicationUrlResolver applicationUrlResolver =
            new NodeResourceApplicationUrlResolver( ApplicationKey.from( "myapp" ), nodeService );

        doReturn( Optional.of( applicationUrlResolver ) ).when( applicationFactoryService )
            .findResolver( ApplicationKey.from( "myapp" ), null );

        final Resource resource =
            resourceService.getResource( ResourceKey.from( ApplicationKey.from( "myapp" ), "/cms/parts/my-part/my-part.xml" ) );

        assertEquals( "node", resource.getResolverName() );

        assertEquals( timestamp.toEpochMilli(), resource.getTimestamp() );

        final String bytes = new String( resource.getBytes().read(), StandardCharsets.UTF_8 );
        assertEquals( data.getString( "resource" ), bytes );
        assertEquals( 34, resource.getSize() );

        final String value = processResource( "segment1", "cms/parts/my-part/my-part.xml", "1" );
        assertEquals( "myapp:/cms/parts/my-part/my-part.xml->1", value );
    }

    @Test
    void testProcessResource_notFound()
    {
        final String value = processResource( "segment1", "a.txt", "1" );
        assertNull( value );
    }

    private Node createNode( final String name, final NodePath root, final Instant timestamp, final PropertyTree data )
    {
        return Node.create().name( NodeName.from( name ) ).parentPath( root ).timestamp( timestamp ).data( data ).build();
    }
}
