package com.enonic.wem.itests.core.entity;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodesByParentCommand;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.Node;
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

    @Ignore
    @Test
    public void get_children_order()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.from( "name DESC" ) ).
            build() );

        final Node childNode1 = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "child-1" ).
            build() );

        final Node childNode2 = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "child-2" ).
            build() );

        final Node childNode3 = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "child-3" ).
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

        final Nodes childNodes = result.getNodes();

        assertEquals( 3, childNodes.getSize() );
        final Iterator<Node> iterator = childNodes.iterator();
        assertEquals( "child-3", iterator.next().name().toString() );
        assertEquals( "child-2", iterator.next().name().toString() );
        assertEquals( "child-2", iterator.next().name().toString() );

    }


}
