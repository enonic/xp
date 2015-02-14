package com.enonic.xp.core.impl.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NoNodeWithIdFoundException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;


final class GetContentByIdsCommand
    extends AbstractContentCommand
{
    private final GetContentByIdsParams params;

    private GetContentByIdsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
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
            throw new ContentNotFoundException( contentId, ContextAccessor.current().getBranch() );
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

        public Builder( final GetContentByIdsParams params )
        {
            this.params = params;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public GetContentByIdsCommand build()
        {
            validate();
            return new GetContentByIdsCommand( this );
        }
    }
}
