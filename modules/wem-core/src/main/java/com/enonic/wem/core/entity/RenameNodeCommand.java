package com.enonic.wem.core.entity;

import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.api.util.Exceptions;

final class RenameNodeCommand
{
    private RenameNodeParams params;

    private IndexService indexService;

    private Session session;

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
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final EntityId entityId = params.getEntityId();
        final Node existingNode = nodeJcrDao.getNodeById( entityId );

        if ( existingNode == null )
        {
            final ContentId contentId = ContentId.from( entityId.toString() );
            throw new ContentNotFoundException( contentId );
        }

        nodeJcrDao.moveNode( existingNode.path().asAbsolute(), new NodePath( existingNode.parent().asAbsolute(), params.getNodeName() ) );
        session.save();

        final Node renamedNode = nodeJcrDao.getNodeById( entityId );
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

    RenameNodeCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
