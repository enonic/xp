package com.enonic.wem.core.entity;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;

public abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    private final static String ATTACHMENTS_NODE_NAME = "__att";

    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    protected void doDeleteChildIndexDocuments( final Node node, final Workspace workspace )
    {
        final Nodes childrenNodes = nodeDao.getByParent( node.path(), workspace );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = Strings.startsWithIgnoreCase( nodeName, ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                indexService.delete( child.id() );
                doDeleteChildIndexDocuments( child, workspace );
            }
        }
    }
}
