package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.node.GetNodesByPathsCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetNodesByPathsCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void get_by_paths()
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
    }

    @Test
    void get_by_paths_empty()
    {
        final Nodes result = GetNodesByPathsCommand.create().
            paths( NodePaths.from( "/dummy1", "dummy2" ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 0, result.getSize() );
    }

}
