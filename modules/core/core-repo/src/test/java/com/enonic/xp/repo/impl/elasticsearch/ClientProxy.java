package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequestBuilder;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
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
    public BulkRequestBuilder prepareBulk( final String globalIndex, final String globalType )
    {
        return null;
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
    public FieldCapabilitiesRequestBuilder prepareFieldCaps( final String... indices )
    {
        return null;
    }

    @Override
    public ActionFuture<FieldCapabilitiesResponse> fieldCaps( final FieldCapabilitiesRequest request )
    {
        return null;
    }

    @Override
    public void fieldCaps( final FieldCapabilitiesRequest request, final ActionListener<FieldCapabilitiesResponse> listener )
    {
        client.fieldCaps( request, listener );
    }

    @Override
    public Settings settings()
    {
        return this.client.settings();
    }

    @Override
    public Client filterWithHeader( final Map<String, String> headers )
    {
        return null;
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
    public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
        final ActionType<Response> action, final Request request )
    {
        return null;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void execute( final ActionType<Response> action,
                                                                                          final Request request,
                                                                                          final ActionListener<Response> listener )
    {

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
