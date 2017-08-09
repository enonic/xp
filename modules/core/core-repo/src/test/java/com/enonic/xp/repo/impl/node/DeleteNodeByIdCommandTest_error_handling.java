package com.enonic.xp.repo.impl.node;

import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeStorageException;
import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;
import com.enonic.xp.repo.impl.elasticsearch.ClientProxy;

import static org.junit.Assert.*;

public class DeleteNodeByIdCommandTest_error_handling
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test(expected = NodeStorageException.class)
    public void delete_fails()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        this.storageDao.setClient( new FailDeleteOnIdsProxy( this.client, NodeIds.from( createdNode.id() ) ) );

        doDeleteNode( createdNode.id() );
    }

    @Test
    public void delete_children_first()
        throws Exception
    {
        final Node n1 = createNode( NodePath.ROOT, "n1" );
        final Node n2 = createNode( NodePath.ROOT, "n2" );
        final Node n1_1 = createNode( n1.path(), "n1_1" );
        final Node n1_1_1 = createNode( n1_1.path(), "n1_1_1" );
        final Node n1_1_1_1 = createNode( n1_1_1.path(), "n1_1_1_1" );

        refresh();

        this.storageDao.setClient( new FailDeleteOnIdsProxy( this.client, NodeIds.from( n1_1.id() ) ) );

        try
        {
            doDeleteNode( n1.id() );
        }
        catch ( NodeStorageException e )
        {
            //expected
        }

        assertNull( getNode( n1_1_1_1.id() ) );
        assertNull( getNode( n1_1_1.id() ) );
        assertNotNull( getNode( n1_1.id() ) );
        assertNotNull( getNode( n1.id() ) );
        assertNotNull( getNode( n2.id() ) );
    }

    private class FailDeleteOnIdsProxy
        extends ClientProxy
    {
        private final List<String> failOn = Lists.newArrayList();

        private FailDeleteOnIdsProxy( final Client client, final NodeIds failOnIds )
        {
            super( client );
            failOnIds.forEach( id -> {
                failOn.add( new BranchDocumentId( id, ContextAccessor.current().getBranch() ).toString() );
                failOn.add( id.toString() );
            } );
        }

        @Override
        public ActionFuture<DeleteResponse> delete( final DeleteRequest request )
        {
            if ( failOn.contains( request.id() ) )
            {
                throw new ElasticsearchException( "This was supposed to fail" );
            }

            return super.delete( request );
        }
    }
}
