package com.enonic.wem.core.entity;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.AttachmentsJcrMapper;
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;
import com.enonic.wem.core.index.IndexService;

public abstract class AbstractDeleteNodeCommand
{
    protected IndexService indexService;

    protected NodeElasticsearchDao nodeElasticsearchDao;

    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeElasticsearchDao = builder.nodeElasticsearchDao;
    }

    protected void doDeleteChildIndexDocuments( final Node node )
    {
        final Nodes childrenNodes = nodeElasticsearchDao.getByParent( node.path() );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = Strings.startsWithIgnoreCase( nodeName, AttachmentsJcrMapper.ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                indexService.deleteEntity( child.id() );
                doDeleteChildIndexDocuments( child );
            }
        }
    }

    protected static class Builder<B extends Builder>
    {
        private IndexService indexService;

        private NodeElasticsearchDao nodeElasticsearchDao;

        @SuppressWarnings("unchecked")
        B indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B nodeElasticsearchDao( final NodeElasticsearchDao nodeElasticsearchDao )
        {
            this.nodeElasticsearchDao = nodeElasticsearchDao;
            return (B) this;
        }
    }
}
