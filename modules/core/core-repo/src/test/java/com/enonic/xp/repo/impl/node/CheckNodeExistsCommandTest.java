package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckNodeExistsCommandTest
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

        assertTrue( CheckNodeExistsCommand.create().
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
        assertFalse( CheckNodeExistsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodePath( NodePath.create( NodePath.ROOT, "notExists" ).build() ).
            build().
            execute() );

    }
}
