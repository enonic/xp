package com.enonic.xp.core.impl.app.resolver;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeResourceApplicationUrlResolverTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

    private static final NodePath APP_PATH = new NodePath( "/myapp" );

    @Mock
    private NodeService nodeService;

    @Test
    void findFiles_lists_the_children_of_the_cms_node()
    {
        stubSchemaNodes( schemaNode( "contenttype:mytype", null ), schemaNode( "part:mypart", null ) );

        assertEquals( Set.of( "/cms/content-types/mytype.yaml", "/cms/parts/mypart.yaml" ), resolver().findFiles() );

        final ArgumentCaptor<ListNodesParams> params = ArgumentCaptor.forClass( ListNodesParams.class );
        verify( this.nodeService ).list( params.capture() );
        assertEquals( new NodePath( "/myapp/cms" ), params.getValue().getParentPath() );
    }

    @Test
    void findFiles_lists_the_application_descriptor_and_icon_from_the_application_node()
    {
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( "kind: \"Application\"", "image/png" ) );
        stubSchemaNodes();

        assertEquals( Set.of( "/enonic.yaml", "/enonic.png" ), resolver().findFiles() );
    }

    @Test
    void findFiles_lists_nothing_for_an_application_node_without_descriptor()
    {
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( null, null ) );
        stubSchemaNodes();

        assertEquals( Set.of(), resolver().findFiles() );
    }

    @Test
    void findFiles_lists_the_icons_of_the_schemas_by_their_format()
    {
        stubSchemaNodes( schemaNode( "contenttype:mytype", "icon.svg" ), schemaNode( "macro:mymacro", "icon.png" ),
                         schemaNode( "layout:mylayout", null ) );

        assertEquals( Set.of( "/cms/content-types/mytype.yaml", "/cms/content-types/mytype.svg", "/cms/macros/mymacro.yaml",
                              "/cms/macros/mymacro.png", "/cms/layouts/mylayout.yaml" ), resolver().findFiles() );
    }

    @Test
    void findFiles_lists_cms_style_and_phrases()
    {
        stubSchemaNodes( schemaNode( "cms", null ), schemaNode( "style", null ), schemaNode( "phrases:phrases_en", null ) );

        assertEquals( Set.of( "/cms/cms.yaml", "/cms/style/style.yaml", "/cms/i18n/phrases/phrases_en.properties" ), resolver().findFiles() );
    }

    @Test
    void findFiles_skips_nodes_that_are_not_schemas()
    {
        stubSchemaNodes( schemaNode( "contenttype:mytype", null ), schemaNode( "stray", null ), schemaNode( "Unknown.x", null ) );

        assertEquals( Set.of( "/cms/content-types/mytype.yaml" ), resolver().findFiles() );
    }

    @Test
    void findResource_returns_the_persisted_application_descriptor_without_icon()
    {
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( "kind: \"Application\"", null ) );

        final Resource resource = resolver().findResource( "/enonic.yaml" );

        assertEquals( "myapp:/enonic.yaml", resource.getKey().toString() );
        assertEquals( "kind: \"Application\"", resource.readString() );
        assertEquals( "node", resource.getResolverName() );

        assertNull( resolver().findResource( "/enonic.svg" ) );
        assertNull( resolver().findResource( "/enonic.png" ) );
    }

    @Test
    void findResource_returns_null_for_an_application_node_without_descriptor()
    {
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( applicationNode( null, null ) );

        assertNull( resolver().findResource( "/enonic.yaml" ) );
    }

    @Test
    void findResource_returns_the_application_icon_attached_to_the_application_node()
    {
        final Node appNode = applicationNode( "kind: \"Application\"", "image/svg+xml" );
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( appNode );
        // the binary is attached under the file name of the icon
        when( this.nodeService.getBinary( appNode.id(), BinaryReference.from( "enonic.svg" ) ) ).thenReturn(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        // the descriptor is always the text, even with the icon attached
        assertEquals( "kind: \"Application\"", resolver().findResource( "/enonic.yaml" ).readString() );

        final Resource icon = resolver().findResource( "/enonic.svg" );
        assertEquals( "myapp:/enonic.svg", icon.getKey().toString() );
        assertEquals( "<svg/>", icon.readString() );
        assertEquals( "node", icon.getResolverName() );
        assertEquals( appNode.getTimestamp().toEpochMilli(), icon.getTimestamp() );

        // served at the path of its binary name only
        assertNull( resolver().findResource( "/enonic.png" ) );
    }

    @Test
    void findResource_ignores_an_application_icon_binary_no_longer_referenced()
    {
        // the binary may remain attached after the icon is removed: the data decides
        final Node appNode = Node.create()
            .id( new NodeId() )
            .parentPath( NodePath.ROOT )
            .name( "myapp" )
            .data( applicationData( "kind: \"Application\"", null ) )
            .timestamp( Instant.now() )
            .attachedBinaries( AttachedBinaries.create().add( new AttachedBinary( BinaryReference.from( "enonic.svg" ), "blobkey" ) ).build() )
            .build();
        when( this.nodeService.getByPath( APP_PATH ) ).thenReturn( appNode );

        assertNull( resolver().findResource( "/enonic.svg" ) );
        assertEquals( "kind: \"Application\"", resolver().findResource( "/enonic.yaml" ).readString() );
    }

    @Test
    void findResource_returns_the_descriptor_of_a_schema_node()
    {
        final Node node = schemaNode( "contenttype:mytype", null );
        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) ).thenReturn( node );

        final Resource resource = resolver().findResource( "/cms/content-types/mytype.yaml" );

        assertEquals( "myapp:/cms/content-types/mytype.yaml", resource.getKey().toString() );
        assertEquals( "resource of contenttype:mytype", resource.readString() );
        assertEquals( "node", resource.getResolverName() );
        assertTrue( resource.exists() );

        // no icon attached: none is served
        assertNull( resolver().findResource( "/cms/content-types/mytype.svg" ) );
    }

    @Test
    void findResource_returns_the_icon_attached_to_a_schema_node_at_the_path_of_its_format()
    {
        final Node node = schemaNode( "contenttype:mytype", "icon.svg" );
        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/contenttype:mytype" ) ) ).thenReturn( node );
        when( this.nodeService.getBinary( node.id(), BinaryReference.from( "icon.svg" ) ) ).thenReturn(
            ByteSource.wrap( "<svg/>".getBytes( StandardCharsets.UTF_8 ) ) );

        final Resource resource = resolver().findResource( "/cms/content-types/mytype.svg" );

        assertEquals( "myapp:/cms/content-types/mytype.svg", resource.getKey().toString() );
        assertEquals( "<svg/>", resource.readString() );
        assertEquals( "node", resource.getResolverName() );
        assertEquals( node.getTimestamp().toEpochMilli(), resource.getTimestamp() );

        assertNull( resolver().findResource( "/cms/content-types/mytype.png" ) );
    }

    @Test
    void findResource_returns_cms_style_and_phrases()
    {
        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/cms" ) ) ).thenReturn( schemaNode( "cms", null ) );
        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/style" ) ) ).thenReturn( schemaNode( "style", null ) );
        when( this.nodeService.getByPath( new NodePath( "/myapp/cms/phrases:phrases_en" ) ) ).thenReturn(
            schemaNode( "phrases:phrases_en", null ) );

        assertEquals( "resource of cms", resolver().findResource( "/cms/cms.yaml" ).readString() );
        assertEquals( "resource of style", resolver().findResource( "/cms/style/style.yaml" ).readString() );
        assertEquals( "resource of phrases:phrases_en", resolver().findResource( "/cms/i18n/phrases/phrases_en.properties" ).readString() );
    }

    @Test
    void findResource_returns_null_for_a_missing_node()
    {
        assertNull( resolver().findResource( "/cms/content-types/missing.yaml" ) );
        assertNull( resolver().findResource( "/enonic.yaml" ) );
        assertNull( resolver().findResource( "/enonic.svg" ) );
    }

    // nothing of an application but its descriptor, icon and cms schema is persisted
    @Test
    void findResource_returns_null_for_paths_that_are_not_persisted()
    {
        assertNull( resolver().findResource( "/assets/app.js" ) );
        assertNull( resolver().findResource( "/enonic.yml" ) );
        assertNull( resolver().findResource( "/application.yaml" ) );
        assertNull( resolver().findResource( "/cms/layouts/mylayout.svg" ) );
        assertNull( resolver().findResource( "/cms/content-types/mytype/mytype.yaml" ) );
        verify( this.nodeService, never() ).getByPath( any() );
    }

    private NodeResourceApplicationUrlResolver resolver()
    {
        return new NodeResourceApplicationUrlResolver( APP_KEY, this.nodeService, APP_PATH, () -> ContextBuilder.create().build() );
    }

    private static Node applicationNode( final String descriptor, final String iconMimeType )
    {
        return Node.create()
            .id( new NodeId() )
            .parentPath( NodePath.ROOT )
            .name( "myapp" )
            .data( applicationData( descriptor, iconMimeType ) )
            .timestamp( Instant.now() )
            .build();
    }

    private static PropertyTree applicationData( final String descriptor, final String iconMimeType )
    {
        final PropertyTree data = new PropertyTree();
        if ( descriptor != null )
        {
            data.setString( SchemaNodePropertyNames.DESCRIPTOR, descriptor );
        }
        if ( iconMimeType != null )
        {
            data.setBinaryReference( SchemaNodePropertyNames.ICON,
                                     BinaryReference.from( "image/png".equals( iconMimeType ) ? "enonic.png" : "enonic.svg" ) );
        }
        return data;
    }

    private static Node schemaNode( final String name, final String iconReference )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, "resource of " + name );
        if ( iconReference != null )
        {
            data.setBinaryReference( SchemaNodePropertyNames.ICON, BinaryReference.from( iconReference ) );
        }
        return Node.create()
            .id( new NodeId() )
            .parentPath( new NodePath( "/myapp/cms" ) )
            .name( name )
            .data( data )
            .timestamp( Instant.now() )
            .build();
    }

    // the children of the cms node, as listed and then read by id
    private void stubSchemaNodes( final Node... nodes )
    {
        final Map<NodeId, Node> byId = new LinkedHashMap<>();
        Arrays.stream( nodes ).forEach( node -> byId.put( node.id(), node ) );
        when( this.nodeService.list( any() ) ).thenAnswer(
            invocation -> byId.values().stream().map( node -> new NodeListEntry( node.id(), node.path(), Instant.now() ) ) );
        if ( nodes.length > 0 )
        {
            when( this.nodeService.getByIds( any( NodeIds.class ) ) ).thenAnswer(
                invocation -> Nodes.from( invocation.getArgument( 0, NodeIds.class ).stream().map( byId::get ).toList() ) );
        }
    }
}
