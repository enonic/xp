package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.Collection;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public void execute( final Collection<IndexDocument> indexDocuments )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            final String id = indexDocument.getId();

            final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( indexDocument );

            final IndexRequest req = Requests.indexRequest().
                id( id ).
                index( indexDocument.getIndexName() ).
                type( indexDocument.getIndexTypeName() ).
                source( xContentBuilder ).
                refresh( indexDocument.isRefreshAfterOperation() );

            try
            {
                this.client.index( req ).actionGet( storeTimeout );
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
        private Builder( final Client client )
        {
            super( client );
        }


        public StoreExecutor build()
        {
            return new StoreExecutor( this );
        }
    }
}



