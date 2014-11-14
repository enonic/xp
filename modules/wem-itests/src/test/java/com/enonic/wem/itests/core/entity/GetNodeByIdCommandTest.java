package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.core.entity.GetNodeByIdCommand;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;

import static org.junit.Assert.*;


public class GetNodeByIdCommandTest
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
    public void get_by_id()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node fetchedNode = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( false ).
            build().
            execute();

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    public void get_by_id_resolve_hasChild()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "child-1" ).
            build() );

        final Node fetchedNode = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( true ).
            build().
            execute();

        assertTrue( fetchedNode.getHasChildren() );

        final Node fetchedNodeSkipResolve = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( false ).
            build().
            execute();

        assertFalse( fetchedNodeSkipResolve.getHasChildren() );
    }

}
