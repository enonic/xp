package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentNodeTranslatorTest
{
    public static final NodeId ID_1 = NodeId.from( "id1" );

    @Test
    void testNodeOutsideOfContentRoot()
    {
        assertThrows( ContentNotFoundException.class,
                      () -> ContentNodeTranslator.fromNode( createNode( NodePath.ROOT, ContentConstants.CONTENT_NODE_COLLECTION ) ) );
        assertThrows( ContentNotFoundException.class, () -> ContentNodeTranslator.fromNode(
            createNode( new NodePath( "/non-content" ), ContentConstants.CONTENT_NODE_COLLECTION ) ) );
    }

    @Test
    void testNodeOutsideOfContentRootAllowed()
    {
        final Content content =
            ContentNodeTranslator.fromNodeWithAnyRootPath( createNode( NodePath.ROOT, ContentConstants.CONTENT_NODE_COLLECTION ) );
        assertEquals( ContentPath.ROOT, content.getPath() );
    }

    private Node createNode( final NodePath parentPath, final NodeType nodeType )
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        rootDataSet.setSet( ContentPropertyNames.DATA, rootDataSet.newSet() );
        rootDataSet.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );

        return Node.create().id( ID_1 ).name( "contentRoot" ).parentPath( parentPath ).data( rootDataSet ).nodeType( nodeType ).build();
    }
}
