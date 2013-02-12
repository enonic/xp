package com.enonic.wem.core.search.elastic;

import java.util.Collection;

import org.elasticsearch.action.delete.DeleteResponse;

import com.enonic.wem.core.search.DeleteDocument;
import com.enonic.wem.core.search.IndexStatus;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

public interface ElasticsearchIndexService
{
    public IndexStatus getIndexStatus( final String indexName, final boolean waitForStatusYellow );

    public boolean indexExists( String indexName );

    public void createIndex( String indexName );

    public void putMapping( IndexMapping indexMapping );

    public void index( Collection<IndexDocument> indexDocuments );

    public DeleteResponse delete( final DeleteDocument deleteDocument );
}
