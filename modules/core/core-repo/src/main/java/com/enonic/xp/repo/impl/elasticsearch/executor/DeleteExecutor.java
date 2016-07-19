package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;

import com.enonic.xp.repo.impl.elasticsearch.document.DeleteDocument;

public class DeleteExecutor
    extends AbstractExecutor
{
    private DeleteExecutor( final Builder builder )
    {
        super( builder );
    }

    public boolean delete( final DeleteDocument deleteDocument )
    {
        DeleteRequest deleteRequest = new DeleteRequest( deleteDocument.getIndexName() ).
            type( deleteDocument.getIndexTypeName() ).
            id( deleteDocument.getId() ).
            refresh( false );

        return doDelete( deleteRequest );
    }

    private boolean doDelete( final DeleteRequest deleteRequest )
    {
        final DeleteResponse deleteResponse = this.client.delete( deleteRequest ).
            actionGet( deleteTimeout );

        return deleteResponse.isFound();
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }


    public static final class Builder
        extends AbstractExecutor.Builder<Builder>
    {

        private Builder( final Client client )
        {
            super( client );
        }


        public DeleteExecutor build()
        {
            return new DeleteExecutor( this );
        }
    }
}
