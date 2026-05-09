package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

import static java.util.Objects.requireNonNull;

public class FindNodesByQueryCommand
    extends AbstractNodeCommand
{
    private final NodeQuery query;

    private final ReturnFields returnFields;

    private final SearchPreference searchPreference;

    private FindNodesByQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
        returnFields = builder.returnFields;
        searchPreference = builder.searchPreference;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByQueryResult execute()
    {
        final SearchResult result = nodeSearchService.query( this.query, returnFields,
                                                             SingleRepoSearchSource.from(
                                                                 InternalContext.create( ContextAccessor.current() )
                                                                     .searchPreference( searchPreference )
                                                                     .build() ) );

        return FindNodesByQueryResultFactory.create( result );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeQuery query;

        private ReturnFields returnFields = ReturnFields.empty();

        private SearchPreference searchPreference;

        private Builder()
        {
            super();
        }

        public Builder query( NodeQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder returnFields( ReturnFields returnFields )
        {
            this.returnFields = returnFields;
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
            requireNonNull( this.query, "query is required" );
        }

        public FindNodesByQueryCommand build()
        {
            this.validate();
            return new FindNodesByQueryCommand( this );
        }
    }
}
