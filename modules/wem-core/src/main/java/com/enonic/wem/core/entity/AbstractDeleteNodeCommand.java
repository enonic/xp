package com.enonic.wem.core.entity;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;

public abstract class AbstractDeleteNodeCommand
{
    private final static String ATTACHMENTS_NODE_NAME = "__att";

    protected ElasticsearchIndexService indexService;

    protected NodeDao nodeDao;

    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
    }

    protected void doDeleteChildIndexDocuments( final Node node )
    {
        final Nodes childrenNodes = nodeDao.getByParent( node.path() );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = Strings.startsWithIgnoreCase( nodeName, ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                indexService.deleteEntity( child.id() );
                doDeleteChildIndexDocuments( child );
            }
        }
    }

    protected static class Builder<B extends Builder>
    {
        private ElasticsearchIndexService indexService;

        private NodeDao nodeDao;

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
    }
}
