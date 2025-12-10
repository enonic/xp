package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class FindNodesByMultiRepoQueryCommand
    extends AbstractNodeCommand
{
    private final MultiRepoNodeQuery query;

    private FindNodesByMultiRepoQueryCommand( Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByMultiRepoQueryResult execute()
    {
        final SearchTargets searchTargets = query.getSearchTargets();

        final MultiRepoSearchSource.Builder searchSourceBuilder = MultiRepoSearchSource.create();

        for ( final SearchTarget searchTarget : searchTargets )
        {
            searchSourceBuilder.add( SingleRepoSearchSource.create().
                branch( searchTarget.getBranch() ).
                repositoryId( searchTarget.getRepositoryId() ).
                acl( searchTarget.getPrincipalKeys() ).
                build() );
        }

        final SearchResult result = nodeSearchService.query( this.query.getNodeQuery(), searchSourceBuilder.build() );

        return FindNodesByMultiRepoQueryResultFactory.create( result );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private MultiRepoNodeQuery query;

        private Builder()
        {
            super();
        }

        public Builder query( MultiRepoNodeQuery query )
        {
            this.query = query;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( this.query, "query is required" );
        }

        public FindNodesByMultiRepoQueryCommand build()
        {
            this.validate();
            return new FindNodesByMultiRepoQueryCommand( this );
        }
    }
}
