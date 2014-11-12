package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.core.entity.GetNodesByIdsCommand;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeIds;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.repo.Nodes;

import static org.junit.Assert.*;

public class GetNodesByIdsCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
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

        final Nodes result = GetNodesByIdsCommand.create().
            ids( NodeIds.from( createdNode1.id(), createdNode2.id() ) ).
            resolveHasChild( true ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
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
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
        assertTrue( result.getNodeById( createdNode1.id() ).getHasChildren() );
        assertFalse( result.getNodeById( createdNode2.id() ).getHasChildren() );
    }
}
