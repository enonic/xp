package com.enonic.xp.repo.impl.elasticsearch.storage;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Multimap;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.IndexException;

class XContentBuilderFactory
{
    static XContentBuilder create( final StoreRequest doc )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            final Multimap<String, Object> values = doc.getEntries();

            for ( final String key : values.keySet() )
            {
                addField( builder, key, values.get( key ) );
            }

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for StorageDocument", e );
        }
    }

    private static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = EsClient.jsonBuilder();
        result.startObject();

        return result;
    }

    private static void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String )
        {
            value = IndexValueNormalizer.normalize( (String) value );
        }

        result.field( name, value );
    }


    private static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
