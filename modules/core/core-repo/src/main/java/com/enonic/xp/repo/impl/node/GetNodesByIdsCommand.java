package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.SearchPreference;

import static java.util.Objects.requireNonNull;

public class GetNodesByIdsCommand
    extends AbstractNodeCommand
{
    private final NodeIds ids;

    private final SearchPreference searchPreference;

    private GetNodesByIdsCommand( final Builder builder )
    {
        super( builder );
        this.ids = builder.ids;
        this.searchPreference = builder.searchPreference;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes execute()
    {
        return this.nodeStorageService.get( ids, createInternalContext( searchPreference ) );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds ids;

        private SearchPreference searchPreference;

        private Builder()
        {
            super();
        }

        public Builder ids( NodeIds ids )
        {
            this.ids = ids;
            return this;
        }

        public Builder searchPreference( final SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( this.ids, "ids is required" );
        }

        public GetNodesByIdsCommand build()
        {
            this.validate();
            return new GetNodesByIdsCommand( this );
        }
    }
}
