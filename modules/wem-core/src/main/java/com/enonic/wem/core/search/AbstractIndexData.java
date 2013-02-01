package com.enonic.wem.core.search;

import org.elasticsearch.common.xcontent.XContentBuilder;

public abstract class AbstractIndexData
    extends IndexConstants
{

    protected void addField( XContentBuilder result, String name, Object value )
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
}
