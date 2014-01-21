package com.enonic.wem.core.index;

import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.core.index.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.index.elastic.IndexMapping;
import com.enonic.wem.core.index.elastic.IndexMappingProvider;

public class IndexServiceImplTest

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
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.NODB, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.NODB ) ).thenReturn( false );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( Index.NODB );
    }

    @Test
    public void testInitializeIndex_index_exists()
        throws Exception
    {

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.NODB, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.NODB ) ).thenReturn( true );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.never() ).createIndex( Index.NODB );
    }

    @Test
    public void testInitializeIndex_index_already_exists_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.NODB, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.NODB ) ).thenReturn( false );

        Mockito.doThrow( new IndexAlreadyExistsException( null ) ).when( elasticsearchIndexService ).createIndex( Index.NODB );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test(expected = ElasticsearchException.class)
    public void testInitializeIndex_create_index_fails_with_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.NODB, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.NODB ) ).thenReturn( false );

        Mockito.doThrow( new ElasticsearchException( "expected" ) ).when( elasticsearchIndexService ).createIndex( Index.NODB );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test
    public void testInitializeIndex_reindex_on_create()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.NODB, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.NODB ) ).thenReturn( false );
        indexService.setDoReindexOnEmptyIndex( true );

        final ReindexService reindexService = Mockito.mock( ReindexService.class );
        indexService.setReindexService( reindexService );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( Index.NODB );
    }

    private IndexMappingProvider setUpIndexMappingMock()
    {
        final IndexMappingProvider indexMappingProvider = Mockito.mock( IndexMappingProvider.class );
        List<IndexMapping> indexMappings =
            Lists.newArrayList( new IndexMapping( Index.NODB, IndexType.NODE.getIndexTypeName(), "Testings 1234" ) );
        Mockito.when( indexMappingProvider.getMappingsForIndex( Index.NODB ) ).thenReturn( indexMappings );
        return indexMappingProvider;
    }

}
