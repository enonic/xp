package com.enonic.wem.core.elasticsearch;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.document.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentOrderbyItem;

public class IndexDocumentXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{

    private IndexDocumentXContentBuilderFactory()
    {
    }

    static XContentBuilder create( final IndexDocument indexDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addDocumentAnalyzer( builder, indexDocument );
            adddIndexDocumentItems( builder, indexDocument );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }
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


}
