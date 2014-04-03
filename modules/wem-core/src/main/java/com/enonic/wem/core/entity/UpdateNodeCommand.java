package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.util.Exceptions;

import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;

public class UpdateNodeCommand
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

    public UpdateNodeResult execute()
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

    private UpdateNodeResult doExecute()
        throws Exception
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node beforeChange = nodeJcrDao.getNodeById( params.getId() );

        final Node.EditBuilder editBuilder = params.getEditor().edit( beforeChange );
        if ( !editBuilder.isChanges() )
        {
            return new UpdateNodeResult( beforeChange );
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

        return new UpdateNodeResult( updatedNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private Session session;

        private UpdateNodeParams params;

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

        public Builder params( final UpdateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public UpdateNodeCommand build()
        {
            return new UpdateNodeCommand( this );
        }
    }

}
