package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;
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
        //     createSearchIndex( this.repository );
        refresh();

        final String hundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 10000.0 ) ) );
        final String thousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 100000.0 ) ) );
        final String minusThousand = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( -1000.0 ) ) );
        final String minusHundred = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( -100.0 ) ) );
        final String zero = storeOrderbyDocument( OrderbyValueResolver.getOrderbyValue( Value.newDouble( 0.0 ) ) );
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
