package com.enonic.wem.core.entity;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.core.entity.dao.MoveNodeArguments;
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.api.util.Exceptions;

final class RenameNodeCommand
{
    private RenameNodeParams params;

    private IndexService indexService;

    private NodeElasticsearchDao nodeElasticsearchDao;

    Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error renaming node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final EntityId entityId = params.getEntityId();
        final Node existingNode = nodeElasticsearchDao.getById( entityId );

        if ( existingNode == null )
        {
            final ContentId contentId = ContentId.from( entityId.toString() );
            throw new ContentNotFoundException( contentId );
        }

        final NodePath newPath = new NodePath( existingNode.parent().asAbsolute(), params.getNodeName() );

        final MoveNodeArguments moveNodeArguments = MoveNodeArguments.newMoveNode().
            name( params.getNodeName() ).
            path( newPath ).
            updater( UserKey.superUser() ).
            nodeToMove( params.getEntityId() ).
            build();

        nodeElasticsearchDao.move( moveNodeArguments );

        final Node renamedNode = nodeElasticsearchDao.getById( params.getEntityId() );

        this.indexService.indexNode( renamedNode );

        return renamedNode;
    }

    RenameNodeCommand params( RenameNodeParams params )
    {
        this.params = params;
        return this;
    }

    RenameNodeCommand indexService( final IndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    RenameNodeCommand nodeElasticsearchDao( final NodeElasticsearchDao nodeElasticsearchDao )
    {
        this.nodeElasticsearchDao = nodeElasticsearchDao;
        return this;
    }
}
