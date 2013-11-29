package com.enonic.wem.core.index.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class IndexDocumentItemFactory
{

    public static Set<AbstractIndexDocumentItem> create( final IndexDocumentItemPath path, final Value value,
                                                         final PropertyIndexConfig propertyIndexConfig )
    {
        return doCreate( propertyIndexConfig, path, value );
    }

    public static Set<AbstractIndexDocumentItem> create( final Property property, final PropertyIndexConfig propertyIndexConfig )
    {
        final IndexDocumentItemPath path = IndexDocumentItemPath.from( property );
        final Value propertyValue = property.getValue();

        return doCreate( propertyIndexConfig, path, propertyValue );
    }

    private static Set<AbstractIndexDocumentItem> doCreate( final PropertyIndexConfig propertyIndexConfig, final IndexDocumentItemPath path,
                                                            final Value propertyValue )
    {
        final Set<AbstractIndexDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !propertyIndexConfig.enabled() )
        {
            return indexDocumentItems;
        }

        addIndexBaseTypeEntries( path, indexDocumentItems, propertyValue );

        if ( propertyIndexConfig.fulltextEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( path, propertyValue.asString() ) );
        }

        if ( propertyIndexConfig.tokenizeEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentTokenizedItem( path, propertyValue.asString() ) );
        }

        indexDocumentItems.add( createOrderbyItemType( path, propertyValue ) );

        return indexDocumentItems;
    }

    private static IndexDocumentOrderbyItem createOrderbyItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentOrderbyItem( path, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private static void addIndexBaseTypeEntries( final IndexDocumentItemPath path, final Set<AbstractIndexDocumentItem> indexDocumentItems,
                                                 final Value propertyValue )
    {
        if ( propertyValue.isDateType() )
        {
            indexDocumentItems.add( createDateItemType( path, propertyValue ) );
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

    private static IndexDocumentGeoPointItem createGeoPointItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentGeoPointItem( path, (Value.GeoPoint) propertyValue );
    }

    private static IndexDocumentStringItem createStringItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentStringItem( path, propertyValue.asString() );
    }

    private static IndexDocumentNumberItem createNumericItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentNumberItem( path, propertyValue.asDouble() );
    }

    private static IndexDocumentDateItem createDateItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentDateItem( path, propertyValue.asDateTime() );
    }

    private static void addIfNotNull( final AbstractIndexDocumentItem item, final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        if ( item != null )
        {
            indexDocumentItems.add( item );
        }
    }

}
