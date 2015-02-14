package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
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
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void given_child_order_as_param()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
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
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodePath( NodePath.ROOT ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
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
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

}