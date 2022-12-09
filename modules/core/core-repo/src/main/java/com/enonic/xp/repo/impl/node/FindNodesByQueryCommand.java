package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

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

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public FindNodesByQueryResult execute()
    {
        final SearchResult result =
            nodeSearchService.query( this.query, returnFields, SingleRepoSearchSource.from( ContextAccessor.current() ) );

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

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
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
            Preconditions.checkNotNull( this.query );
        }

        public FindNodesByQueryCommand build()
        {
            this.validate();
            return new FindNodesByQueryCommand( this );
        }
    }
}
