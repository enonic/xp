package com.enonic.wem.core.index.elastic;

import java.util.Collection;

import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.indexdocument.IndexDocument;

public interface ElasticsearchIndexService
{
    public IndexStatus getIndexStatus( final String indexName, final boolean waitForStatusYellow );

    public boolean indexExists( String indexName );

    public void createIndex( String indexName );

    public void putMapping( IndexMapping indexMapping );

    public void index( Collection<IndexDocument> indexDocuments );

    public void delete( final DeleteDocument deleteDocument );


}
