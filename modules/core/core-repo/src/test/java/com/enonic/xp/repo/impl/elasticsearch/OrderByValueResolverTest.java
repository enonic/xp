package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.xp.repo.impl.entity.AbstractNodeTest;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.result.SearchResult;
import com.enonic.xp.repo.impl.index.result.SearchResultEntry;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.Repository;

import static org.junit.Assert.*;

public class OrderByValueResolverTest
    extends AbstractNodeTest
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

        this.repository = TEST_REPO;

        this.indexName = IndexNameResolver.resolveSearchIndexName( repository.getId() );
        this.indexType = "test";
    }

    @Test
    public void ensureOrderingCorrectForLongValues()
        throws Exception
    {

        final String hundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( 10000.0 ) ) );
        final String thousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( 100000.0 ) ) );
        final String minusThousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( -1000.0 ) ) );
        final String minusHundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( -100.0 ) ) );
        final String zero = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( ValueFactory.newDouble( 0.0 ) ) );
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
            addEntry( new StoreDocumentOrderbyItem( IndexPath.from( "ordervalue" ), value ) ).
            build();

        IndexRequestBuilder indexRequestBuilder = new IndexRequestBuilder( this.client, this.indexName ).
            setType( this.indexType ).
            setSource( StoreDocumentXContentBuilderFactory.create( storeDocument ) );

        return elasticsearchDao.store( indexRequestBuilder.request() );
    }

}
