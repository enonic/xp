package com.enonic.xp.core.impl.app.resolver;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.SchemaNodePropertyNames;

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

    @Test
    void findFiles_lists_the_cms_subtree()
    {
        when( this.nodeService.list( any() ) ).thenAnswer( invocation -> result( "/myapp/cms/content-types/mytype/mytype.yaml" ) );

        virtualAppResolver().findFiles();

        final ArgumentCaptor<ListNodesParams> params = ArgumentCaptor.forClass( ListNodesParams.class );
        verify( this.nodeService ).list( params.capture() );

        assertEquals( new NodePath( "/myapp/cms" ), params.getValue().getParentPath() );
    }

    @Test
    void findFiles_returns_resources_relative_to_the_application()
    {
        when( this.nodeService.list( any() ) ).thenAnswer(
            invocation -> result( "/myapp/cms/content-types/mytype/mytype.yaml", "/myapp/cms/parts/mypart/mypart.yaml" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/mytype.yaml", "/cms/parts/mypart/mypart.yaml" ), virtualAppResolver().findFiles() );
    }

    @Test
    void findFiles_skips_the_folders_on_the_way_to_a_resource()
    {
        when( this.nodeService.list( any() ) ).thenAnswer( invocation -> result( "/myapp/cms/content-types", "/myapp/cms/content-types/mytype",
                                                                                  "/myapp/cms/content-types/mytype/mytype.yaml",
                                                                                  "/myapp/cms/i18n", "/myapp/cms/i18n/phrases",
                                                                                  "/myapp/cms/i18n/phrases/phrases_en.properties" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/mytype.yaml", "/cms/i18n/phrases/phrases_en.properties" ),
                      virtualAppResolver().findFiles() );
    }

    @Test
    void findFiles_lists_cms_and_style_descriptors()
    {
        when( this.nodeService.list( any() ) ).thenAnswer(
            invocation -> result( "/myapp/cms/cms.yaml", "/myapp/cms/style", "/myapp/cms/style/style.yaml" ) );

        assertEquals( Set.of( "/cms/cms.yaml", "/cms/style/style.yaml" ), virtualAppResolver().findFiles() );
    }

    @Test
    void findFiles_relative_to_a_nested_application_node()
    {
        when( this.nodeService.list( any() ) ).thenAnswer(
            invocation -> result( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) );

        final NodeResourceApplicationUrlResolver resolver = staticAppResolver();

        assertEquals( Set.of( "/cms/content-types/mytype/mytype.yaml" ), resolver.findFiles() );

        final ArgumentCaptor<ListNodesParams> params = ArgumentCaptor.forClass( ListNodesParams.class );
        verify( this.nodeService ).list( params.capture() );
        assertEquals( new NodePath( "/applications/myapp/cms" ), params.getValue().getParentPath() );
    }

    @Test
    void findResource_returns_the_resource_property()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, "kind: \"ContentType\"" );
        final Node node = Node.create()
            .id( new NodeId() )
            .parentPath( new NodePath( "/applications/myapp/cms/content-types/mytype" ) )
            .name( "mytype.yaml" )
            .data( data )
            .timestamp( Instant.now() )
            .build();
        when( this.nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.yaml" ) ) ).thenReturn( node );

        final Resource resource = staticAppResolver().findResource( "/cms/content-types/mytype/mytype.yaml" );

        assertEquals( "myapp:/cms/content-types/mytype/mytype.yaml", resource.getKey().toString() );
        assertEquals( "kind: \"ContentType\"", resource.readString() );
        assertEquals( "node", resource.getResolverName() );
        assertTrue( resource.exists() );
    }

    @Test
    void findResource_returns_the_attached_binary_for_icon_nodes()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.MIME_TYPE, "image/svg+xml" );
        data.setBinaryReference( SchemaNodePropertyNames.ICON, VirtualAppConstants.ICON_BINARY_REFERENCE );
        final NodeId nodeId = new NodeId();
        final Node node = Node.create()
            .id( nodeId )
            .parentPath( new NodePath( "/applications/myapp/cms/content-types/mytype" ) )
            .name( "mytype.svg" )
            .data( data )
            .timestamp( Instant.now() )
            .attachedBinaries( AttachedBinaries.create()
                                   .add( new AttachedBinary( VirtualAppConstants.ICON_BINARY_REFERENCE, "blobkey" ) )
                                   .build() )
            .build();
        when( this.nodeService.getByPath( new NodePath( "/applications/myapp/cms/content-types/mytype/mytype.svg" ) ) ).thenReturn( node );
        when( this.nodeService.getBinary( nodeId, VirtualAppConstants.ICON_BINARY_REFERENCE ) ).thenReturn(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        final Resource resource = staticAppResolver().findResource( "/cms/content-types/mytype/mytype.svg" );

        assertEquals( "<svg/>", resource.readString() );
        assertEquals( "node", resource.getResolverName() );
    }

    @Test
    void findResource_returns_null_for_a_missing_node()
    {
        assertNull( staticAppResolver().findResource( "/cms/content-types/missing/missing.yaml" ) );
    }

    @Test
    void findResource_returns_null_outside_cms()
    {
        assertNull( staticAppResolver().findResource( "/assets/app.js" ) );
        verify( this.nodeService, org.mockito.Mockito.never() ).getByPath( any() );
    }

    private NodeResourceApplicationUrlResolver virtualAppResolver()
    {
        return NodeResourceApplicationUrlResolver.forVirtualApp( APP_KEY, this.nodeService );
    }

    private NodeResourceApplicationUrlResolver staticAppResolver()
    {
        return new NodeResourceApplicationUrlResolver( APP_KEY, this.nodeService, new NodePath( "/applications/myapp" ),
                                                       () -> ContextBuilder.create().build() );
    }

    private static Stream<NodeListEntry> result( final String... paths )
    {
        return Stream.of( paths ).map( path -> new NodeListEntry( new NodeId(), new NodePath( path ), Instant.now() ) );
    }
}