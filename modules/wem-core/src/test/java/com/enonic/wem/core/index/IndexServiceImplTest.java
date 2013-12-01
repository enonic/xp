package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.List;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
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
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.WEM, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.WEM ) ).thenReturn( false );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( Index.WEM );
    }

    @Test
    public void testInitializeIndex_index_exists()
        throws Exception
    {

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.WEM, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.WEM ) ).thenReturn( true );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.never() ).createIndex( Index.WEM );
    }

    @Test
    public void testInitializeIndex_index_already_exists_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.WEM, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.WEM ) ).thenReturn( false );

        Mockito.doThrow( new IndexAlreadyExistsException( null ) ).when( elasticsearchIndexService ).createIndex( Index.WEM );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test(expected = ElasticSearchException.class)
    public void testInitializeIndex_create_index_fails_with_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.WEM, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.WEM ) ).thenReturn( false );

        Mockito.doThrow( new ElasticSearchException( "expected" ) ).when( elasticsearchIndexService ).createIndex( Index.WEM );

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
        Mockito.when( elasticsearchIndexService.getIndexStatus( Index.WEM, true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( Index.WEM ) ).thenReturn( false );
        indexService.setDoReindexOnEmptyIndex( true );

        final ReindexService reindexService = Mockito.mock( ReindexService.class );
        indexService.setReindexService( reindexService );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.start();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( Index.WEM );
        Mockito.verify( reindexService, Mockito.times( 1 ) ).reindexAccounts();
    }

    @Test
    public void testIndexAccount()
    {
        Account account = UserAccount.create( "enonic:rmy" );

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.indexAccount( account );

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).index( Mockito.isA( Collection.class ) );
    }

    private IndexMappingProvider setUpIndexMappingMock()
    {
        final IndexMappingProvider indexMappingProvider = Mockito.mock( IndexMappingProvider.class );
        List<IndexMapping> indexMappings =
            Lists.newArrayList( new IndexMapping( Index.WEM, IndexType.ACCOUNT.getIndexTypeName(), "Testings 1234" ) );
        Mockito.when( indexMappingProvider.getMappingsForIndex( Index.WEM ) ).thenReturn( indexMappings );
        return indexMappingProvider;
    }

}
