package com.enonic.wem.core.index.elastic;

import java.util.Collection;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;

import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.document.IndexDocument;

public interface ElasticsearchIndexService
{
    public IndexStatus getIndexStatus( final Index index, final boolean waitForStatusYellow );

    public boolean indexExists( Index index );

    public void createIndex( Index index );

    public void putMapping( IndexMapping indexMapping );

    public void indexDocuments( Collection<IndexDocument> indexDocuments );

    public void delete( final DeleteDocument deleteDocument );

    public SearchResponse search( final ElasticsearchQuery elasticsearchQuery );

    public void deleteIndex( final Index index );

    public SearchResponse get( final ByIdsQuery indexDocumentIds );

    public GetResponse get( final ByIdQuery byIdQuery );

    public SearchResponse get( final ByPathQuery byPathQuery );

    public SearchResponse get( final ByPathsQuery byPathsQuery );

    public SearchResponse get( final ByParentPathQuery byParentPathQuery );
}
