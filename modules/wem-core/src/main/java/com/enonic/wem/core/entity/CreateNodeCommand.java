package com.enonic.wem.core.entity;


import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.util.Exceptions;

import static com.enonic.wem.core.entity.dao.CreateNodeArguments.newCreateNodeArgs;

public class CreateNodeCommand
{
    private IndexService indexService;

    private CreateNodeParams params;

    private Session session;

    private CreateNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.session = builder.session;
        this.params = builder.params;
    }

    public CreateNodeResult execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error creating node" ).withCause( e );
        }
    }

    private CreateNodeResult doExecute()
        throws Exception
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

        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );
        final Node persistedNode = nodeJcrDao.createNode( createNodeArguments );
        session.save();

        indexService.indexNode( persistedNode );

        return new CreateNodeResult( persistedNode );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IndexService indexService;

        private Session session;

        private CreateNodeParams params;

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

        public Builder params( final CreateNodeParams params )
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


