package com.enonic.xp.core.node;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionMetadatas;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repo.impl.node.FindNodeVersionsCommand;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FindNodeVersionsCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
        throws Exception
    {
        createDefaultRootNode();
    }

    @Test
    void get_single_version()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );
        nodeService.refresh( RefreshMode.ALL );

        final NodeVersionQuery query = NodeVersionQuery.create().
            size( 100 ).
            from( 0 ).
            nodeId( node.id() ).
            build();

        final NodeVersionQueryResult result = FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 1, result.getNodeVersionMetadatas().getSize() );
    }

    @Test
    void get_multiple_version()
    {
        PropertyTree data = new PropertyTree();

        final Node node = createNode( CreateNodeParams.create().
            data( data ).
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final int updateCount = 4;

        updateNode( node.id(), updateCount );

        refresh();

        final NodeVersionQuery query = NodeVersionQuery.create().
            size( 100 ).
            from( 0 ).
            nodeId( node.id() ).
            addOrderBy( FieldOrderExpr.create( VersionIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            build();

        final NodeVersionQueryResult result = FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( updateCount + 1, result.getNodeVersionMetadatas().getSize() );

        final NodeVersionMetadatas nodeVersionMetadatas = result.getNodeVersionMetadatas();
        Instant previousTimestamp = null;

        for ( final NodeVersionMetadata nodeVersionMetadata : nodeVersionMetadatas )
        {
            final Instant currentNodeTimestamp = nodeVersionMetadata.getTimestamp();
            if ( previousTimestamp != null )
            {
                if ( currentNodeTimestamp.isAfter( previousTimestamp ) )
                {
                    fail( String.format(
                        "expected timestamp of current item to be before or equal to previous. Previous: [%s], current: [%s]",
                        previousTimestamp, currentNodeTimestamp ) );
                }
            }

            previousTimestamp = currentNodeTimestamp;
        }
    }

    private void updateNode( final NodeId nodeId, final int updates )
    {
        for ( int i = 0; i < updates; i++ )
        {
            // Every update should introduce a change into the node
            final long value = i;
            updateNode( UpdateNodeParams.create().
                id( nodeId ).
                editor( node -> node.data.setLong( "someValue", value ) ).
                build() );
        }
    }
}
