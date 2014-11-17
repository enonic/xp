package com.enonic.wem.repo.internal.elasticsearch.xcontent;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.wem.repo.internal.elasticsearch.FieldNameResolver;
import com.enonic.wem.repo.internal.elasticsearch.IndexConstants;
import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.index.IndexValueNormalizer;

public class StoreDocumentXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{

    private StoreDocumentXContentBuilderFactory()
    {
    }

    public static XContentBuilder create( final StoreDocument storeDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addDocumentAnalyzer( builder, storeDocument );
            addIndexDocumentItems( builder, storeDocument );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for indexSource", e );
        }
    }


    private static void addDocumentAnalyzer( final XContentBuilder builder, final StoreDocument storeDocument )
        throws Exception
    {
        final String analyzer = storeDocument.getAnalyzer();

        if ( !Strings.isNullOrEmpty( analyzer ) )
        {
            addField( builder, IndexConstants.ANALYZER_VALUE_FIELD, analyzer );
        }
    }

    private static void addIndexDocumentItems( final XContentBuilder result, final StoreDocument storeDocument )
        throws Exception
    {
        final Multimap<String, Object> fieldMultiValueMap = ArrayListMultimap.create();

        // OrderBy should only have one value, add to own one-value-map
        final Map<String, Object> orderByMap = Maps.newHashMap();

        final Set<AbstractStoreDocumentItem> indexDocumentItems = storeDocument.getStoreDocumentItems();

        for ( AbstractStoreDocumentItem item : indexDocumentItems )
        {
            final String fieldName = FieldNameResolver.resolve( item );

            if ( item instanceof StoreDocumentOrderbyItem )
            {
                orderByMap.put( fieldName, normalizeValueIfString( item ) );
            }
            else
            {
                fieldMultiValueMap.put( fieldName, normalizeValueIfString( item ) );
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

    private static Object normalizeValueIfString( final AbstractStoreDocumentItem item )
    {
        final Object value = item.getValue();

        if ( value instanceof String )
        {
            return IndexValueNormalizer.normalize( (String) value );
        }
        else
        {
            return value;
        }
    }

}
