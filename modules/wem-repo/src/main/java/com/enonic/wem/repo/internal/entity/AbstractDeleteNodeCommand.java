package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    private final static String ATTACHMENTS_NODE_NAME = "__att";

    AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    void doDeleteChildren( final Node parent )
    {
        final Context context = ContextAccessor.current();

        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( parent.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node child : result.getNodes() )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = nodeName.startsWith( ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                workspaceService.delete( child.id(), WorkspaceContext.from( context ) );

                indexService.delete( child.id(), IndexContext.from( context ) );
                doDeleteChildren( child );
            }
            else
            {
                // TODO; What to do with attachment nodes?
                workspaceService.delete( child.id(), WorkspaceContext.from( context ) );
            }
        }
    }
}
