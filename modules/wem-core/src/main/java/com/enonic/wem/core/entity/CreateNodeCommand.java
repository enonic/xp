package com.enonic.wem.core.entity;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;

import static com.enonic.wem.core.entity.dao.CreateNodeArguments.newCreateNodeArgs;

final class CreateNodeCommand
    extends AbstractNodeCommand
{
    private CreateNodeParams params;

    private CreateNodeCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    Node execute()
    {
        return doExecute();
    }

    private Node doExecute()
    {
        final CreateNodeArguments createNodeArguments = newCreateNodeArgs().
            creator( UserKey.superUser() ).
            parent( params.getParent() ).
            name( params.getName() ).
            rootDataSet( params.getData() ).
            attachments( params.getAttachments() != null ? params.getAttachments() : Attachments.empty() ).
            entityIndexConfig( params.getEntityIndexConfig() ).
            embed( params.isEmbed() ).
            build();

        final Node persistedNode = nodeDao.create( createNodeArguments, context.getWorkspace() );

        this.indexService.index( persistedNode, context.getWorkspace() );

        return persistedNode;
    }

    static Builder create( final Context context )
    {
        return new Builder( context );
    }

    static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private CreateNodeParams params;

        Builder( final Context context )
        {
            super( context );
        }

        Builder params( final CreateNodeParams params )
        {
            this.params = params;
            return this;
        }

        public CreateNodeCommand build()
        {
            return new CreateNodeCommand( this );
        }
    }
}


