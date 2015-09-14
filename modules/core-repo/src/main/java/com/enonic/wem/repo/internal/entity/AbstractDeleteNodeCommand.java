package com.enonic.wem.repo.internal.entity;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.xp.context.Context;
import com.enonic.xp.node.GetNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.wem.repo.internal.entity.NodePermissionsResolver.requireContextUserPermission;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    void deleteNodeWithChildren( final Node node, final Context context )
    {

        final List<Node> nodesToDelete = new ArrayList<>();
        resolveNodesToDelete( node, nodesToDelete );

        for ( final Node child : nodesToDelete )
        {
            requireContextUserPermission( context.getAuthInfo(), Permission.DELETE, child );
        }

        for ( final Node child : nodesToDelete )
        {
            doDelete( context, child );
        }
    }

    void resolveNodesToDelete( final Node node, final List<Node> nodes )
    {
        final Nodes children = GetNodesByParentCommand.create( this ).
            params( GetNodesByParentParams.create().
                parentId( node.id() ).
                build() ).
            build().
            execute();

        for ( final Node child : children )
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
