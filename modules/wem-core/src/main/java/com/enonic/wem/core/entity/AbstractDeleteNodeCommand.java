package com.enonic.wem.core.entity;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;

public abstract class AbstractDeleteNodeCommand
{
    private final static String ATTACHMENTS_NODE_NAME = "__att";

    protected final ElasticsearchIndexService indexService;

    protected final NodeDao nodeDao;

    protected final Workspace workspace;

    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.workspace = builder.workspace;
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

    protected static class Builder<B extends Builder>
    {
        private ElasticsearchIndexService indexService;

        private NodeDao nodeDao;

        protected Workspace workspace;

        @SuppressWarnings("unchecked")
        B indexService( final ElasticsearchIndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return (B) this;
        }
    }
}
