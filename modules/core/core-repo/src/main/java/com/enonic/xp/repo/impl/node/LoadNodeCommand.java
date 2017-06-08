package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.Node;

public class LoadNodeCommand
    extends AbstractNodeCommand
{
    private final LoadNodeParams params;

    private LoadNodeCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public LoadNodeResult execute()
    {
        final Node node = StoreNodeCommand.create( this ).
            node( params.getNode() ).
            updateMetadataOnly( false ).
            build().
            execute();

        return LoadNodeResult.create().
            node( node ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private LoadNodeParams params;

        private Builder()
        {
        }

        public Builder params( final LoadNodeParams val )
        {
            params = val;
            return this;
        }

        public LoadNodeCommand build()
        {
            return new LoadNodeCommand( this );
        }
    }
}
