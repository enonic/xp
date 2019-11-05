package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
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
            query( QueryBuilders.boolQuery().filter( query ).filter( idFilterBuilder ) ).
            addIndexName( copyRequest.getStorageSource().getStorageName().getName() ).
            addIndexType( copyRequest.getStorageSource().getStorageType().getName() ).
            size( copyRequest.getNodeIds().getSize() ).
            batchSize( BATCH_SIZE ).
            from( 0 ).
            setReturnFields( ReturnFields.from( NodeIndexPath.SOURCE ) ).
            build();
    }

    public static Builder create( final RestHighLevelClient client )
    {
        return new Builder( client );
    }

    public void execute()
    {
        final SearchRequest searchRequest = createScrollRequest( createQuery() );

        try
        {
            SearchResponse scrollResp = client.search( searchRequest, RequestOptions.DEFAULT );

            while ( true )
            {
                LOG.debug( "Copy: Fetched [" + scrollResp.getHits().getHits().length + "] hits, processing" );

                if ( scrollResp.getHits().getHits().length > 0 )
                {
                    doCopy( scrollResp );
                }

                final SearchScrollRequest searchScrollRequest = new SearchScrollRequest( scrollResp.getScrollId() ).
                    scroll( defaultScrollTime );

                scrollResp = client.scroll( searchScrollRequest, RequestOptions.DEFAULT );
                if ( scrollResp.getHits().getHits().length == 0 )
                {
                    clearScroll( scrollResp );
                    break;
                }
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private void doCopy( final SearchResponse scrollResp )
    {
        final BulkRequest bulkRequest = new BulkRequest();

        final org.elasticsearch.search.SearchHits hits = scrollResp.getHits();

        for ( final org.elasticsearch.search.SearchHit hit : hits )
        {
            bulkRequest.add( Requests.indexRequest().
                id( hit.getId() ).
                index( IndexNameResolver.resolveSearchIndexName( copyRequest.getTargetRepo() ) ).
                source( hit.getSourceAsMap() ).
                setRefreshPolicy( WriteRequest.RefreshPolicy.NONE ) );
        }

        final Stopwatch timer = Stopwatch.createStarted();
        try
        {
            final BulkResponse response = client.bulk( bulkRequest, RequestOptions.DEFAULT );
            LOG.debug( "Copied [" + response.getItems().length + "] in " + timer.stop() );
            reportProgress( response.getItems().length );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }


    public static final class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private CopyRequest request;

        private Builder( final RestHighLevelClient client )
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
