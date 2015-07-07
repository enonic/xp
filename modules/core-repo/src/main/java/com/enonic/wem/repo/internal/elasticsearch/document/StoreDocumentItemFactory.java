package com.enonic.wem.repo.internal.elasticsearch.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.elasticsearch.OrderbyValueResolver;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.node.NodeIndexPath;

public class StoreDocumentItemFactory
{

    public static Set<AbstractStoreDocumentItem> create( final IndexPath path, final Value value, final IndexConfig indexConfig )
    {
        return doCreate( indexConfig, path, value );
    }

    public static Set<AbstractStoreDocumentItem> create( final Property property, final IndexConfig indexConfig )
    {
        final IndexPath path = IndexPath.from( property );
        final Value propertyValue = property.getValue();

        return doCreate( indexConfig, path, propertyValue );
    }

    private static Set<AbstractStoreDocumentItem> doCreate( final IndexConfig indexConfig, final IndexPath path, final Value propertyValue )
    {
        final Set<AbstractStoreDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !indexConfig.isEnabled() )
        {
            return indexDocumentItems;
        }

        Value processedPropertyValue = propertyValue;
        for ( IndexValueProcessor indexValueProcessor : indexConfig.getIndexValueProcessors() )
        {
            processedPropertyValue = indexValueProcessor.process( processedPropertyValue );
        }

        addIndexBaseTypeEntries( path, indexDocumentItems, processedPropertyValue );

        addFulltextFields( indexConfig, path, processedPropertyValue, indexDocumentItems );

        indexDocumentItems.add( createOrderbyItemType( path, processedPropertyValue ) );

        addAllFields( processedPropertyValue, indexDocumentItems, indexConfig );

        return indexDocumentItems;
    }

    private static void addAllFields( final Value propertyValue, final Set<AbstractStoreDocumentItem> indexDocumentItems,
                                      final IndexConfig indexConfig )
    {
        if ( indexConfig.isDecideByType() || indexConfig.isIncludeInAllText() )
        {
            indexDocumentItems.add( new StoreDocumentAnalyzedItem( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );

            indexDocumentItems.add( new StoreDocumentNGramItem( NodeIndexPath.ALL_TEXT, propertyValue.asString() ) );
        }
    }

    private static void addFulltextFields( final IndexConfig indexConfig, final IndexPath path, final Value propertyValue,
                                           final Set<AbstractStoreDocumentItem> indexDocumentItems )
    {
        if ( indexConfig.isDecideByType() )
        {
            addFulltextFieldsTypeBased( path, propertyValue, indexDocumentItems );
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                indexDocumentItems.add( new StoreDocumentAnalyzedItem( path, propertyValue.asString() ) );
            }

            if ( indexConfig.isnGram() )
            {
                indexDocumentItems.add( new StoreDocumentNGramItem( path, propertyValue.asString() ) );
            }
        }
    }

    private static void addFulltextFieldsTypeBased( final IndexPath path, final Value propertyValue,
                                                    final Set<AbstractStoreDocumentItem> indexDocumentItems )
    {

        if ( propertyValue.isText() )
        {
            indexDocumentItems.add( new StoreDocumentAnalyzedItem( path, propertyValue.asString() ) );
            indexDocumentItems.add( new StoreDocumentNGramItem( path, propertyValue.asString() ) );
        }
    }

    private static StoreDocumentOrderbyItem createOrderbyItemType( final IndexPath path, final Value propertyValue )
    {
        return new StoreDocumentOrderbyItem( path, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private static void addIndexBaseTypeEntries( final IndexPath path, final Set<AbstractStoreDocumentItem> indexDocumentItems,
                                                 final Value propertyValue )
    {
        if ( propertyValue.isDateType() )
        {
            indexDocumentItems.add( createInstantType( path, propertyValue ) );
        }
        if ( propertyValue.isNumericType() )
        {
            indexDocumentItems.add( createNumericItemType( path, propertyValue ) );
        }

        if ( propertyValue.isGeoPoint() )
        {
            indexDocumentItems.add( createGeoPointItemType( path, propertyValue ) );
        }

        indexDocumentItems.add( createStringItemType( path, propertyValue ) );
    }

    private static StoreDocumentGeoPointItem createGeoPointItemType( final IndexPath path, final Value propertyValue )
    {
        return new StoreDocumentGeoPointItem( path, propertyValue.asString() );
    }

    private static StoreDocumentStringItem createStringItemType( final IndexPath path, final Value propertyValue )
    {
        return new StoreDocumentStringItem( path, propertyValue.asString() );
    }

    private static StoreDocumentNumberItem createNumericItemType( final IndexPath path, final Value propertyValue )
    {
        return new StoreDocumentNumberItem( path, propertyValue.asDouble() );
    }

//    private IndexDocumentDateItem createDateItemType( final IndexDocumentItemPath path, final Value propertyValue )
//    {
//        return new IndexDocumentDateItem( path, propertyValue.asDateTime() );
//    }

    private static StoreDocumentDateItem createInstantType( final IndexPath path, final Value propertyValue )
    {
        return new StoreDocumentDateItem( path, propertyValue.asInstant() );
    }

}
