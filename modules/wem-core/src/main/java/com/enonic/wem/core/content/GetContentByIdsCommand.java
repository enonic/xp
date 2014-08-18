package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NoEntityWithIdFoundException;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.index.query.QueryService;


final class GetContentByIdsCommand
    extends AbstractContentCommand
{
    private final GetContentByIdsParams params;

    private final NodeService nodeService;

    private final QueryService queryService;

    private GetContentByIdsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.nodeService = builder.nodeService;
        this.queryService = builder.queryService;
    }

    Contents execute()
    {
        final Contents contents;

        try
        {
            contents = doExecute();
        }
        catch ( NoEntityWithIdFoundException ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId, this.context.getWorkspace() );
        }

        return this.params.doGetChildrenIds() ? ChildContentIdsResolver.
            create().
            context( this.context ).
            nodeService( this.nodeService ).
            blobService( this.blobService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            queryService( this.queryService ).
            build().
            resolve( contents ) : contents;
    }

    private Contents doExecute()
    {
        final EntityIds entityIds = getAsEntityIds( this.params.getIds() );
        final Nodes nodes = nodeService.getByIds( entityIds, this.context );

        return translator.fromNodes( nodes );
    }

    private EntityIds getAsEntityIds( final ContentIds contentIds )
    {
        final Set<EntityId> entityIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            entityIds.add( EntityId.from( iterator.next().toString() ) );
        }

        return EntityIds.from( entityIds );
    }


    public static Builder create( final GetContentByIdsParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final GetContentByIdsParams params;

        private NodeService nodeService;

        private QueryService queryService;


        public Builder( final GetContentByIdsParams params )
        {
            this.params = params;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder queryService( final QueryService queryService )
        {
            this.queryService = queryService;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( nodeService );
            Preconditions.checkNotNull( queryService );
        }

        public GetContentByIdsCommand build()
        {
            return new GetContentByIdsCommand( this );
        }
    }
}
