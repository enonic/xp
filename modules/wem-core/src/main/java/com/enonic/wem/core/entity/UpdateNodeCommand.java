package com.enonic.wem.core.entity;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.core.entity.dao.UpdateNodeArgs;

import static com.enonic.wem.core.entity.dao.UpdateNodeArgs.newUpdateItemArgs;

final class UpdateNodeCommand
    extends AbstractNodeCommand
{
    private final UpdateNodeParams params;

    private UpdateNodeCommand( final Builder builder )
    {
        super( builder );

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
        final Node beforeChange = nodeDao.getById( params.getId(), params.getWorkspace() );

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

        final Node updatedNode = nodeDao.update( updateNodeArgs, params.getWorkspace() );

        indexService.index( updatedNode );

        return updatedNode;
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
    }

    static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private UpdateNodeParams params;

        Builder( final Context context )
        {
            super( context );
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
