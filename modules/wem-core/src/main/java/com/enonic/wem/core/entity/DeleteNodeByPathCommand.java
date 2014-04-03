package com.enonic.wem.core.entity;

import javax.jcr.Session;

import org.elasticsearch.common.Strings;

import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.AttachmentsJcrMapper;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;

public class DeleteNodeByPathCommand
{
    private IndexService indexService;

    private Session session;

    private DeleteNodeByPathParams params;

    public DeleteNodeByPathCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.session = builder.session;
        this.params = builder.params;
    }

    public Node execute()
    {
        this.params.validate();
        return doExecute();
    }

    private Node doExecute()
    {
        final NodePath nodePath = params.getPath();
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private Session session;

        private DeleteNodeByPathParams params;

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public Builder session( final Session session )
        {
            this.session = session;
            return this;
        }

        public Builder params( final DeleteNodeByPathParams params )
        {
            this.params = params;
            return this;
        }

        public DeleteNodeByPathCommand build()
        {
            return new DeleteNodeByPathCommand( this );
        }
    }

}
