package com.enonic.wem.core.entity;

import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.AttachmentsJcrMapper;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

final class DeleteNodeByPathCommand
{
    private IndexService indexService;

    private Session session;

    private NodePath nodePath;

    DeleteNodeByPathCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.session = builder.session;
        this.nodePath = builder.nodePath;
    }

    Node execute()
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node nodeToDelete = nodeJcrDao.getNodeByPath( nodePath );

        doDeleteChildrenFromIndex( nodeJcrDao, nodeToDelete );

        nodeJcrDao.deleteNodeByPath( nodePath );
        JcrSessionHelper.save( session );

        indexService.deleteEntity( nodeToDelete.id() );

        return nodeToDelete;
    }

    private void doDeleteChildrenFromIndex( final NodeJcrDao nodeJcrDao, final Node parent )
    {
        final Nodes childrenNodes = nodeJcrDao.getNodesByParentPath( parent.path() );

        for ( final Node child : childrenNodes )
        {
            final String nodeName = child.name().toString();

            final boolean isAttachmentNode = Strings.startsWithIgnoreCase( nodeName, AttachmentsJcrMapper.ATTACHMENTS_NODE_NAME );
            if ( !isAttachmentNode )
            {
                indexService.deleteEntity( child.id() );
                doDeleteChildrenFromIndex( nodeJcrDao, child );
            }
        }
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private IndexService indexService;

        private Session session;

        private NodePath nodePath;

        Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        Builder session( final Session session )
        {
            this.session = session;
            return this;
        }

        Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        DeleteNodeByPathCommand build()
        {
            return new DeleteNodeByPathCommand( this );
        }
    }

}
