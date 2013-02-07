package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.IndexException;

@Component
public class XContentBuilderFactory
{
    public XContentBuilder create( final IndexSource indexSource )
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

    private XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }


    private void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    private void addFields( final XContentBuilder result, final IndexSource indexSource )
        throws Exception
    {
        final Set<IndexSourceEntry> indexSourceEntries = indexSource.getIndexSourceEntries();

        for ( IndexSourceEntry indexSourceEntry : indexSourceEntries )
        {
            addField( result, indexSourceEntry.getKey(), indexSourceEntry.getValue() );
        }
    }

    private void addField( XContentBuilder result, String name, Object value )
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
