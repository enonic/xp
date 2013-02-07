package com.enonic.wem.core.search;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Entry;

abstract class AbstractIndexDataFactory
{

    public void addField( XContentBuilder result, String name, Object value )
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

    protected String getEntryPath( final Entry entry )
    {
        return entry.getPath().toString();
    }

    protected void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    protected XContentBuilder startBuilder( )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }
}
