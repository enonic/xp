package com.enonic.wem.core.index.elastic;

import java.util.Collection;

import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.document.IndexDocument2;
import com.enonic.wem.core.index.entity.EntityQueryResult;

public interface ElasticsearchIndexService
{
    public IndexStatus getIndexStatus( final Index index, final boolean waitForStatusYellow );

    public boolean indexExists( Index index );

    public void createIndex( Index index );

    public void putMapping( IndexMapping indexMapping );

    public void indexDocuments( Collection<IndexDocument2> indexDocuments );

    public void delete( final DeleteDocument deleteDocument );

    public EntityQueryResult search( final ElasticsearchQuery elasticsearchQuery );

    public void deleteIndex( final Index index );
}
