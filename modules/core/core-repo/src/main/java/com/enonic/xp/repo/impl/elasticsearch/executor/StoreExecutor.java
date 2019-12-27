package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.Collection;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.xp.repository.IndexException;

public class StoreExecutor
    extends AbstractExecutor
{
    private final static Logger LOG = LoggerFactory.getLogger( StoreExecutor.class );

    private StoreExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final EsClient client )
    {
        return new Builder( client );
    }

    public void execute( final Collection<IndexDocument> indexDocuments )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            final String id = indexDocument.getId();

            final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( indexDocument );

            final IndexRequest req = new IndexRequest().
                id( id ).
                index( indexDocument.getIndexName() ).
                source( xContentBuilder ).
                setRefreshPolicy(
                    indexDocument.isRefreshAfterOperation() ? WriteRequest.RefreshPolicy.IMMEDIATE : WriteRequest.RefreshPolicy.NONE ).
                timeout( storeTimeout );

            try
            {
                this.client.index( req );
            }
            catch ( Exception e )
            {
                final String msg = "Failed to store document with id [" + id + "] in index [" + indexDocument.getIndexName() + "] branch " +
                    indexDocument.getIndexTypeName();

                LOG.error( msg, e );

                throw new IndexException( msg, e );
            }
        }
    }

    public static final class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final EsClient client )
        {
            super( client );
        }


        public StoreExecutor build()
        {
            return new StoreExecutor( this );
        }
    }
}



