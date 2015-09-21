package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class FindNodesByIdsCommandTest
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

        final Nodes result = FindNodesByIdsCommand.create().
            ids( NodeIds.from( createdNode1.id(), createdNode2.id() ) ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
    }

}
