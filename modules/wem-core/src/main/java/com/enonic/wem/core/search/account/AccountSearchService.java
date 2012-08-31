package com.enonic.wem.core.search.account;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.FacetEntry;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Component
public class AccountSearchService
{
    private static final String CMS_INDEX = "cms";

    private static final String ACCOUNT_INDEX_TYPE = "account";

    private static final Logger LOG = LoggerFactory.getLogger( AccountSearchService.class );

    private Client client;

    private AccountQueryTranslator translator;

    public void createIndex()
    {
        try
        {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest( CMS_INDEX );
            ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder().loadFromSource(jsonBuilder()
                .startObject()
                    .startObject( "analysis" )
                        .startObject( "analyzer" )
                            .startObject( "keywordlowercase" )
                                .field( "type", "custom" )
                                .field( "tokenizer", "keyword" )
                                .field( "filter", new String[]{"lowercase"} )
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject().string());
            createIndexRequest.settings( settings );
            client.admin().indices().create( createIndexRequest ).actionGet();

            final PutMappingRequest putMappingRequest = new PutMappingRequest( CMS_INDEX );
            putMappingRequest.type( ACCOUNT_INDEX_TYPE );
            final XContentBuilder mapping = buildIndexMapping();
            putMappingRequest.source( mapping );

            client.admin().indices().putMapping( putMappingRequest ).actionGet();
        }
        catch ( org.elasticsearch.indices.IndexAlreadyExistsException e )
        {
            LOG.warn( "Index already exists; skipping index creation", e );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private XContentBuilder buildIndexMapping()
        throws IOException
    {
        final XContentBuilder mapping = XContentFactory.jsonBuilder().prettyPrint();
        mapping.startObject();
        mapping.field( ACCOUNT_INDEX_TYPE ).startObject();
        mapping.field( "properties" ).startObject()
            .field( AccountIndexField.USERSTORE_FIELD.id() )
                .startObject()
                    .field( "type", "multi_field" )
                    .startObject( "fields" )
                        .startObject( AccountIndexField.USERSTORE_FIELD.id() )
                            .field( "type", "string" )
                            .field( "index", "analyzed" )
                        .endObject()
                        .startObject( "untouched" )
                            .field( "type", "string" )
                            .field( "index", "not_analyzed" )
                        .endObject()
                    .endObject()
                .endObject()
            .field( AccountIndexField.DISPLAY_NAME_FIELD.id() )
                .startObject()
                    .field( "type", "multi_field" )
                    .startObject( "fields" )
                        .startObject( AccountIndexField.DISPLAY_NAME_FIELD.id() )
                            .field( "type", "string" )
                            .field( "index", "analyzed" )
                        .endObject()
                        .startObject( "untouched" )
                            .field( "type", "string" )
                            .field( "index", "not_analyzed" )
                        .endObject()
                    .endObject()
                .endObject()
            .field( AccountIndexField.NAME_FIELD.id() )
                .startObject()
                    .field( "type", "multi_field" )
                    .startObject( "fields" )
                        .startObject( AccountIndexField.NAME_FIELD.id() )
                            .field( "type", "string" )
                            .field( "index", "analyzed" )
                        .endObject()
                        .startObject( "untouched" )
                            .field( "type", "string" )
                            .field( "index", "not_analyzed" )
                        .endObject()
                    .endObject()
                .endObject()
            .field( AccountIndexField.KEY_FIELD.id() )
                .startObject()
                    .field( "type", "string" )
                    .field( "enabled", false )
                .endObject()
            .field( AccountIndexField.LAST_MODIFIED_FIELD.id() )
                .startObject()
                    .field( "type", "date" )
                    .field( "store", "yes" )
                    .field( "format", "dateOptionalTime" )
                .endObject()
            .field( AccountIndexField.EMAIL_FIELD.id() )
                .startObject()
                    .field( "type", "string" )
                    .field( "index", "not_analyzed" )
                .endObject()
            .field( AccountIndexField.ORGANIZATION_FIELD.id() )
                .startObject()
                    .field( "type", "multi_field" )
                    .startObject( "fields" )
                        .startObject( AccountIndexField.ORGANIZATION_FIELD.id() )
                            .field( "type", "string" )
                            .field( "index", "analyzed" )
                        .endObject()
                        .startObject( "untouched" )
                            .field( "type", "string" )
                            .field( "index", "not_analyzed" )
                        .endObject()
                        .startObject( "lowercase" )
                            .field( "type", "string" )
                            .field( "index", "analyzed" )
                            .field( "analyzer", "keywordlowercase" )
                        .endObject()
                    .endObject()
                .endObject()
            .endObject()
        .endObject()
        .endObject();

        LOG.info( "Account mapping: " + mapping.string() );
        return mapping;
    }

    public void index( AccountIndexData account )
    {
        final XContentBuilder data = account.getData();
        final String id  = account.getKey().toString();

        final IndexRequest req = Requests.indexRequest()
                .id( id )
                .index( CMS_INDEX )
                .type( ACCOUNT_INDEX_TYPE )
                .source( data );

        this.client.index(req).actionGet();
    }

    public AccountSearchResults search(AccountSearchQuery query)
    {
        final SearchRequest req = Requests.searchRequest( CMS_INDEX )
                .types( ACCOUNT_INDEX_TYPE )
                .searchType( getSearchType( query ) )
                .source( this.translator.build( query ) );

        final SearchResponse res = this.client.search(req).actionGet();

//        LOG.info( "Search result: " + res.toString() );

        final SearchHits hits = res.getHits();

        final AccountSearchResults searchResult = new AccountSearchResults(query.getFrom(), (int)hits.getTotalHits());
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
                com.enonic.wem.core.search.Facet resultFacet = new com.enonic.wem.core.search.Facet( tf.name() );
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
            final String accountTypeValue = ( (String) hit.sourceAsMap().get( "type" ) ).toUpperCase();
            final AccountType accountType = AccountType.valueOf( accountTypeValue );
            searchResult.add( new AccountKey( key ), accountType, hit.score() );
        }
    }

    public void dropIndex()
    {
        final String[] indices = new String[]{CMS_INDEX};
        final DeleteMappingRequest deleteMappingRequest = new DeleteMappingRequest()
            .indices( indices )
            .type( ACCOUNT_INDEX_TYPE );

        try
        {
            this.client.admin().indices().deleteMapping( deleteMappingRequest ).get();
            final FlushRequest flushRequest = new FlushRequest();
            this.client.admin().indices().flush( flushRequest ).get();
        }
        catch ( Exception e )
        {
            LOG.warn( "Unable to delete ElasticSearch mapping", e );
        }

        try
        {
            final DeleteIndexRequest deleteRequest = new DeleteIndexRequest()
                .indices( CMS_INDEX );
            this.client.admin().indices().delete( deleteRequest ).get();
        }
        catch ( Exception e )
        {
            LOG.warn( "Unable to delete ElasticSearch index", e );
        }
    }

    public boolean indexExists() {
        final IndicesExistsRequest indicesExistRequest = new IndicesExistsRequest(CMS_INDEX);
        IndicesExistsResponse response = this.client.admin().indices().exists( indicesExistRequest ).actionGet();
        return response.exists();
    }

    public void deleteIndex(String id) {
        deleteIndex( id, false );
    }

    public void deleteIndex(String id, boolean flushDataAfterDelete) {
        final DeleteRequest deleteRequest = new DeleteRequest(  )
            .index( CMS_INDEX )
            .type( ACCOUNT_INDEX_TYPE )
            .id( id );
        this.client.delete( deleteRequest ).actionGet();

        if ( flushDataAfterDelete ) {
            flush();
        }
    }

    private void flush() {
        this.client.admin().indices().flush( new FlushRequest( CMS_INDEX ).refresh( true ) ).actionGet();
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
