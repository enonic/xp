package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.Context;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.security.acl.Permission;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    NodeIds deleteNodeWithChildren( final Node node, final Context context )
    {
        doRefresh();

        final NodeIds nodesToBeDeleted = newResolveNodesToDelete( node );

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).
            nodeIds( nodesToBeDeleted ).
            permission( Permission.DELETE ).
            build().
            execute();

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        this.storageService.delete( nodesToBeDeleted, InternalContext.from( context ) );

        doRefresh();

        return nodesToBeDeleted;
    }

    private void doRefresh()
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }


    private NodeIds newResolveNodesToDelete( final Node node )
    {
        final FindNodesByParentResult result = FindNodeIdsByParentCommand.create( this ).
            parentPath( node.path() ).
            recursive( true ).
            childOrder( ChildOrder.path() ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            build().
            execute();

        return NodeIds.create().
            add( node.id() ).
            addAll( result.getNodeIds() ).
            build();
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }
    }
}
