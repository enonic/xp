package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContentNodeTranslatorTest
{
    public static final NodeId ID_1 = NodeId.from( "id1" );

    private ContentNodeTranslator contentNodeTranslator;

    @BeforeEach
    public void setUp()
    {
        this.contentNodeTranslator = new ContentNodeTranslator();
    }

    @Test
    public void testNodeOutsideOfContentRoot()
    {
        assertThrows( ContentNotFoundException.class, () -> this.contentNodeTranslator.fromNode( createNode( NodePath.ROOT ) ) );
        assertThrows( ContentNotFoundException.class,
                      () -> this.contentNodeTranslator.fromNode( createNode( new NodePath( "/non-content" ) ) ) );
    }

    @Test
    public void testNodeOutsideOfContentRootAllowed()
    {
        final Content content = this.contentNodeTranslator.fromNodeWithAnyRootPath( createNode( NodePath.ROOT ) );
        assertEquals( ContentPath.ROOT, content.getPath() );
    }

    private Node createNode( final NodePath parentPath )
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( ContentPropertyNames.TYPE, ContentTypeName.unstructured().toString() );
        rootDataSet.setSet( ContentPropertyNames.DATA, rootDataSet.newSet() );
        rootDataSet.setString( ContentPropertyNames.CREATOR, "user:myidprovider:user1" );

        return Node.create().id( ID_1 ).name( "contentRoot" ).parentPath( parentPath ).data( rootDataSet ).build();
    }
}
