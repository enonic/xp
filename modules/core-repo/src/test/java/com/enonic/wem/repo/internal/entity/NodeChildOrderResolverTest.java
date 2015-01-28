package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class NodeChildOrderResolverTest
    extends AbstractNodeTest
{

    @Test
    public void given_child_order_as_param()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            workspaceService( this.queryService ).
            nodePath( NodePath.newPath( "myPath" ).build() ).
            childOrder( childOrder ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

    @Test
    public void user_parent_child_order_when_root()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        CreateRootNodeCommand.create().
            params( CreateRootNodeParams.create().
                childOrder( childOrder ).
                build() ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            execute();

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodePath( NodePath.ROOT ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

    @Test
    public void user_parent_child_order()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        final Node parent = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "parent" ).
            childOrder( childOrder ).
            build() );

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodePath( parent.path() ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

}