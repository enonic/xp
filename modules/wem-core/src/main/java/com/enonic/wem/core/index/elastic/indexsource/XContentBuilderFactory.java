package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.wem.core.index.IndexException;

public class XContentBuilderFactory
{
    private XContentBuilderFactory()
    {
    }

    public static XContentBuilder create( final IndexSource indexSource )
    {
        try
        {
            final XContentBuilder result = startBuilder();
            addFields( result, indexSource );
            endBuilder( result );
            return result;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource" );
        }
    }

    private static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }


    private static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    private static void addFields( final XContentBuilder result, final IndexSource indexSource )
        throws Exception
    {
        final Set<IndexSourceItem> indexSourceEntries = indexSource.indexSourceItems();

        for ( IndexSourceItem indexSourceItem : indexSourceEntries )
        {
            addField( result, indexSourceItem.getKey(), indexSourceItem.getValue() );
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
            value = ( (String) value ).trim();
        }

        result.field( name, value );
    }
}
