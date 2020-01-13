package com.enonic.xp.elasticsearch.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

public abstract class EsClientResponseResolver<T>
{

    public T fromResponse( final Response response )
    {
        final HttpEntity entity = response.getEntity();
        if ( entity == null )
        {
            throw new IllegalStateException( "Response body expected but not returned" );
        }

        if ( entity.getContentType() == null )
        {
            throw new IllegalStateException( "Elasticsearch didn't return the [Content-Type] header, unable to parse response body" );
        }

        final XContentType xContentType = XContentType.fromMediaTypeOrFormat( entity.getContentType().getValue() );
        if ( xContentType == null )
        {
            throw new IllegalStateException( "Unsupported Content-Type: " + entity.getContentType().getValue() );
        }

        try (final InputStream stream = response.getEntity().getContent();

             final XContentParser parser = XContentFactory.xContent( xContentType ).
                 createParser( NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, stream ))
        {
            return doFromXContent( parser );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public T fromXContent( final XContentParser parser )
    {
        try
        {
            return doFromXContent( parser );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public abstract T doFromXContent( final XContentParser parser )
        throws IOException;

}
