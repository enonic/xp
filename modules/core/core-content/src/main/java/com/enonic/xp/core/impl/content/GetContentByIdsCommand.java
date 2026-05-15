package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;

import static java.util.Objects.requireNonNull;


final class GetContentByIdsCommand
    extends AbstractContentCommand
{
    private final GetContentByIdsParams params;

    private final boolean allowRoot;

    private GetContentByIdsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.allowRoot = builder.allowRoot;
    }

    Contents execute()
    {
        final Contents contents = doExecute();
        return filter( contents );
    }

    private Contents doExecute()
    {
        final NodeIds nodeIds = ContentNodeHelper.toNodeIds( this.params.getIds() );

        final Nodes nodes = nodeService.getByIds( nodeIds );

        final Nodes filteredNodes =
            allowRoot ? nodes : nodes.stream().filter( n -> !isProtectedRoot( n.path() ) ).collect( Nodes.collector() );

        return ContentNodeTranslator.fromNodes( filteredNodes );
    }

    public static Builder create( final GetContentByIdsParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final GetContentByIdsParams params;

        private boolean allowRoot;

        Builder( final GetContentByIdsParams params )
        {
            this.params = params;
        }

        public Builder allowRoot()
        {
            this.allowRoot = true;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( params, "params cannot be null" );
        }

        public GetContentByIdsCommand build()
        {
            validate();
            return new GetContentByIdsCommand( this );
        }
    }
}
