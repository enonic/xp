package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.util.Exceptions;

import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;

final class UpdateNodeCommand
{
    private IndexService indexService;

    private Session session;

    private UpdateNodeParams params;

    private UpdateNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.session = builder.session;
        this.params = builder.params;
    }

    Node execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error updating node" ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node beforeChange = nodeJcrDao.getNodeById( params.getId() );

        final Node.EditBuilder editBuilder = params.getEditor().edit( beforeChange );
        if ( !editBuilder.isChanges() )
        {
            return beforeChange;
        }

        final Node edited = editBuilder.build();
        beforeChange.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            nodeToUpdate( params.getId() ).
            name( edited.name() ).
            rootDataSet( edited.data() ).
            attachments( edited.attachments() ).
            entityIndexConfig( edited.getEntityIndexConfig() ).
            build();

        final Node updatedNode = nodeJcrDao.updateNode( updateNodeArgs );
        session.save();

        indexService.indexNode( updatedNode );

        return updatedNode;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private IndexService indexService;

        private Session session;

        private UpdateNodeParams params;

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

        Builder params( final UpdateNodeParams params )
        {
            this.params = params;
            return this;
        }

        UpdateNodeCommand build()
        {
            return new UpdateNodeCommand( this );
        }
    }

}
