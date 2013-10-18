package com.enonic.wem.core.index.indexdocument;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.PropertyIndexConfig;

public class IndexDocumentItemFactory
{
    public static Set<AbstractIndexDocumentItem> create( final Property property, final PropertyIndexConfig propertyIndexConfig )
    {
        final Set<AbstractIndexDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !propertyIndexConfig.enabled() )
        {
            return indexDocumentItems;
        }

        final Value propertyValue = property.getValue();

        addIndexBaseTypeEntries( property, indexDocumentItems, propertyValue );

        if ( propertyIndexConfig.isFulltextEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( property.getName(), propertyValue.asString() ) );
        }

        if ( propertyIndexConfig.isTokenizeEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentTokenizedItem( property.getName(), propertyValue.asString() ) );
        }

        indexDocumentItems.add( createOrderbyItemType( property, propertyValue ) );

        return indexDocumentItems;
    }

    private static IndexDocumentOrderbyItem createOrderbyItemType( final Property property, final Value propertyValue )
    {
        return new IndexDocumentOrderbyItem( property.getName(), OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private static void addIndexBaseTypeEntries( final Property property, final Set<AbstractIndexDocumentItem> indexDocumentItems,
                                                 final Value propertyValue )
    {
        if ( propertyValue.isDateType() )
        {
            indexDocumentItems.add( createDateItemType( property, propertyValue ) );
        }

        if ( propertyValue.isNumericType() )
        {
            indexDocumentItems.add( createNumericItemType( property, propertyValue ) );
        }

        if ( propertyValue.isGeographicCoordinate() )
        {
            indexDocumentItems.add( createGeoPointItemType( property, propertyValue ) );
        }

        indexDocumentItems.add( createStringItemType( property, propertyValue ) );
    }

    private static IndexDocumentGeoPointItem createGeoPointItemType( final Property property, final Value propertyValue )
    {
        return new IndexDocumentGeoPointItem( property.getName(), (Value.GeographicCoordinate) propertyValue );
    }

    private static IndexDocumentStringItem createStringItemType( final Property property, final Value propertyValue )
    {
        return new IndexDocumentStringItem( property.getName(), propertyValue.asString() );
    }

    private static IndexDocumentNumberItem createNumericItemType( final Property property, final Value propertyValue )
    {
        return new IndexDocumentNumberItem( property.getName(), propertyValue.asDouble() );
    }

    private static IndexDocumentDateItem createDateItemType( final Property property, final Value propertyValue )
    {
        return new IndexDocumentDateItem( property.getName(), propertyValue.asDateTime() );
    }

    private static void addIfNotNull( final AbstractIndexDocumentItem item, final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        if ( item != null )
        {
            indexDocumentItems.add( item );
        }
    }

}
