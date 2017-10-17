package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodePathsByQueryResult;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodePathsByQueryCommand
    extends AbstractNodeCommand
{
    private final NodeQuery query;

    private FindNodePathsByQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodePathsByQueryResult execute()
    {
        final SearchResult result = nodeSearchService.query( this.query, ReturnFields.from( NodeIndexPath.PATH ), SingleRepoSearchSource.from( ContextAccessor.current() ) );

        return FindNodePathsByQueryResultFactory.create( result );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeQuery query;

        private Builder()
        {
            super();
        }

        public Builder query( NodeQuery query )
        {
            this.query = query;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.query );
        }

        public FindNodePathsByQueryCommand build()
        {
            this.validate();
            return new FindNodePathsByQueryCommand( this );
        }
    }
}
