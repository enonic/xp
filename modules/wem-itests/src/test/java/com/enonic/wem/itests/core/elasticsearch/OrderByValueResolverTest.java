package com.enonic.wem.itests.core.elasticsearch;

import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexDocumentItemPath;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.elasticsearch.OrderbyValueResolver;
import com.enonic.wem.core.elasticsearch.document.StoreDocument;
import com.enonic.wem.core.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.RepositoryIndexMappingProvider;

import static org.junit.Assert.*;

public class OrderByValueResolverTest
    extends AbstractElasticsearchIntegrationTest
{
    private Repository repository;

    private String indexName;

    private String indexType;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        this.repository = ContentConstants.CONTENT_REPO;

        this.indexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );
        this.indexType = "test";
    }

    protected void createSearchIndex( final Repository repository )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );
        elasticsearchIndexService.createIndex( indexName, getContentRepoSearchDefaultSettings() );
        elasticsearchIndexService.applyMapping( IndexNameResolver.resolveSearchIndexName( repository.getId() ),
                                                IndexType._DEFAULT_.getName(),
                                                RepositoryIndexMappingProvider.getSearchMappings( repository ) );

        assertTrue( indexExists( indexName ) );
    }

    @Test
    public void ensureOrderingCorrectForLongValues()
        throws Exception
    {
        createSearchIndex( this.repository );
        refresh();

        final String hundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 10000 ) ) );
        final String thousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 100000 ) ) );
        final String minusThousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( -1000 ) ) );
        final String minusHundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( -100 ) ) );
        final String zero = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 0 ) ) );
        refresh();

        final SearchResult result = elasticsearchDao.find( ElasticsearchQuery.create().
            query( QueryBuilderFactory.create().build() ).
            index( this.indexName ).
            indexType( this.indexType ).
            addSortBuilder(
                new FieldSortBuilder( IndexQueryFieldNameResolver.resolveOrderByFieldName( "ordervalue" ) ).order( SortOrder.DESC ) ).
            build() );

        assertEquals( 5, result.getResults().getSize() );

        final Iterator<SearchResultEntry> iterator = result.getResults().iterator();

        assertEquals( "thousand", thousand, iterator.next().getId() );
        assertEquals( "hundred", hundred, iterator.next().getId() );
        assertEquals( "zero", zero, iterator.next().getId() );
        assertEquals( "minusHundred", minusHundred, iterator.next().getId() );
        assertEquals( "minusThousand", minusThousand, iterator.next().getId() );
    }

    private String storeOrderbyDocument( final String value )
    {
        final StoreDocument storeDocument = StoreDocument.create().
            indexTypeName( this.indexType ).
            indexName( this.indexName ).
            analyzer( "default" ).
            addEntry( new StoreDocumentOrderbyItem( IndexDocumentItemPath.from( "ordervalue" ), value ) ).
            build();

        IndexRequestBuilder indexRequestBuilder = new IndexRequestBuilder( this.client, this.indexName ).
            setType( this.indexType ).
            setSource( StoreDocumentXContentBuilderFactory.create( storeDocument ) );

        return elasticsearchDao.store( indexRequestBuilder.request() );
    }

}
