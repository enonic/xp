package com.enonic.wem.core.entity;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.dao.CreateNodeArguments;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.entity.dao.CreateNodeArguments.newCreateNodeArgs;

final class CreateNodeCommand
{
    private IndexService indexService;

    private CreateNodeParams params;

    private NodeDao nodeDao;

    private CreateNodeCommand( final Builder builder )
    {
        this.indexService = builder.indexService;
        this.nodeDao = builder.nodeDao;
        this.params = builder.params;
    }

    Node execute()
    {
        this.params.validate();

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

        final Node persistedNode = nodeDao.create( createNodeArguments );

        indexService.indexNode( persistedNode );

        return persistedNode;
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private IndexService indexService;

        private NodeDao nodeDao;

        private CreateNodeParams params;

        Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        Builder nodeDao( final NodeDao nodeDao )
        {
            this.nodeDao = nodeDao;
            return this;
        }

        Builder params( final CreateNodeParams params )
        {
            this.params = params;
            return this;
        }

        CreateNodeCommand build()
        {
            return new CreateNodeCommand( this );
        }
    }
}


