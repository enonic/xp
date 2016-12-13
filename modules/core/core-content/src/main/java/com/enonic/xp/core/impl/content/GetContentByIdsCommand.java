package com.enonic.xp.core.impl.content;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
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
        final Contents contents = doExecute();
        return filter( contents );
    }

    private Contents doExecute()
    {
        final NodeIds nodeIds = getAsNodeIds( this.params.getIds() );

        final Nodes nodes = nodeService.getByIds( nodeIds );

        return this.translator.fromNodes( nodes, true );
    }

    private NodeIds getAsNodeIds( final ContentIds contentIds )
    {
        final List<NodeId> nodeIds = Lists.newArrayList();

        contentIds.forEach( contentId -> nodeIds.add( NodeId.from( contentId.toString() ) ) );

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

        @Override
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
