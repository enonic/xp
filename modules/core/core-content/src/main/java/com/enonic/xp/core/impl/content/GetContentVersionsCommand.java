package com.enonic.xp.core.impl.content;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.NodeId;

public class GetContentVersionsCommand
    extends AbstractContentCommand
{
    @NonNull
    private final GetContentVersionsParams params;

    private GetContentVersionsCommand( final Builder builder )
    {
        super( builder );
        this.params = Objects.requireNonNull( builder.params );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetContentVersionsResult execute()
    {
        final GetNodeVersionsResult nodeVersionsResult = nodeService.getVersions( GetNodeVersionsParams.create()
                                                                                      .nodeId( NodeId.from( params.getContentId() ) )
                                                                                      .cursor( params.getCursor() )
                                                                                      .size( params.getSize() )
                                                                                      .build() );

        return GetContentVersionsResult.create()
            .totalHits( nodeVersionsResult.getTotalHits() )
            .cursor( nodeVersionsResult.getCursor() )
            .contentVersions(
                nodeVersionsResult.getNodeVersions().stream().map( this::createVersion ).collect( ContentVersions.collector() ) )
            .build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        @Nullable
        private GetContentVersionsParams params;

        private Builder()
        {
        }

        public Builder params( final GetContentVersionsParams params )
        {
            this.params = params;
            return this;
        }

        public GetContentVersionsCommand build()
        {
            return new GetContentVersionsCommand( this );
        }
    }
}
