package com.enonic.xp.repo.impl.elasticsearch.storage;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.xp.repo.impl.index.IndexValueNormalizer;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.IndexException;

class XContentBuilderFactory
{
    static XContentBuilder create( final StoreRequest doc )
    {
        try
        {
            final XContentBuilder builder = XContentFactory.jsonBuilder().startObject();

            for ( final var e : doc.getEntries().entrySet() )
            {
                addField( builder, e.getKey(), e.getValue() );
            }

            builder.endObject();
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for StorageDocument", e );
        }
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


}
