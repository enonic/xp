package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

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
            nodePath( NodePath.create( "myPath" ).build() ).
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodePath( NodePath.ROOT ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

}