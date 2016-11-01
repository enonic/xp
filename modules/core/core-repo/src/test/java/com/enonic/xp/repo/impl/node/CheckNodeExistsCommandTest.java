package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class CheckNodeExistsCommandTest
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
    public void exists_path()
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
    public void exists_id()
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
            nodeId( NodeId.from( "myNode" ) ).
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            build().
            execute() );

    }

    @Test
    public void not_exists_path()
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

    @Test
    public void not_exists_id()
        throws Exception
    {
        assertFalse( CheckNodeExistsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            nodeId( NodeId.from( "fiskekake" ) ).
            build().
            execute() );
    }

}