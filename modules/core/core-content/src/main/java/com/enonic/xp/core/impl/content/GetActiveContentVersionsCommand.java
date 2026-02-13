package com.enonic.xp.core.impl.content;

import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersion;

public class GetActiveContentVersionsCommand
    extends AbstractContentCommand
{
    @NonNull
    private final GetActiveContentVersionsParams params;

    private GetActiveContentVersionsCommand( final Builder builder )
    {
        super( builder );
        this.params = Objects.requireNonNull( builder.params );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public GetActiveContentVersionsResult execute()
    {
        final GetActiveNodeVersionsResult nodeResult = nodeService.getActiveVersions(
            GetActiveNodeVersionsParams.create().nodeId( NodeId.from( params.getContentId() ) ).branches( params.getBranches() ).build() );

        final GetActiveContentVersionsResult.Builder resultBuilder = GetActiveContentVersionsResult.create();

        for ( Map.Entry<Branch, NodeVersion> entry : nodeResult.getNodeVersions().entrySet() )
        {
            resultBuilder.add( entry.getKey(), this.createVersion( entry.getValue() ) );
        }

        return resultBuilder.build();
    }

    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        @Nullable
        private GetActiveContentVersionsParams params;

        private Builder()
        {
        }

        public Builder params( final GetActiveContentVersionsParams params )
        {
            this.params = params;
            return this;
        }

        public GetActiveContentVersionsCommand build()
        {
            return new GetActiveContentVersionsCommand( this );
        }
    }
}
