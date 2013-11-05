package com.enonic.wem.core.index.elastic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;

public class ElasticsearchIndexServiceImplTest
{

    ElasticsearchIndexServiceImpl elasticsearchIndexService = new ElasticsearchIndexServiceImpl();

    @Test
    public void testIndexSingle()
        throws Exception
    {
        final Client client = Mockito.mock( Client.class );
        elasticsearchIndexService.setClient( client );

        ActionFuture<IndexResponse> indexResponse = createFutureIndexResponse();

        Mockito.when( client.index( Mockito.isA( IndexRequest.class ) ) ).thenReturn( indexResponse );

        IndexDocument indexDocument = new IndexDocument( "1", IndexType.CONTENT, Index.WEM );

        elasticsearchIndexService.index( Lists.newArrayList( indexDocument ) );

        Mockito.verify( client, Mockito.times( 1 ) ).index( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void testIndexMultiple()
        throws Exception
    {
        final Client client = Mockito.mock( Client.class );
        elasticsearchIndexService.setClient( client );

        ActionFuture<IndexResponse> indexResponse = createFutureIndexResponse();

        Mockito.when( client.index( Mockito.isA( IndexRequest.class ) ) ).thenReturn( indexResponse );

        IndexDocument indexDocument1 = new IndexDocument( "1", IndexType.CONTENT, Index.WEM );
        IndexDocument indexDocument2 = new IndexDocument( "2", IndexType.ACCOUNT, Index.WEM );

        elasticsearchIndexService.index( Lists.newArrayList( indexDocument1, indexDocument2 ) );

        Mockito.verify( client, Mockito.times( 2 ) ).index( Mockito.isA( IndexRequest.class ) );

    }


    private ActionFuture<IndexResponse> createFutureIndexResponse()
    {
        return new ActionFuture<IndexResponse>()
        {
            @Override
            public IndexResponse actionGet()
                throws ElasticSearchException
            {
                return new IndexResponse();
            }

            @Override
            public IndexResponse actionGet( final String timeout )
                throws ElasticSearchException
            {
                return null;
            }

            @Override
            public IndexResponse actionGet( final long timeoutMillis )
                throws ElasticSearchException
            {
                return null;
            }

            @Override
            public IndexResponse actionGet( final long timeout, final TimeUnit unit )
                throws ElasticSearchException
            {
                return null;
            }

            @Override
            public IndexResponse actionGet( final TimeValue timeout )
                throws ElasticSearchException
            {
                return null;
            }

            @Override
            public Throwable getRootFailure()
            {
                return null;
            }

            @Override
            public boolean cancel( final boolean mayInterruptIfRunning )
            {
                return false;
            }

            @Override
            public boolean isCancelled()
            {
                return false;
            }

            @Override
            public boolean isDone()
            {
                return false;
            }

            @Override
            public IndexResponse get()
                throws InterruptedException, ExecutionException
            {
                return null;
            }

            @Override
            public IndexResponse get( final long timeout, final TimeUnit unit )
                throws InterruptedException, ExecutionException, TimeoutException
            {
                return null;
            }
        };
    }
}
