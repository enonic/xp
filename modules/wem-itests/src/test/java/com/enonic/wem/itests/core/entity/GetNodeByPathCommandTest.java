package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.core.entity.GetNodeByPathCommand;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;

import static org.junit.Assert.*;

public class GetNodeByPathCommandTest
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
    public void get_by_path()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node fetchedNode = doGetNodeByPath( createdNode.path() );

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    public void get_by_path_fetch_correct()
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

        final Node fetchedNode1 = doGetNodeByPath( createdNode1.path() );
        final Node fetchedNode2 = doGetNodeByPath( createdNode2.path() );

        assertEquals( createdNode1, fetchedNode1 );
        assertEquals( createdNode2, fetchedNode2 );
    }

    @Test
    public void get_by_path_not_found()
        throws Exception
    {
        final Node fetchedNode = doGetNodeByPath( NodePath.newPath( "/dummy" ).build() );
        assertNull( fetchedNode );
    }

    private Node doGetNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            nodePath( nodePath ).
            resolveHasChild( true ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }


}
