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
import com.enonic.wem.util.Exceptions;

public class RenameNodeCommand
{
    private RenameNodeParams params;

    private IndexService indexService;

    private Session session;

    public boolean execute()
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

    private boolean doExecute()
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

        final boolean moved = nodeJcrDao.moveNode( existingNode.path().asAbsolute(),
                                                   new NodePath( existingNode.parent().asAbsolute(), params.getNodeName() ) );

        session.save();

        final Node changedNode = nodeJcrDao.getNodeById( entityId );
        this.indexService.indexNode( changedNode );

        return moved;
    }

    public RenameNodeCommand params( RenameNodeParams params )
    {
        this.params = params;
        return this;
    }

    public RenameNodeCommand indexService( final IndexService indexService )
    {
        this.indexService = indexService;
        return this;
    }

    public RenameNodeCommand session( final Session session )
    {
        this.session = session;
        return this;
    }
}
