package com.enonic.wem.core.entity;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;

public abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    private final static String ATTACHMENTS_NODE_NAME = "__att";

    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    protected void doDeleteChildren( final Node parent, final Workspace workspace )
    {
        final NodeVersionIds childrenVersions = workspaceService.findByParent( new WorkspaceParentQuery( workspace, parent.path() ) );

        if ( childrenVersions.isEmpty() )
        {
            return;
        }

        final Nodes childrenNodes = nodeDao.getByVersionIds( childrenVersions );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = Strings.startsWithIgnoreCase( nodeName, ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                workspaceService.delete( new WorkspaceDeleteQuery( workspace, child.id() ) );
                indexService.delete( child.id(), workspace );
                doDeleteChildren( child, workspace );
            }
            else
            {
                // TODO; What to do with attachment nodes?
                workspaceService.delete( new WorkspaceDeleteQuery( workspace, child.id() ) );
            }
        }
    }
}
