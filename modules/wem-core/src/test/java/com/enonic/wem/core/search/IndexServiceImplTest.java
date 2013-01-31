package com.enonic.wem.core.search;

import java.util.List;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.core.search.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.search.elastic.IndexMapping;
import com.enonic.wem.core.search.elastic.IndexMappingProvider;

public class IndexServiceImplTest
    extends IndexConstants
{

    private IndexService indexService;

    @Before
    public void init()
    {
        indexService = new IndexService();
        indexService.setDoReindexOnEmptyIndex( false );
    }

    @Test
    public void testInitializeIndex_index_not_exists()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( WEM_INDEX, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( WEM_INDEX ) ).thenReturn( false );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( WEM_INDEX );
    }

    @Test
    public void testInitializeIndex_index_exists()
        throws Exception
    {

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( WEM_INDEX, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( WEM_INDEX ) ).thenReturn( true );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        Mockito.verify( elasticsearchIndexService, Mockito.never() ).createIndex( WEM_INDEX );
    }

    @Test
    public void testInitializeIndex_index_already_exists_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( WEM_INDEX, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( WEM_INDEX ) ).thenReturn( false );

        Mockito.doThrow( new IndexAlreadyExistsException( null ) ).when( elasticsearchIndexService ).createIndex( WEM_INDEX );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test(expected = ElasticSearchException.class)
    public void testInitializeIndex_create_index_fails_with_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( WEM_INDEX, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( WEM_INDEX ) ).thenReturn( false );

        Mockito.doThrow( new ElasticSearchException( "expected" ) ).when( elasticsearchIndexService ).createIndex( WEM_INDEX );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    private IndexMappingProvider setUpIndexMappingMock()
    {
        final IndexMappingProvider indexMappingProvider = Mockito.mock( IndexMappingProvider.class );
        List<IndexMapping> indexMappings =
            Lists.newArrayList( new IndexMapping( WEM_INDEX, IndexType.ACCOUNT.getIndexTypeName(), "Testings 1234" ) );
        Mockito.when( indexMappingProvider.getMappingsForIndex( WEM_INDEX ) ).thenReturn( indexMappings );
        return indexMappingProvider;
    }

}
