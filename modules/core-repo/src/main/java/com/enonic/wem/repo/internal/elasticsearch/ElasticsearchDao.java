package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;

import com.enonic.wem.repo.internal.elasticsearch.document.DeleteDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;

public interface ElasticsearchDao
{
    public String store( IndexRequest indexRequest );

    public void store( Collection<StoreDocument> storeDocuments );

    public boolean delete( DeleteRequest deleteRequest );

    public boolean delete( DeleteDocument deleteDocument );

    public SearchResult find( ElasticsearchQuery query );

    public GetResult get( GetQuery getQuery );

    public long count( ElasticsearchQuery query );

    public void snapshot( String name );

    public void restore( String repositoryName, String snapshotName );
}
