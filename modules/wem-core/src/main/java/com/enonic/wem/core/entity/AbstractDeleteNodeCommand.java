package com.enonic.wem.core.entity;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.workspace.WorkspaceContext;

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
        final NodeVersionIds childrenVersions = workspaceService.findByParent( parent.path(), WorkspaceContext.from( this.context ) );

        if ( childrenVersions.isEmpty() )
        {
            return;
        }

        final Nodes childrenNodes = nodeDao.getByVersionIds( childrenVersions );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = nodeName.startsWith( ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                workspaceService.delete( child.id(), WorkspaceContext.from( this.context ) );

                indexService.delete( child.id(), IndexContext.from( this.context ) );
                doDeleteChildren( child );
            }
            else
            {
                // TODO; What to do with attachment nodes?
                workspaceService.delete( child.id(), WorkspaceContext.from( this.context ) );
            }
        }
    }
}
