package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.Contents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentNodeTranslatorTest
{
    public static final NodeId ID_1 = NodeId.from( "id1" );

    public static final NodeId ID_2 = NodeId.from( "id2" );

    public static final NodeId ID_3 = NodeId.from( "id3" );

    private ContentNodeTranslator contentNodeTranslator;

    private NodeService nodeService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.contentNodeTranslator = new ContentNodeTranslator( nodeService );
    }

    @Test
    public void testFromNodesResolvingChildren()
        throws Exception
    {
        final Nodes nodes = createNodes();
        Mockito.when( this.nodeService.hasChildren( Mockito.any( Node.class ) ) ).thenReturn( true ).thenReturn( true ).thenReturn( false );

        final Contents contents = this.contentNodeTranslator.fromNodes( nodes, true );

        assertThat(contents).map( Content::hasChildren ).containsExactly( true, true, false );
    }

    @Test
    public void testFromNodesNotResolvingChildren()
        throws Exception
    {
        final Nodes nodes = createNodes();

        final Contents contents = this.contentNodeTranslator.fromNodes( nodes, false );

        assertThat(contents).map( Content::hasChildren ).containsExactly( false, false, false );
    }

    @Test
    public void testFromNodeResolvingChildren()
        throws Exception
    {
        final Node node = createNode();
        Mockito.when( this.nodeService.hasChildren( Mockito.any( Node.class ) ) ).thenReturn( true );

        final Content content = this.contentNodeTranslator.fromNode( node, true );

        assertNotNull( content );
        assertTrue( content.hasChildren() );
    }

    @Test
    public void testFromNodeNotResolvingChildren()
        throws Exception
    {
        final Node node = createNode();

        final Content content = this.contentNodeTranslator.fromNode( node, false );

        assertNotNull( content );
        assertFalse( content.hasChildren() );
    }


    @Test
    public void testNodeOutsideOfContentRoot()
        throws Exception
    {
        assertThrows( ContentNotFoundException.class, () -> this.contentNodeTranslator.fromNode( createNode( NodePath.ROOT ), false ) );
        assertThrows( ContentNotFoundException.class,
                      () -> this.contentNodeTranslator.fromNode( createNode( new NodePath( "/non-content" ) ), false ) );
    }

    @Test
    public void testNodeOutsideOfContentRootAllowed()
        throws Exception
    {
        final Content content = this.contentNodeTranslator.fromNode( createNode( NodePath.ROOT ), false, true );
        assertEquals( ContentPath.ROOT, content.getPath() );
    }

    private Node createNode()
    {
        return createNode( ContentConstants.CONTENT_ROOT_PATH );
    }

    private Node createNode( final NodePath parentPath )
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        rootDataSet.setSet( ContentPropertyNames.DATA, rootDataSet.newSet() );
        rootDataSet.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );

        return Node.create().id( ID_1 ).name( "contentRoot" ).parentPath( parentPath ).data( rootDataSet ).build();
    }

    private Nodes createNodes()
    {

        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        rootDataSet.setSet( ContentPropertyNames.DATA, rootDataSet.newSet() );
        rootDataSet.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );

        final Node node1 =
            Node.create().id( ID_1 ).name( "contentRoot" ).parentPath( ContentConstants.CONTENT_ROOT_PATH ).data( rootDataSet ).build();
        final Node node2 = Node.create().id( ID_2 ).name( "contentParent" ).parentPath( node1.path() ).data( rootDataSet ).build();
        final Node node3 = Node.create().id( ID_3 ).name( "contentChild" ).parentPath( node2.path() ).data( rootDataSet ).build();

        return Nodes.from( node1, node2, node3 );
    }

}
