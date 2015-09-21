package com.enonic.xp.repo.impl.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class GetNodesByIdsCommandTest
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
    public void get_by_ids()
        throws Exception
    {
        final Node createdNode1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            build() );

        final Node createdNode2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            build() );

        refresh();

        printContentRepoIndex();

        final Nodes result = GetNodesByIdsCommand.create().
            ids( NodeIds.from( createdNode1.id(), createdNode2.id() ) ).
            resolveHasChild( true ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
    }

    @Test
    public void get_by_ids_resolve_hasChild()
        throws Exception
    {
        final Node createdNode1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( createdNode1.path() ).
            name( "child-1" ).
            build() );

        final Node createdNode2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            build() );

        refresh();

        final Nodes result = GetNodesByIdsCommand.create().
            ids( NodeIds.from( createdNode1.id(), createdNode2.id() ) ).
            resolveHasChild( true ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
        assertTrue( result.getNodeById( createdNode1.id() ).getHasChildren() );
        assertFalse( result.getNodeById( createdNode2.id() ).getHasChildren() );
    }
}
