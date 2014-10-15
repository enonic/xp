package com.enonic.wem.core.elasticsearch.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.core.elasticsearch.IndexConstants;
import com.enonic.wem.core.entity.index.IndexDocumentItemPath;

public class IndexDocumentItemFactory
{

    public static Set<AbstractStoreDocumentItem> create( final IndexDocumentItemPath path, final Value value,
                                                         final IndexConfig indexConfig )
    {
        return doCreate( indexConfig, path, value );
    }

    public static Set<AbstractStoreDocumentItem> create( final Property property, final IndexConfig indexConfig )
    {
        final IndexDocumentItemPath path = IndexDocumentItemPath.from( property );
        final Value propertyValue = property.getValue();

        return doCreate( indexConfig, path, propertyValue );
    }

    private static Set<AbstractStoreDocumentItem> doCreate( final IndexConfig indexConfig, final IndexDocumentItemPath path,
                                                            final Value propertyValue )
    {
        final Set<AbstractStoreDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !indexConfig.isEnabled() )
        {
            return indexDocumentItems;
        }

        addIndexBaseTypeEntries( path, indexDocumentItems, propertyValue );

        addFulltextFields( indexConfig, path, propertyValue, indexDocumentItems );

        indexDocumentItems.add( createOrderbyItemType( path, propertyValue ) );

        addAllFields( propertyValue, indexDocumentItems, indexConfig );

        return indexDocumentItems;
    }

    private static void addAllFields( final Value propertyValue, final Set<AbstractStoreDocumentItem> indexDocumentItems,
                                      final IndexConfig indexConfig )
    {
        if ( indexConfig.isDecideByType() || indexConfig.isIncludeInAllText() )
        {
            indexDocumentItems.add( new StoreDocumentAnalyzedItem( IndexDocumentItemPath.from( IndexConstants.ALL_TEXT_FIELD_NAME ),
                                                                   propertyValue.asString() ) );

            indexDocumentItems.add(
                new StoreDocumentNGramItem( IndexDocumentItemPath.from( IndexConstants.ALL_TEXT_FIELD_NAME ), propertyValue.asString() ) );
        }
    }

    private static void addFulltextFields( final IndexConfig indexConfig, final IndexDocumentItemPath path, final Value propertyValue,
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

    private static void addFulltextFieldsTypeBased( final IndexDocumentItemPath path, final Value propertyValue,
                                                    final Set<AbstractStoreDocumentItem> indexDocumentItems )
    {
        final ValueType valueType = propertyValue.getType();

        if ( isTextField( valueType ) )
        {
            indexDocumentItems.add( new StoreDocumentAnalyzedItem( path, propertyValue.asString() ) );
            indexDocumentItems.add( new StoreDocumentNGramItem( path, propertyValue.asString() ) );
        }
    }

    private static boolean isTextField( final ValueType valueType )
    {
        return ( valueType == ValueTypes.STRING ) ||
            ( valueType == ValueTypes.HTML_PART ) ||
            ( valueType == ValueTypes.XML );
    }

    private static StoreDocumentOrderbyItem createOrderbyItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new StoreDocumentOrderbyItem( path, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private static void addIndexBaseTypeEntries( final IndexDocumentItemPath path, final Set<AbstractStoreDocumentItem> indexDocumentItems,
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

    private static StoreDocumentGeoPointItem createGeoPointItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new StoreDocumentGeoPointItem( path, propertyValue.asString() );
    }

    private static StoreDocumentStringItem createStringItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new StoreDocumentStringItem( path, propertyValue.asString() );
    }

    private static StoreDocumentNumberItem createNumericItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new StoreDocumentNumberItem( path, propertyValue.asDouble() );
    }

//    private IndexDocumentDateItem createDateItemType( final IndexDocumentItemPath path, final Value propertyValue )
//    {
//        return new IndexDocumentDateItem( path, propertyValue.asDateTime() );
//    }

    private static StoreDocumentDateItem createInstantType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new StoreDocumentDateItem( path, propertyValue.asInstant() );
    }

}
