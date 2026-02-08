package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.SortNodeResult;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class SortNodeCommand
    extends AbstractNodeCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( SortNodeCommand.class );

    private static final int BATCH_SIZE = 10_000;

    private final SortNodeParams params;

    private SortNodeCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public SortNodeResult execute()
    {
        final Node node = doGetById( params.getNodeId() );

        checkContextUserPermissionOrAdmin( node );

        final SortNodeResult.Builder result = SortNodeResult.create();

        if ( params.getChildOrder().isManualOrder() )
        {
            if ( !node.getChildOrder().isManualOrder() )
            {
                orderChildNodes( node.path(), Objects.requireNonNullElse( params.getManualOrderSeed(), node.getChildOrder() ),
                                 params.getReorderChildNodes(), result );
            }
            else
            {
                reorderChildNodes( node.path(), params.getReorderChildNodes(), result );
            }
        }

        final PropertyTree processedData = params.getProcessor().process( node.data(), node.path() );

        if ( !processedData.equals( node.data() ) || !Objects.equals( params.getChildOrder(), node.getChildOrder() ) )
        {
            final Node editedNode =
                Node.create( node ).childOrder( params.getChildOrder() ).data( processedData ).timestamp( Instant.now( CLOCK ) ).build();
            Node updatedNode =
                this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode, params.getVersionAttributes() ), InternalContext.from( ContextAccessor.current() ) )
                    .node();
            result.node( updatedNode );
        }
        else
        {
            result.node( node );
        }
        refresh( params.getRefresh() );
        return result.build();
    }

    private void checkContextUserPermissionOrAdmin( final Node parentNode )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, parentNode );
    }

    private void orderChildNodes( final NodePath parentNodePath, ChildOrder baseOrder, final List<ReorderChildNodeParams> reorderChildNodes,
                                  final SortNodeResult.Builder result )
    {
        refresh( RefreshMode.SEARCH );
        final NodeQuery query = NodeQuery.create()
            .parent( parentNodePath )
            .query( new QueryExpr( baseOrder.getOrderExpressions() ) )
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .batchSize( BATCH_SIZE )
            .build();

        final List<NodeId> childNodeIds = nodeSearchService.query( query, SingleRepoSearchSource.from( ContextAccessor.current() ) )
            .getIds()
            .stream()
            .distinct()
            .map( NodeId::from )
            .collect( Collectors.toCollection( ArrayList::new ) );

        for ( ReorderChildNodeParams param : reorderChildNodes )
        {
            final NodeId moveBefore = param.getMoveBefore();
            final NodeId nodeId = param.getNodeId();
            if ( moveBefore != null )
            {
                final int moveBeforeIndex = childNodeIds.indexOf( moveBefore );
                if ( moveBeforeIndex >= 0 )
                {
                    final int indexOf = childNodeIds.indexOf( nodeId );
                    if ( indexOf >= 0 )
                    {
                        childNodeIds.remove( indexOf );
                        childNodeIds.add( moveBeforeIndex, nodeId );
                    }
                }
            }
            else
            {
                final int indexOf = childNodeIds.indexOf( nodeId );
                if ( indexOf >= 0 )
                {
                    childNodeIds.remove( indexOf );
                    childNodeIds.add( nodeId );
                }
            }
        }

        final NodeManualOrderValueResolver resolver = new NodeManualOrderValueResolver();
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );
        for ( final NodeId nodeId : childNodeIds )
        {
            final Node node = doGetById( nodeId );
            final Node updatedNode = Node.create( node ).manualOrderValue( resolver.getAsLong() ).timestamp( Instant.now( CLOCK ) ).build();
            final Node storedNode =
                this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode, params.getChildVersionAttributes() ),
                                               internalContext ).node();
            result.addReorderedNode( storedNode );
        }
    }

    private void reorderChildNodes( final NodePath parentPath, final List<ReorderChildNodeParams> reorderChildNodes,
                                    final SortNodeResult.Builder result )
    {
        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final LinkedHashMap<NodeId, Node> visitedNodes = new LinkedHashMap<>();

        for ( final ReorderChildNodeParams reorderChildNodeParams : reorderChildNodes )
        {
            final Long toMoveBeforeValue;
            if ( reorderChildNodeParams.getMoveBefore() == null )
            {
                toMoveBeforeValue = null;
            }
            else
            {
                final Node toMoveBefore = memoNode( reorderChildNodeParams.getMoveBefore(), parentPath, visitedNodes );
                if ( toMoveBefore == null )
                {
                    continue;
                }
                toMoveBeforeValue = toMoveBefore.getManualOrderValue();
                if ( toMoveBeforeValue == null )
                {
                    LOG.debug( "Node [{}] to move before does not have manualOrderValue", reorderChildNodeParams.getMoveBefore() );
                    continue;
                }
            }

            final Node node = memoNode( reorderChildNodeParams.getNodeId(), parentPath, visitedNodes );
            if ( node == null )
            {
                continue;
            }
            final Long currentValue = node.getManualOrderValue();

            final long newOrderValue =
                ResolveInsertOrderValueCommand.create( this ).parentPath( parentPath ).build().reorder( toMoveBeforeValue, currentValue );

            if ( currentValue != null && newOrderValue == currentValue )
            {
                LOG.debug( "manualOrderValue not changed {}", node.id() );
                continue;
            }
            final Node updatedNode = Node.create( node ).timestamp( Instant.now( CLOCK ) ).manualOrderValue( newOrderValue ).build();
            final Node storedNode =
                this.nodeStorageService.store( StoreNodeParams.newVersion( updatedNode, params.getChildVersionAttributes() ),
                                               internalContext ).node();
            visitedNodes.put( storedNode.id(), storedNode );

            result.addReorderedNode( storedNode );
        }
    }

    private Node memoNode( NodeId nodeId, NodePath parentPath, LinkedHashMap<NodeId, Node> storage )
    {
        final Node inMemory = storage.get( nodeId );
        if ( inMemory != null )
        {
            return inMemory;
        }

        final Node node = doGetById( nodeId );
        if ( node == null )
        {
            LOG.debug( "Reorder node [{}] not found", nodeId );
            storage.put( nodeId, null );
            return null;
        }
        if ( !parentPath.equals( node.parentPath() ) )
        {
            LOG.debug( "Reordered nodes must have parent path [{}]. Node [{}] has parent path [{}]", parentPath, node.id(),
                       node.parentPath() );
            storage.put( node.id(), null );
            return null;
        }
        storage.put( node.id(), node );
        return node;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private SortNodeParams params;

        private Builder()
        {
        }

        public Builder params( SortNodeParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public SortNodeCommand build()
        {
            validate();
            return new SortNodeCommand( this );
        }
    }
}
