package com.enonic.xp.repo.impl.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SearchSources;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;
import com.enonic.xp.storage.spi.SearchResult;

import static java.util.Objects.requireNonNull;

public class FindNodesByQueryCommand
    extends AbstractNodeCommand
{
    private final NodeQuery query;

    private final ReturnFields returnFields;

    private FindNodesByQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
        returnFields = builder.returnFields;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByQueryResult execute()
    {
        final SearchResult result =
            nodeSearchService.query( this.query, returnFields, SearchSources.from( InternalContext.from( ContextAccessor.current() ) ) );

        return FindNodesByQueryResultFactory.create( result );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeQuery query;

        private ReturnFields returnFields = ReturnFields.empty();

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
