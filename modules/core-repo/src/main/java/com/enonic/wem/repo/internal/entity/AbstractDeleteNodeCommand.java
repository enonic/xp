package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.workspace.NodeWorkspaceState;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    void deleteNodeWithChildren( final Node node, final Context context )
    {
        final NodeVersionId nodeVersionId = this.queryService.get( node.id(), IndexContext.from( context ) );

        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node child : result.getNodes() )
        {
            deleteNodeWithChildren( child, context );
        }

        markAsDeleted( context, node, nodeVersionId );
        indexService.delete( node.id(), IndexContext.from( context ) );
    }


    void markAsDeleted( final Context context, final Node child, final NodeVersionId version )
    {
        workspaceService.store( StoreWorkspaceDocument.create().
            nodeVersionId( version ).
            node( child ).
            workspaceNodeStatus( NodeWorkspaceState.DELETED ).
            build(), WorkspaceContext.from( context ) );
    }
}
