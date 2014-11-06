package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;

import static org.junit.Assert.*;

public class FindContentByParentCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        createContentRepository();
    }

    @Test
    public void getChildren()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child1" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child2" ).
            build() );

        refresh();

        final FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            build() );

        assertEquals( 2, children.getHits() );
        assertEquals( 2, children.getNodes().getSize() );

    }
}
