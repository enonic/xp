package com.enonic.wem.core.index.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class IndexDocumentItemFactory
{

    public static Set<AbstractIndexDocumentItem> create( final String propertyName, final Value propertyValue,
                                                         PropertyIndexConfig propertyIndexConfig )
    {
        return doCreate( propertyIndexConfig, propertyName, propertyValue );
    }


    public static Set<AbstractIndexDocumentItem> create( final Property property, final PropertyIndexConfig propertyIndexConfig )
    {
        final String propertyName = property.getName();
        final Value propertyValue = property.getValue();

        return doCreate( propertyIndexConfig, propertyName, propertyValue );
    }

    private static Set<AbstractIndexDocumentItem> doCreate( final PropertyIndexConfig propertyIndexConfig, final String propertyName,
                                                            final Value propertyValue )
    {
        final Set<AbstractIndexDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !propertyIndexConfig.enabled() )
        {
            return indexDocumentItems;
        }

        addIndexBaseTypeEntries( propertyName, indexDocumentItems, propertyValue );

        if ( propertyIndexConfig.isFulltextEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( propertyName, propertyValue.asString() ) );
        }

        if ( propertyIndexConfig.isTokenizeEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentTokenizedItem( propertyName, propertyValue.asString() ) );
        }

        indexDocumentItems.add( createOrderbyItemType( propertyName, propertyValue ) );

        return indexDocumentItems;
    }

    private static IndexDocumentOrderbyItem createOrderbyItemType( final String propertyName, final Value propertyValue )
    {
        return new IndexDocumentOrderbyItem( propertyName, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private static void addIndexBaseTypeEntries( final String propertyName, final Set<AbstractIndexDocumentItem> indexDocumentItems,
                                                 final Value propertyValue )
    {
        if ( propertyValue.isDateType() )
        {
            indexDocumentItems.add( createDateItemType( propertyName, propertyValue ) );
        }

        if ( propertyValue.isNumericType() )
        {
            indexDocumentItems.add( createNumericItemType( propertyName, propertyValue ) );
        }

        if ( propertyValue.isGeographicCoordinate() )
        {
            indexDocumentItems.add( createGeoPointItemType( propertyName, propertyValue ) );
        }

        indexDocumentItems.add( createStringItemType( propertyName, propertyValue ) );
    }

    private static IndexDocumentGeoPointItem createGeoPointItemType( final String propertyName, final Value propertyValue )
    {
        return new IndexDocumentGeoPointItem( propertyName, (Value.GeographicCoordinate) propertyValue );
    }

    private static IndexDocumentStringItem createStringItemType( final String propertyName, final Value propertyValue )
    {
        return new IndexDocumentStringItem( propertyName, propertyValue.asString() );
    }

    private static IndexDocumentNumberItem createNumericItemType( final String propertyName, final Value propertyValue )
    {
        return new IndexDocumentNumberItem( propertyName, propertyValue.asDouble() );
    }

    private static IndexDocumentDateItem createDateItemType( final String propertyName, final Value propertyValue )
    {
        return new IndexDocumentDateItem( propertyName, propertyValue.asDateTime() );
    }

    private static void addIfNotNull( final AbstractIndexDocumentItem item, final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        if ( item != null )
        {
            indexDocumentItems.add( item );
        }
    }

}
