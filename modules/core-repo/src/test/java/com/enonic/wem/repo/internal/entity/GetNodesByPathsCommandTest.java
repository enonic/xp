package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.Nodes;

import static org.junit.Assert.*;

public class GetNodesByPathsCommandTest
    extends AbstractNodeTest
{

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
    public void get_by_paths_empty()
        throws Exception
    {
        final Nodes result = GetNodesByPathsCommand.create().
            paths( NodePaths.from( "/dummy1", "dummy2" ) ).
            resolveHasChild( true ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            build().
            execute();

        assertEquals( 0, result.getSize() );
    }

}
