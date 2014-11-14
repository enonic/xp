package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.repo.FindNodesByParentParams;
import com.enonic.wem.repo.FindNodesByParentResult;
import com.enonic.wem.repo.Node;

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
