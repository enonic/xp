package com.enonic.xp.repo.impl.entity;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.context.Context;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
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

        final List<Node> nodesToDelete = new ArrayList<>();
        resolveNodesToDelete( node, nodesToDelete );

        for ( final Node child : nodesToDelete )
        {
            NodePermissionsResolver.requireContextUserPermission( context.getAuthInfo(), Permission.DELETE, child );
        }

        for ( final Node child : nodesToDelete )
        {
            doDelete( context, child );
        }
    }

    private void resolveNodesToDelete( final Node node, final List<Node> nodes )
    {
        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentId( node.id() ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();

        for ( final Node child : result.getNodes() )
        {
            resolveNodesToDelete( child, nodes );
        }

        nodes.add( node );
    }

    private void doDelete( final Context context, final Node node )
    {
        this.storageService.delete( node.id(), InternalContext.from( context ) );
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
