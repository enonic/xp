package com.enonic.xp.core.node;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeStorageException;
import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteNodeByIdCommandTest_error_handling
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void delete_fails()
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        this.storageDao.setClient( new FailDeleteOnIdsProxy( client, NodeIds.from( createdNode.id() ) ) );

        assertThrows(NodeStorageException.class, () -> doDeleteNode( createdNode.id() ));
    }

    @Test
    void delete_children_first()
    {
        final Node n1 = createNode( NodePath.ROOT, "n1" );
        final Node n2 = createNode( NodePath.ROOT, "n2" );
        final Node n1_1 = createNode( n1.path(), "n1_1" );
        final Node n1_1_1 = createNode( n1_1.path(), "n1_1_1" );
        final Node n1_1_1_1 = createNode( n1_1_1.path(), "n1_1_1_1" );

        this.storageDao.setClient( new FailDeleteOnIdsProxy( client, NodeIds.from( n1_1.id() ) ) );

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

    private static class FailDeleteOnIdsProxy
        extends ClientProxy
    {
        private final List<String> failOn = new ArrayList<>();

        private FailDeleteOnIdsProxy( final Client client, final NodeIds failOnIds )
        {
            super( client );
            failOnIds.forEach( id -> {
                failOn.add( BranchDocumentId.asString( id, ContextAccessor.current().getBranch() ) );
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
