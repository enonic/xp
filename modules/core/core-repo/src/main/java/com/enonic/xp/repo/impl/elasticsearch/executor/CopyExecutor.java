package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.FilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repo.impl.search.SearchStorageType;
import com.enonic.xp.repo.impl.storage.CopyRequest;

public class CopyExecutor
    extends AbstractExecutor
{
    private final static Logger LOG = LoggerFactory.getLogger( CopyExecutor.class );

    public static final int BATCH_SIZE = 1_000;

    private final CopyRequest copyRequest;

    private CopyExecutor( final Builder builder )
    {
        super( builder );
        this.copyRequest = builder.request;
    }

    private ElasticsearchQuery createQuery()
    {
        final IdFilter idFilter = IdFilter.create().
            fieldName( NodeIndexPath.ID.getPath() ).
            values( copyRequest.getNodeIds() ).
            build();

        final QueryBuilder idFilterBuilder = new FilterBuilderFactory( new SearchQueryFieldNameResolver() ).
            create( Filters.from( idFilter ) );

        QueryBuilder query = QueryBuilders.matchAllQuery();

        return ElasticsearchQuery.create().
            query( QueryBuilders.filteredQuery( query, idFilterBuilder ) ).
            addIndexName( copyRequest.getStorageSource().getStorageName().getName() ).
            addIndexType( copyRequest.getStorageSource().getStorageType().getName() ).
            size( copyRequest.getNodeIds().getSize() ).
            batchSize( BATCH_SIZE ).
            from( 0 ).
            setReturnFields( ReturnFields.from( NodeIndexPath.SOURCE ) ).
            build();
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public void execute()
    {
        final SearchRequestBuilder searchRequestBuilder = createScrollRequest( createQuery() );

        SearchResponse scrollResp = searchRequestBuilder.
            execute().
            actionGet();

        while ( true )
        {
            LOG.debug( "Copy: Fetched [" + scrollResp.getHits().hits().length + "] hits, processing" );

            if ( scrollResp.getHits().getHits().length > 0 )
            {
                doCopy( scrollResp );
            }

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).
                setScroll( defaultScrollTime ).
                execute().
                actionGet();

            if ( scrollResp.getHits().getHits().length == 0 )
            {
                clearScroll( scrollResp );
                break;
            }
        }

    }

    private void doCopy( final SearchResponse scrollResp )
    {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        final org.elasticsearch.search.SearchHits hits = scrollResp.getHits();

        for ( final org.elasticsearch.search.SearchHit hit : hits )
        {
            bulkRequest.add( Requests.indexRequest().
                id( hit.id() ).
                index( IndexNameResolver.resolveSearchIndexName( copyRequest.getTargetRepo() ) ).
                type( SearchStorageType.from( copyRequest.getTargetBranch() ).getName() ).
                source( hit.source() ).
                refresh( false ) );
        }

        final Stopwatch timer = Stopwatch.createStarted();
        final BulkResponse response = bulkRequest.execute().actionGet();
        LOG.debug( "Copied [" + response.getItems().length + "] in " + timer.stop() );
        reportProgress( response.getItems().length );
    }


    public static final class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private CopyRequest request;

        private Builder( final Client client )
        {
            super( client );
        }

        public Builder request( final CopyRequest request )
        {
            this.request = request;
            return this;
        }

        public CopyExecutor build()
        {
            return new CopyExecutor( this );
        }
    }
}
