package com.enonic.wem.core.search.account;

import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;
import com.enonic.wem.core.search.facet.FacetEntry;

@Component
public class AccountSearchService
{
    private Client client;

    private AccountQueryTranslator translator;

    public AccountSearchResults search( AccountSearchQuery query )
    {
        final SearchRequest req =
            Requests.searchRequest( IndexConstants.WEM_INDEX.value() ).types( IndexType.ACCOUNT.getIndexTypeName() ).searchType(
                getSearchType( query ) ).source( this.translator.build( query ) );

        final SearchResponse res = this.client.search( req ).actionGet();

//        LOG.info( "Search result: " + res.toString() );

        final SearchHits hits = res.getHits();

        final AccountSearchResults searchResult = new AccountSearchResults( query.getFrom(), (int) hits.getTotalHits() );
        if ( query.isIncludeResults() )
        {
            addSearchHits( searchResult, hits );
        }

        if ( query.isIncludeFacets() )
        {
            final Facets facets = res.facets();
            addSearchFacets( searchResult, facets );
        }

        return searchResult;
    }

    private SearchType getSearchType( AccountSearchQuery query )
    {
        if ( query.isIncludeResults() )
        {
            return SearchType.QUERY_THEN_FETCH;
        }
        else
        {
            return SearchType.COUNT;
        }
    }

    private void addSearchFacets( AccountSearchResults searchResult, Facets facets )
    {
        for ( Facet facet : facets )
        {
            if ( facet instanceof TermsFacet )
            {
                TermsFacet tf = (TermsFacet) facet;
                com.enonic.wem.core.search.facet.Facet resultFacet = new com.enonic.wem.core.search.facet.Facet( tf.name() );
                searchResult.getFacets().addFacet( resultFacet );
                for ( TermsFacet.Entry entry : tf )
                {
                    FacetEntry facetEntry = new FacetEntry( entry.term(), entry.count() );
                    resultFacet.addEntry( facetEntry );
                }
            }
        }
    }

    private void addSearchHits( AccountSearchResults searchResult, SearchHits hits )
    {
        final int hitCount = hits.getHits().length;
        for ( int i = 0; i < hitCount; i++ )
        {
            final SearchHit hit = hits.getAt( i );
            final String key = (String) hit.sourceAsMap().get( "key" );
            searchResult.add( AccountKey.from( key ), hit.score() );
        }
    }

    public void deleteIndex( String id )
    {
        deleteIndex( id, false );
    }

    void deleteIndex( String id, boolean flushDataAfterDelete )
    {
        final DeleteRequest deleteRequest =
            new DeleteRequest().index( IndexConstants.WEM_INDEX.value() ).type( IndexType.ACCOUNT.getIndexTypeName() ).id( id );
        this.client.delete( deleteRequest ).actionGet();

        if ( flushDataAfterDelete )
        {
            flush();
        }
    }

    private void flush()
    {
        this.client.admin().indices().flush( new FlushRequest( IndexConstants.WEM_INDEX.value() ).refresh( true ) ).actionGet();
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }

    @Autowired
    public void setTranslator( AccountQueryTranslator translator )
    {
        this.translator = translator;
    }
}
