package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;
import com.enonic.xp.repo.impl.node.GetNodeIdByPathCommand;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GetNodeIdByPathCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void exists()
        throws Exception
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

        assertNotNull( GetNodeIdByPathCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodePath( node.path() ).
            build().
            execute() );

    }

    @Test
    public void not_exists()
        throws Exception
    {
        assertNull( GetNodeIdByPathCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodePath( new NodePath( "/notExists" ) ).
            build().
            execute() );

    }
}
