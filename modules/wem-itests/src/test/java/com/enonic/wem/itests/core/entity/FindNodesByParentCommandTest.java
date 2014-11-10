package com.enonic.wem.itests.core.entity;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodesByParentCommand;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.Nodes;
import com.enonic.wem.core.repository.RepositoryInitializer;

import static org.junit.Assert.*;

public class FindNodesByParentCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer();
        repositoryInitializer.setIndexService( this.indexService );

        repositoryInitializer.init( ContentConstants.CONTENT_REPO );
    }

    @Test
    public void get_by_parent_one_child()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child" ).
            build() );

        final FindNodesByParentResult result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                build() ).queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        assertEquals( 1, result.getNodes().getSize() );
        assertEquals( childNode, result.getNodes().first() );
    }

    @Test
    public void get_children_order()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "parent" ) ).
            name( "my-node" ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.from( "name ASC" ) ).
            build() );

        final Node childNode_b_3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childNode_b_3" ) ).
            parent( createdNode.path() ).
            name( "b" ).
            data( createOrderProperty( 3.0 ) ).
            build() );

        final Node childNode_a_2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childNode_a_2" ) ).
            parent( createdNode.path() ).
            name( "a" ).
            data( createOrderProperty( 2.0 ) ).
            build() );

        final Node childNode_c_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childNode_c_1" ) ).
            parent( createdNode.path() ).
            name( "c" ).
            data( createOrderProperty( 1.0 ) ).
            build() );

        // Use default parent ordering; name
        FindNodesByParentResult result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                build() ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        Nodes childNodes = result.getNodes();

        assertEquals( 3, childNodes.getSize() );
        Iterator<Node> iterator = childNodes.iterator();
        assertEquals( childNode_a_2, iterator.next() );
        assertEquals( childNode_b_3, iterator.next() );
        assertEquals( childNode_c_1, iterator.next() );

        // Override by specify childOrder-parameter by order-field
        result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                childOrder( ChildOrder.from( "order ASC" ) ).
                build() ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        childNodes = result.getNodes();

        assertEquals( 3, childNodes.getSize() );
        iterator = childNodes.iterator();
        assertEquals( childNode_c_1, iterator.next() );
        assertEquals( childNode_a_2, iterator.next() );
        assertEquals( childNode_b_3, iterator.next() );
    }

    private RootDataSet createOrderProperty( final Double value )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "order", Value.newDouble( value ) );
        return rootDataSet;
    }

}
