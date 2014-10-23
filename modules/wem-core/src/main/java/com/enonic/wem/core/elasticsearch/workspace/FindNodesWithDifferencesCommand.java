package com.enonic.wem.core.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.repository.StorageNameResolver;

public class FindNodesWithDifferencesCommand
    extends AbstractWorkspaceCommand
{
    private final Workspace source;

    private final Workspace target;

    private FindNodesWithDifferencesCommand( Builder builder )
    {
        super( builder );
        source = builder.source;
        target = builder.target;
    }

    public static Builder create()
    {
        return new Builder();
    }

    NodeIds execute()
    {

        final TermQueryBuilder inSource = createWorkspaceQuery( this.source );
        final TermQueryBuilder inTarget = createWorkspaceQuery( this.target );

        final long inSourceCount = elasticsearchDao.count( createGetBlobKeyQueryMetaData( 0, this.repositoryId ), inSource );
        final long inTargetCount = elasticsearchDao.count( createGetBlobKeyQueryMetaData( 0, this.repositoryId ), inTarget );

        final long totalCount = inSourceCount + inTargetCount;

        final BoolQueryBuilder inOnOfTheWorkspaces = new BoolQueryBuilder().
            should( inSource ).
            should( inTarget ).
            minimumNumberShouldMatch( 1 );

        final String changedAggregationName = "changed";

        final TermsBuilder changedAggregationQuery = AggregationBuilders.
            terms( changedAggregationName ).
            size( (int) (long) totalCount ).
            order( Terms.Order.count( true ) );

        final SearchResult searchResult = elasticsearchDao.search( ElasticsearchQuery.create().
            query( inOnOfTheWorkspaces ).
            setAggregations( Sets.newHashSet( changedAggregationQuery ) ).
            size( 0 ).
            from( 0 ).
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexType( source.getName() ).
            build() );

        final Aggregation changedAggregation = searchResult.getAggregations().get( changedAggregationName );

        if ( changedAggregation instanceof BucketAggregation )
        {
            return ChangedIdsResolver.resolve( (BucketAggregation) changedAggregation );
        }
        else
        {
            throw new ClassCastException(
                "Aggregation of unexpected type, should be BucketAggregation, was " + changedAggregation.getClass().getName() );
        }
    }


    static final class Builder
        extends AbstractWorkspaceCommand.Builder<Builder>
    {
        private Workspace source;

        private Workspace target;

        private Builder()
        {
        }

        public Builder source( final Workspace source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public FindNodesWithDifferencesCommand build()
        {
            return new FindNodesWithDifferencesCommand( this );
        }
    }
}
