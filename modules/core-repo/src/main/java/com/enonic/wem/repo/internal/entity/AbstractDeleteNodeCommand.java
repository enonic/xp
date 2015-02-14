package com.enonic.wem.repo.internal.entity;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.node.FindNodesByParentParams;
import com.enonic.xp.core.node.FindNodesByParentResult;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.security.acl.Permission;
import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.QueryService;

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
        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node child : result.getNodes() )
        {
            resolveNodesToDelete( child, nodes );
        }

        nodes.add( node );
    }

    private void doDelete( final Context context, final Node node )
    {
        branchService.delete( node.id(), BranchContext.from( context ) );

        indexServiceInternal.delete( node.id(), IndexContext.from( context ) );
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
