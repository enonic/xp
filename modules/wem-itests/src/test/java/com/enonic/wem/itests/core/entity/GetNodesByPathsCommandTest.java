package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.core.entity.GetNodesByPathsCommand;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.Nodes;

import static org.junit.Assert.*;

public class GetNodesByPathsCommandTest
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
    public void get_by_paths()
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

        final Nodes result = GetNodesByPathsCommand.create().
            paths( NodePaths.create().
                addNodePath( createdNode1.path() ).
                addNodePath( createdNode2.path() ).
                build() ).
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
    public void get_by_paths_empty()
        throws Exception
    {
        final Nodes result = GetNodesByPathsCommand.create().
            paths( NodePaths.from( "/dummy1", "dummy2" ) ).
            resolveHasChild( true ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            build().
            execute();

        assertEquals( 0, result.getSize() );
    }

}
