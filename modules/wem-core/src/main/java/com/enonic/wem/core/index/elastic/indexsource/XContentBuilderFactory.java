package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument2;

public class XContentBuilderFactory
{
    private XContentBuilderFactory()
    {
    }

    public static XContentBuilder create( final IndexDocument2 indexDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addDocumentAnalyzer( builder, indexDocument );
            addCollection( builder, indexDocument );
            addFields( builder, indexDocument );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }
    }

    private static void addCollection( final XContentBuilder builder, final IndexDocument2 indexDocument )
        throws Exception
    {
        final String collection = indexDocument.getCollection();

        addField( builder, IndexConstants.COLLECTION_FIELD,
                  Strings.isNullOrEmpty( collection ) ? IndexConstants.DEFAULT_COLLECTION : collection );
    }

    @Deprecated
    public static XContentBuilder create( final IndexSource indexSource )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addFields( builder, indexSource );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource" );
        }
    }

    private static void addDocumentAnalyzer( final XContentBuilder builder, final IndexDocument2 indexDocument )
        throws Exception
    {
        final String analyzer = indexDocument.getAnalyzer();

        if ( !Strings.isNullOrEmpty( analyzer ) )
        {
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, analyzer );
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

    private static void addFields( final XContentBuilder result, final IndexDocument2 indexDocument2 )
        throws Exception
    {
        final Multimap<String, Object> fieldValueMap = ArrayListMultimap.create();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = indexDocument2.getIndexDocumentItems();

        for ( AbstractIndexDocumentItem item : indexDocumentItems )
        {
            final String fieldName = IndexFieldNameResolver.resolve( item );

            fieldValueMap.put( fieldName, item.getValue() );
        }

        for ( String field : fieldValueMap.keySet() )
        {
            addField( result, field, fieldValueMap.get( field ) );
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


    private static void addFields( final XContentBuilder result, final IndexSource indexSource )
        throws Exception
    {
        final Set<IndexSourceItem> indexSourceEntries = indexSource.indexSourceItems();

        for ( IndexSourceItem indexSourceItem : indexSourceEntries )
        {
            addField( result, indexSourceItem.getKey(), indexSourceItem.getValue() );
        }
    }
}
