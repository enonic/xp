package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.Context;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.acl.Permission;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    NodeBranchEntries deleteNodeWithChildren( final Node node, final Context context )
    {
        if ( node.isRoot() )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        doRefresh();

        final NodeBranchEntries nodesToBeDeleted = newResolveNodesToDelete( node );

        final NodeIds nodeIds = NodeIds.from( nodesToBeDeleted.getKeys() );

        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).
            nodeIds( nodeIds ).
            permission( Permission.DELETE ).
            build().
            execute();

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        this.nodeStorageService.delete( nodeIds, InternalContext.from( context ) );

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


    private NodeBranchEntries newResolveNodesToDelete( final Node node )
    {
        final FindNodeIdsByParentCommand command = FindNodeIdsByParentCommand.create( this ).
            parentPath( node.path() ).
            recursive( true ).
            childOrder( ChildOrder.path() ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build();

        final FindNodesByParentResult result = command.execute();

        final NodeIds nodeIds = NodeIds.create().
            add( node.id() ).
            addAll( result.getNodeIds() ).
            build();

        return FindNodeBranchEntriesByIdCommand.
            create( command ).
            ids( nodeIds ).
            build().
            execute();
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
