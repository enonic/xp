package com.enonic.wem.core.index.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;

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

        addFulltextFields( propertyIndexConfig, path, propertyValue, indexDocumentItems );

        indexDocumentItems.add( createOrderbyItemType( path, propertyValue ) );

        addAllFields( propertyValue, indexDocumentItems );

        return indexDocumentItems;
    }

    private static void addAllFields( final Value propertyValue, final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {

        // TODO: This should be decided e.g by PropertyIndexConfig
        if ( isTextField( propertyValue.getType() ) )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( IndexDocumentItemPath.from( IndexConstants.ALL_TEXT_FIELD_NAME ),
                                                                   propertyValue.asString() ) );

            indexDocumentItems.add(
                new IndexDocumentNGramItem( IndexDocumentItemPath.from( IndexConstants.ALL_TEXT_FIELD_NAME ), propertyValue.asString() ) );
        }
    }

    private static void addFulltextFields( final PropertyIndexConfig propertyIndexConfig, final IndexDocumentItemPath path,
                                           final Value propertyValue, final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        if ( propertyIndexConfig.fulltextEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( path, propertyValue.asString() ) );
        }

        if ( propertyIndexConfig.tokenizeEnabled() )
        {
            indexDocumentItems.add( new IndexDocumentNGramItem( path, propertyValue.asString() ) );
        }
    }

    private static void addFulltextFieldsTypeBased( final IndexDocumentItemPath path, final Value propertyValue,
                                                    final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        final ValueType valueType = propertyValue.getType();

        if ( isTextField( valueType ) )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( path, propertyValue.asString() ) );
            indexDocumentItems.add( new IndexDocumentNGramItem( path, propertyValue.asString() ) );
        }
    }

    private static boolean isTextField( final ValueType valueType )
    {
        return ( valueType == ValueTypes.STRING ) ||
            ( valueType == ValueTypes.HTML_PART ) ||
            ( valueType == ValueTypes.XML );
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

    private static IndexDocumentGeoPointItem createGeoPointItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentGeoPointItem( path, propertyValue.asString() );
    }

    private static IndexDocumentStringItem createStringItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentStringItem( path, propertyValue.asString() );
    }

    private static IndexDocumentNumberItem createNumericItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentNumberItem( path, propertyValue.asDouble() );
    }

//    private IndexDocumentDateItem createDateItemType( final IndexDocumentItemPath path, final Value propertyValue )
//    {
//        return new IndexDocumentDateItem( path, propertyValue.asDateTime() );
//    }

    private static IndexDocumentDateItem createInstantType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentDateItem( path, propertyValue.asInstant() );
    }

}
