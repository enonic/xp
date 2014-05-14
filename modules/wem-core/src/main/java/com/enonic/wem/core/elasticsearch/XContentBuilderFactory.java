package com.enonic.wem.core.elasticsearch;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentOrderbyItem;
import com.enonic.wem.core.workspace.WorkspaceDocument;

public class XContentBuilderFactory
{

    private XContentBuilderFactory()
    {
    }


    static XContentBuilder create( final NodeStorageDocument storageDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            for ( final NodeStorageDocumentEntry entry : storageDocument.getEntries() )
            {
                builder.field( entry.getFieldName(), entry.getValue() );
            }
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }

    }


    static XContentBuilder create( final IndexDocument indexDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addDocumentAnalyzer( builder, indexDocument );
            addCollectionInfo( builder, indexDocument );
            adddIndexDocumentItems( builder, indexDocument );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }
    }

    private static XContentBuilder startBuilder()
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();

        return result;
    }

    private static void addDocumentAnalyzer( final XContentBuilder builder, final IndexDocument indexDocument )
        throws Exception
    {
        final String analyzer = indexDocument.getAnalyzer();

        if ( !Strings.isNullOrEmpty( analyzer ) )
        {
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, analyzer );
        }
    }

    private static void addCollectionInfo( final XContentBuilder builder, final IndexDocument indexDocument )
        throws Exception
    {
        final String collection = indexDocument.getCollection();

        addField( builder, IndexConstants.COLLECTION_FIELD,
                  Strings.isNullOrEmpty( collection ) ? IndexConstants.DEFAULT_COLLECTION : collection );
    }

    private static void adddIndexDocumentItems( final XContentBuilder result, final IndexDocument indexDocument )
        throws Exception
    {
        final Multimap<String, Object> fieldMultiValueMap = ArrayListMultimap.create();

        // OrderBy should only have one value, add to own one-value-map
        final Map<String, Object> orderByMap = Maps.newHashMap();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = indexDocument.getIndexDocumentItems();

        for ( AbstractIndexDocumentItem item : indexDocumentItems )
        {
            final String fieldName = FieldNameResolver.resolve( item );

            if ( item instanceof IndexDocumentOrderbyItem )
            {
                orderByMap.put( fieldName, item.getValue() );
            }
            else
            {
                fieldMultiValueMap.put( fieldName, item.getValue() );
            }
        }

        for ( String field : fieldMultiValueMap.keySet() )
        {
            addField( result, field, fieldMultiValueMap.get( field ) );
        }

        for ( String field : orderByMap.keySet() )
        {
            addField( result, field, orderByMap.get( field ) );
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


    private static void endBuilder( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }


}
