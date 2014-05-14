package com.enonic.wem.core.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public abstract class AbstractXContentBuilderFactor
{

    protected static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }

    protected static void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }
        if ( value instanceof String )
        {
            value = ( (String) value ).trim();
        }

        result.field( name, value );
    }


    protected static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }



}
