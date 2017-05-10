package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.index.query.NodeQueryResult;
import com.enonic.xp.repo.impl.index.query.NodeQueryResultEntry;

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

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
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

        final NodeQueryResult nodeQueryResult = nodeSearchService.query( query.getNodeQuery(), searchSourceBuilder.build() );

        final FindNodesByMultiRepoQueryResult.Builder resultBuilder = FindNodesByMultiRepoQueryResult.create().
            hits( nodeQueryResult.getHits() ).
            totalHits( nodeQueryResult.getTotalHits() ).
            aggregations( nodeQueryResult.getAggregations() );

        for ( final NodeQueryResultEntry resultEntry : nodeQueryResult.getEntries() )
        {
            createResultEntry( resultBuilder, resultEntry );
        }

        return resultBuilder.build();
    }

    private void createResultEntry( final FindNodesByMultiRepoQueryResult.Builder resultBuilder, final NodeQueryResultEntry resultEntry )
    {
        resultBuilder.addNodeHit( MultiRepoNodeHit.create().
            nodeId( resultEntry.getId() ).
            score( resultEntry.getScore() ).
            repositoryId( resultEntry.getRepositoryId() ).
            branch( resultEntry.getBranch() ).
            build() );
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private MultiRepoNodeQuery query;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
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
            Preconditions.checkNotNull( this.query );
        }

        public FindNodesByMultiRepoQueryCommand build()
        {
            this.validate();
            return new FindNodesByMultiRepoQueryCommand( this );
        }
    }
}
