package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.index.IndexValueNormalizer;

abstract class AbstractXContentBuilderFactory
{

    static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = EsClient.jsonBuilder();
        result.startObject();

        return result;
    }

    static void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof String )
        {
            value = IndexValueNormalizer.normalize( (String) value );
            result.field( name, value );
        }

        if ( value instanceof Iterable )
        {
            result.field( name, (Iterable<?>) value );
        }
    }


    static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
