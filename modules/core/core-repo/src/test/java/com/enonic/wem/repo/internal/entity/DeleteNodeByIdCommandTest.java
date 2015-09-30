package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class DeleteNodeByIdCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void delete()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        doDeleteNode( createdNode.id() );

        assertNull( getNodeById( createdNode.id() ) );
    }

    @Test
    public void delete_with_children()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        final Node childNode = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "my-node" ).
            build() );
        refresh();

        final Node childChildNode = createNode( CreateNodeParams.create().
            parent( childNode.path() ).
            name( "my-node" ).
            build() );
        refresh();

        doDeleteNode( parentNode.id() );

        assertNull( getNodeById( parentNode.id() ) );
        assertNull( getNodeById( childNode.id() ) );
        assertNull( getNodeById( childChildNode.id() ) );
    }

    @Test
    public void delete_with_children_other_on_level()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        final Node childNode = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child1" ).
            build() );
        refresh();

        final Node childNode2 = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child2" ).
            build() );
        refresh();


        final Node childChildNode = createNode( CreateNodeParams.create().
            parent( childNode.path() ).
            name( "child1-1" ).
            build() );
        refresh();

        final Node childChildNode2 = createNode( CreateNodeParams.create().
            parent( childNode2.path() ).
            name( "child2-1" ).
            build() );
        refresh();


        assertNotNull( getNodeById( parentNode.id() ) );
        assertNotNull( getNodeById( childNode.id() ) );
        assertNotNull( getNodeById( childNode2.id() ) );
        assertNotNull( getNodeById( childChildNode.id() ) );
        assertNotNull( getNodeById( childChildNode2.id() ) );

        doDeleteNode( parentNode.id() );

        assertNull( getNodeById( parentNode.id() ) );
        assertNull( getNodeById( childNode.id() ) );
        assertNull( getNodeById( childNode2.id() ) );
        assertNull( getNodeById( childChildNode.id() ) );
        assertNull( getNodeById( childChildNode2.id() ) );
    }

}
