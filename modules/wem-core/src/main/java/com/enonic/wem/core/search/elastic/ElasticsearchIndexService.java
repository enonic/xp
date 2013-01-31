package com.enonic.wem.core.search.elastic;

import com.enonic.wem.core.search.IndexData;
import com.enonic.wem.core.search.IndexStatus;

public interface ElasticsearchIndexService
{
    public IndexStatus getIndexStatus( final String indexName, final boolean waitForStatusYellow );

    public boolean indexExists( String indexName );

    public void createIndex( String indexName );

    public void putMapping( IndexMapping indexMapping );

    public void index( IndexData indexData );

}
