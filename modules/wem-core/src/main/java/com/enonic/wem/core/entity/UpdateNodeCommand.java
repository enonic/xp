package com.enonic.wem.core.entity;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.core.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;
import com.enonic.wem.api.util.Exceptions;

import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;

final class UpdateNodeCommand
{
    private ElasticsearchIndexService indexService;

    private NodeDao nodeDao;

    private UpdateNodeParams params;

    private UpdateNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
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
            throw Exceptions.newRutime( "Error updating node " + params.getId() ).withCause( e );
        }
    }

    private Node doExecute()
        throws Exception
    {
        final Node beforeChange = nodeDao.getById( params.getId() );

        final Node.EditBuilder editBuilder = params.getEditor().edit( beforeChange );
        if ( !editBuilder.isChanges() )
        {
            return beforeChange;
        }

        final Node edited = editBuilder.build();
        beforeChange.checkIllegalEdit( edited );

        final UpdateNodeArgs updateNodeArgs = newUpdateItemArgs().
            updater( UserKey.superUser() ).
            nodeToUpdate( params.getId() ).
            name( edited.name() ).
            rootDataSet( edited.data() ).
            attachments( edited.attachments() ).
            entityIndexConfig( edited.getEntityIndexConfig() ).
            build();

        final Node updatedNode = nodeDao.update( updateNodeArgs );

        indexService.index( updatedNode );

        return updatedNode;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ElasticsearchIndexService indexService;

        private NodeDao nodeDao;

        private UpdateNodeParams params;

        Builder indexService( final ElasticsearchIndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        Builder nodeDao( final NodeDao session )
        {
            this.nodeDao = session;
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
