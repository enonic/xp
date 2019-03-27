package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.exists.ExistsRequest;
import org.elasticsearch.action.exists.ExistsRequestBuilder;
import org.elasticsearch.action.exists.ExistsResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldstats.FieldStatsRequest;
import org.elasticsearch.action.fieldstats.FieldStatsRequestBuilder;
import org.elasticsearch.action.fieldstats.FieldStatsResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptResponse;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse;
import org.elasticsearch.action.percolate.MultiPercolateRequest;
import org.elasticsearch.action.percolate.MultiPercolateRequestBuilder;
import org.elasticsearch.action.percolate.MultiPercolateResponse;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateRequestBuilder;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequest;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.support.Headers;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;

public class ClientProxy
    implements Client
{
    protected final Client client;

    public ClientProxy( final Client client )
    {
        this.client = client;
    }

    @Override
    public AdminClient admin()
    {
        return this.client.admin();
    }

    @Override
    public ActionFuture<IndexResponse> index( final IndexRequest request )
    {
        return this.client.index( request );
    }

    @Override
    public void index( final IndexRequest request, final ActionListener<IndexResponse> listener )
    {
        this.client.index( request, listener );
    }

    @Override
    public IndexRequestBuilder prepareIndex()
    {
        return this.client.prepareIndex();
    }

    @Override
    public ActionFuture<UpdateResponse> update( final UpdateRequest request )
    {
        return this.client.update( request );
    }

    @Override
    public void update( final UpdateRequest request, final ActionListener<UpdateResponse> listener )
    {
        this.client.update( request, listener );
    }

    @Override
    public UpdateRequestBuilder prepareUpdate()
    {
        return this.client.prepareUpdate();
    }

    @Override
    public UpdateRequestBuilder prepareUpdate( final String index, final String type, final String id )
    {
        return this.client.prepareUpdate( index, type, id );
    }

    @Override
    public IndexRequestBuilder prepareIndex( final String index, final String type )
    {
        return this.client.prepareIndex( index, type );
    }

    @Override
    public IndexRequestBuilder prepareIndex( final String index, final String type, @Nullable final String id )
    {
        return this.client.prepareIndex( index, type, id );
    }

    @Override
    public ActionFuture<DeleteResponse> delete( final DeleteRequest request )
    {
        return this.client.delete( request );
    }

    @Override
    public void delete( final DeleteRequest request, final ActionListener<DeleteResponse> listener )
    {
        this.client.delete( request, listener );
    }

    @Override
    public DeleteRequestBuilder prepareDelete()
    {
        return this.client.prepareDelete();
    }

    @Override
    public DeleteRequestBuilder prepareDelete( final String index, final String type, final String id )
    {
        return this.client.prepareDelete( index, type, id );
    }

    @Override
    public ActionFuture<BulkResponse> bulk( final BulkRequest request )
    {
        return this.client.bulk( request );
    }

    @Override
    public void bulk( final BulkRequest request, final ActionListener<BulkResponse> listener )
    {
        this.client.bulk( request, listener );
    }

    @Override
    public BulkRequestBuilder prepareBulk()
    {
        return this.client.prepareBulk();
    }

    @Override
    public ActionFuture<GetResponse> get( final GetRequest request )
    {
        return this.client.get( request );
    }

    @Override
    public void get( final GetRequest request, final ActionListener<GetResponse> listener )
    {
        this.client.get( request, listener );
    }

    @Override
    public GetRequestBuilder prepareGet()
    {
        return this.client.prepareGet();
    }

    @Override
    public GetRequestBuilder prepareGet( final String index, @Nullable final String type, final String id )
    {
        return this.client.prepareGet( index, type, id );
    }

    @Override
    public PutIndexedScriptRequestBuilder preparePutIndexedScript()
    {
        return this.client.preparePutIndexedScript();
    }

    @Override
    public PutIndexedScriptRequestBuilder preparePutIndexedScript( @Nullable final String scriptLang, final String id, final String source )
    {
        return this.client.preparePutIndexedScript( scriptLang, id, source );
    }

    @Override
    public void deleteIndexedScript( final DeleteIndexedScriptRequest request, final ActionListener<DeleteIndexedScriptResponse> listener )
    {
        this.client.deleteIndexedScript( request, listener );
    }

    @Override
    public ActionFuture<DeleteIndexedScriptResponse> deleteIndexedScript( final DeleteIndexedScriptRequest request )
    {
        return this.client.deleteIndexedScript( request );
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript()
    {
        return this.client.prepareDeleteIndexedScript();
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript( @Nullable final String scriptLang, final String id )
    {
        return this.client.prepareDeleteIndexedScript( scriptLang, id );
    }

    @Override
    public void putIndexedScript( final PutIndexedScriptRequest request, final ActionListener<PutIndexedScriptResponse> listener )
    {
        this.client.putIndexedScript( request, listener );
    }

    @Override
    public ActionFuture<PutIndexedScriptResponse> putIndexedScript( final PutIndexedScriptRequest request )
    {
        return this.client.putIndexedScript( request );
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript()
    {
        return this.client.prepareGetIndexedScript();
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript( @Nullable final String scriptLang, final String id )
    {
        return this.client.prepareGetIndexedScript( scriptLang, id );
    }

    @Override
    public void getIndexedScript( final GetIndexedScriptRequest request, final ActionListener<GetIndexedScriptResponse> listener )
    {
        this.client.getIndexedScript( request, listener );
    }

    @Override
    public ActionFuture<GetIndexedScriptResponse> getIndexedScript( final GetIndexedScriptRequest request )
    {
        return this.client.getIndexedScript( request );
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet( final MultiGetRequest request )
    {
        return this.client.multiGet( request );
    }

    @Override
    public void multiGet( final MultiGetRequest request, final ActionListener<MultiGetResponse> listener )
    {
        this.client.multiGet( request, listener );
    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet()
    {
        return this.client.prepareMultiGet();
    }

    @Override
    public ActionFuture<CountResponse> count( final CountRequest request )
    {
        return this.client.count( request );
    }

    @Override
    public void count( final CountRequest request, final ActionListener<CountResponse> listener )
    {
        this.client.count( request, listener );
    }

    @Override
    public CountRequestBuilder prepareCount( final String... indices )
    {
        return this.client.prepareCount( indices );
    }

    @Override
    public ActionFuture<ExistsResponse> exists( final ExistsRequest request )
    {
        return this.client.exists( request );
    }

    @Override
    public void exists( final ExistsRequest request, final ActionListener<ExistsResponse> listener )
    {
        this.client.exists( request, listener );
    }

    @Override
    public ExistsRequestBuilder prepareExists( final String... indices )
    {
        return this.client.prepareExists( indices );
    }

    @Override
    public ActionFuture<SuggestResponse> suggest( final SuggestRequest request )
    {
        return this.client.suggest( request );
    }

    @Override
    public void suggest( final SuggestRequest request, final ActionListener<SuggestResponse> listener )
    {
        this.client.suggest( request, listener );
    }

    @Override
    public SuggestRequestBuilder prepareSuggest( final String... indices )
    {
        return this.client.prepareSuggest( indices );
    }

    @Override
    public ActionFuture<SearchResponse> search( final SearchRequest request )
    {
        return this.client.search( request );
    }

    @Override
    public void search( final SearchRequest request, final ActionListener<SearchResponse> listener )
    {
        this.client.search( request, listener );
    }

    @Override
    public SearchRequestBuilder prepareSearch( final String... indices )
    {
        return this.client.prepareSearch( indices );
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll( final SearchScrollRequest request )
    {
        return this.client.searchScroll( request );
    }

    @Override
    public void searchScroll( final SearchScrollRequest request, final ActionListener<SearchResponse> listener )
    {
        this.client.searchScroll( request, listener );
    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll( final String scrollId )
    {
        return this.client.prepareSearchScroll( scrollId );
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch( final MultiSearchRequest request )
    {
        return this.client.multiSearch( request );
    }

    @Override
    public void multiSearch( final MultiSearchRequest request, final ActionListener<MultiSearchResponse> listener )
    {
        this.client.multiSearch( request, listener );
    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch()
    {
        return this.client.prepareMultiSearch();
    }

    @Override
    public ActionFuture<TermVectorsResponse> termVector( final TermVectorsRequest request )
    {
        return this.client.termVector( request );
    }

    @Override
    public void termVector( final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener )
    {
        this.client.termVector( request, listener );
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVector()
    {
        return this.client.prepareTermVector();
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVector( final String index, final String type, final String id )
    {
        return this.client.prepareTermVector( index, type, id );
    }

    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors( final MultiTermVectorsRequest request )
    {
        return this.client.multiTermVectors( request );
    }

    @Override
    public void multiTermVectors( final MultiTermVectorsRequest request, final ActionListener<MultiTermVectorsResponse> listener )
    {
        this.client.multiTermVectors( request, listener );
    }

    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors()
    {
        return this.client.prepareMultiTermVectors();
    }

    @Override
    public ActionFuture<PercolateResponse> percolate( final PercolateRequest request )
    {
        return this.client.percolate( request );
    }

    @Override
    public void percolate( final PercolateRequest request, final ActionListener<PercolateResponse> listener )
    {
        this.client.percolate( request, listener );
    }

    @Override
    public PercolateRequestBuilder preparePercolate()
    {
        return this.client.preparePercolate();
    }

    @Override
    public ActionFuture<MultiPercolateResponse> multiPercolate( final MultiPercolateRequest request )
    {
        return this.client.multiPercolate( request );
    }

    @Override
    public void multiPercolate( final MultiPercolateRequest request, final ActionListener<MultiPercolateResponse> listener )
    {
        this.client.multiPercolate( request, listener );
    }

    @Override
    public MultiPercolateRequestBuilder prepareMultiPercolate()
    {
        return this.client.prepareMultiPercolate();
    }

    @Override
    public ExplainRequestBuilder prepareExplain( final String index, final String type, final String id )
    {
        return this.client.prepareExplain( index, type, id );
    }

    @Override
    public ActionFuture<ExplainResponse> explain( final ExplainRequest request )
    {
        return this.client.explain( request );
    }

    @Override
    public void explain( final ExplainRequest request, final ActionListener<ExplainResponse> listener )
    {
        this.client.explain( request, listener );
    }

    @Override
    public ClearScrollRequestBuilder prepareClearScroll()
    {
        return this.client.prepareClearScroll();
    }

    @Override
    public ActionFuture<ClearScrollResponse> clearScroll( final ClearScrollRequest request )
    {
        return this.client.clearScroll( request );
    }

    @Override
    public void clearScroll( final ClearScrollRequest request, final ActionListener<ClearScrollResponse> listener )
    {
        this.client.clearScroll( request, listener );
    }

    @Override
    public Settings settings()
    {
        return this.client.settings();
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
        final Action<Request, Response, RequestBuilder> action, final Request request )
    {
        return this.client.execute( action, request );
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
        final Action<Request, Response, RequestBuilder> action, final Request request, final ActionListener<Response> listener )
    {
        this.client.execute( action, request, listener );
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
        final Action<Request, Response, RequestBuilder> action )
    {
        return this.client.prepareExecute( action );
    }

    @Override
    public ActionFuture<TermVectorsResponse> termVectors( final TermVectorsRequest request )
    {
        return this.client.termVectors( request );
    }

    @Override
    public void termVectors( final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener )
    {
        this.client.termVectors( request, listener );
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors()
    {
        return this.client.prepareTermVectors();
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors( final String index, final String type, final String id )
    {
        return this.client.prepareTermVectors( index, type, id );
    }

    @Override
    public FieldStatsRequestBuilder prepareFieldStats()
    {
        return this.client.prepareFieldStats();
    }

    @Override
    public ActionFuture<FieldStatsResponse> fieldStats( final FieldStatsRequest request )
    {
        return this.client.fieldStats( request );
    }

    @Override
    public void fieldStats( final FieldStatsRequest request, final ActionListener<FieldStatsResponse> listener )
    {
        this.client.fieldStats( request, listener );
    }

    @Override
    public Headers headers()
    {
        return this.client.headers();
    }

    @Override
    public ThreadPool threadPool()
    {
        return this.client.threadPool();
    }

    @Override
    public void close()
        throws ElasticsearchException
    {
        this.client.close();
    }
}
