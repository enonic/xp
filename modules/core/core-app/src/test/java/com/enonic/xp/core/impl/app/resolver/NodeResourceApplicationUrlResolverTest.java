package com.enonic.xp.core.impl.app.resolver;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeResourceApplicationUrlResolverTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

    @Mock
    private NodeService nodeService;

    private NodeResourceApplicationUrlResolver resolver;

    @BeforeEach
    void setup()
    {
        this.resolver = new NodeResourceApplicationUrlResolver( APP_KEY, this.nodeService );
    }

    @Test
    void findFiles_lists_the_cms_subtree()
    {
        when( this.nodeService.list( any() ) ).thenReturn( result( "/myapp/cms/content-types/mytype/content-types" ) );

        this.resolver.findFiles();

        final ArgumentCaptor<ListNodesParams> params = ArgumentCaptor.forClass( ListNodesParams.class );
        verify( this.nodeService ).list( params.capture() );

        assertEquals( new NodePath( "/myapp/cms" ), params.getValue().getParentPath() );
        assertTrue( params.getValue().isRecursive() );
    }

    @Test
    void findFiles_returns_resources_relative_to_the_application()
    {
        when( this.nodeService.list( any() ) ).thenReturn(
            result( "/myapp/cms/content-types/mytype/content-types", "/myapp/cms/parts/mypart/parts" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/content-types", "/cms/parts/mypart/parts" ), this.resolver.findFiles() );
    }

    @Test
    void findFiles_skips_the_folders_on_the_way_to_a_resource()
    {
        when( this.nodeService.list( any() ) ).thenReturn(
            result( "/myapp/cms/content-types", "/myapp/cms/content-types/mytype", "/myapp/cms/content-types/mytype/content-types" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/content-types" ), this.resolver.findFiles() );
    }

    @Test
    void findResource_returns_the_resource_string_property()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "resource", "resource text" );

        final Node node = Node.create()
            .id( new NodeId() )
            .name( "mytype.yaml" )
            .parentPath( new NodePath( "/myapp/cms/content-types/mytype" ) )
            .data( data )
            .timestamp( Instant.ofEpochMilli( 1690000000000L ) )
            .build();

        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.yaml" ) ) ).thenReturn( node );

        final Resource resource = this.resolver.findResource( "/cms/content-types/mytype/mytype.yaml" );

        assertEquals( "resource text", resource.readString() );
        assertEquals( 1690000000000L, resource.getTimestamp() );
    }

    @Test
    void findResource_returns_the_attached_binary_for_icon_nodes()
        throws Exception
    {
        final byte[] iconData = {(byte) 0x89, 'P', 'N', 'G'};

        final PropertyTree data = new PropertyTree();
        data.setString( "mimeType", "image/png" );

        final NodeId nodeId = new NodeId();
        final Node node = Node.create()
            .id( nodeId )
            .name( "mytype.png" )
            .parentPath( new NodePath( "/myapp/cms/content-types/mytype" ) )
            .data( data )
            .attachedBinaries(
                AttachedBinaries.from( List.of( new AttachedBinary( BinaryReference.from( "icon" ), "blobKey" ) ) ) )
            .timestamp( Instant.ofEpochMilli( 1690000000000L ) )
            .build();

        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/content-types/mytype/mytype.png" ) ) ).thenReturn( node );
        when( this.nodeService.getBinary( nodeId, BinaryReference.from( "icon" ) ) ).thenReturn( ByteSource.wrap( iconData ) );

        final Resource resource = this.resolver.findResource( "/cms/content-types/mytype/mytype.png" );

        assertArrayEquals( iconData, resource.readBytes() );
        assertEquals( 1690000000000L, resource.getTimestamp() );
    }

    @Test
    void findResource_returns_null_for_a_missing_node()
    {
        assertNull( this.resolver.findResource( "/cms/content-types/mytype/mytype.yaml" ) );
    }

    private static ListNodesResult result( final String... paths )
    {
        final ListNodesResult.Builder builder = ListNodesResult.create();
        for ( final String path : paths )
        {
            builder.addEntry( new NodeListEntry( new NodeId(), new NodePath( path ), Instant.now() ) );
        }
        return builder.build();
    }
}
