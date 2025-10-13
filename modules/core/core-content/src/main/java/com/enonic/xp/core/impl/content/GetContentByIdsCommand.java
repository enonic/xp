package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
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
        final NodeIds nodeIds = ContentNodeHelper.toNodeIds( this.params.getIds() );

        final Nodes nodes = nodeService.getByIds( nodeIds );

        return ContentNodeTranslator.fromNodes( nodes );
    }

    public static Builder create( final GetContentByIdsParams params )
    {
        return new Builder( params );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final GetContentByIdsParams params;

        Builder( final GetContentByIdsParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public GetContentByIdsCommand build()
        {
            validate();
            return new GetContentByIdsCommand( this );
        }
    }
}
