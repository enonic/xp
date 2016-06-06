package com.enonic.xp.repo.impl.node;

import com.google.common.base.Stopwatch;

import com.enonic.xp.context.Context;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.security.acl.Permission;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    void deleteNodeWithChildren( final Node node, final Context context )
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodeIds.Builder builder = NodeIds.create();

        final Stopwatch timer2 = Stopwatch.createStarted();
        resolveNodesToDelete( node.id(), builder );
        System.out.println( "resolveNodesToDelete: " + timer2.stop() );

        final NodeIds nodesToBeDeleted = builder.build();

        final Stopwatch timer = Stopwatch.createStarted();
        final boolean allHasPermissions = NodesHasPermissionResolver.create( this ).
            nodeIds( nodesToBeDeleted ).
            permission( Permission.DELETE ).
            build().
            execute();
        System.out.println( "Resolve permissions: " + timer.stop() );

        if ( !allHasPermissions )
        {
            throw new NodeAccessException( context.getAuthInfo().getUser(), node.path(), Permission.DELETE );
        }

        this.storageService.delete( nodesToBeDeleted, InternalContext.from( context ) );
    }

    private void resolveNodesToDelete( final NodeId nodeId, final NodeIds.Builder builder )
    {
        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentId( nodeId ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();

        for ( final NodeId child : result.getNodeIds() )
        {
            resolveNodesToDelete( child, builder );
        }

        builder.add( nodeId );
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
