package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.SearchPreference;

import static java.util.Objects.requireNonNull;

public class GetNodeByPathCommand
    extends AbstractNodeCommand
{
    private final NodePath path;

    private final SearchPreference searchPreference;

    private GetNodeByPathCommand( final Builder builder )
    {
        super( builder );
        path = builder.path;
        searchPreference = builder.searchPreference;
    }

    public Node execute()
    {
        return this.nodeStorageService.get( path, createInternalContext( searchPreference ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath path;

        private SearchPreference searchPreference;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodePath( NodePath path )
        {
            this.path = path;
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
            requireNonNull( this.path, "path is required" );
        }

        public GetNodeByPathCommand build()
        {
            this.validate();
            return new GetNodeByPathCommand( this );
        }
    }
}
