package com.enonic.xp.core.node;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CheckNodeExistsCommand;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;

import static org.junit.jupiter.api.Assertions.assertFalse;

class CheckNodeExistsCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void exists()
    {
        final Node node = CreateNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            params( CreateNodeParams.create().
                name( "myNode" ).
                setNodeId( NodeId.from( "myNode" ) ).
                parent( NodePath.ROOT ).
                build() ).
            build().
            execute();

        Assertions.assertTrue( CheckNodeExistsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodePath( node.path() ).
            build().
            execute() );

    }

    @Test
    void not_exists()
    {
        assertFalse( CheckNodeExistsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodePath( new NodePath( "/notExists" ) ).
            build().
            execute() );

    }
}
