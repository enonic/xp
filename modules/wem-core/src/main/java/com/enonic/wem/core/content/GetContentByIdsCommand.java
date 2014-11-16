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
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.repo.NoNodeWithIdFoundException;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeIds;
import com.enonic.wem.repo.NodeService;
import com.enonic.wem.repo.Nodes;


final class GetContentByIdsCommand
    extends AbstractContentCommand
{
    private final GetContentByIdsParams params;

    private final NodeService nodeService;

    private GetContentByIdsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.nodeService = builder.nodeService;
    }

    Contents execute()
    {
        final Contents contents;

        try
        {
            contents = doExecute();
        }
        catch ( NoNodeWithIdFoundException ex )
        {
            final ContentId contentId = ContentId.from( ex.getId().toString() );
            throw new ContentNotFoundException( contentId, ContextAccessor.current().getWorkspace() );
        }

        return contents;
    }

    private Contents doExecute()
    {
        final NodeIds nodeIds = getAsNodeIds( this.params.getIds() );
        final Nodes nodes = nodeService.getByIds( nodeIds );

        return translator.fromNodes( nodes );
    }

    private NodeIds getAsNodeIds( final ContentIds contentIds )
    {
        final Set<NodeId> nodeIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            nodeIds.add( NodeId.from( iterator.next().toString() ) );
        }

        return NodeIds.from( nodeIds );
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

        public Builder( final GetContentByIdsParams params )
        {
            this.params = params;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( nodeService );
        }

        public GetContentByIdsCommand build()
        {
            return new GetContentByIdsCommand( this );
        }
    }
}
