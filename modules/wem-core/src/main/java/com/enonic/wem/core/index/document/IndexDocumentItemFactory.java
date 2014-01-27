package com.enonic.wem.core.index.document;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.HtmlPart;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.Xml;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;

public class IndexDocumentItemFactory
{

    private final boolean decideFulltextByType;

    public IndexDocumentItemFactory( final boolean decideFulltextByType )
    {
        this.decideFulltextByType = decideFulltextByType;
    }

    public Set<AbstractIndexDocumentItem> create( final IndexDocumentItemPath path, final Value value,
                                                  final PropertyIndexConfig propertyIndexConfig )
    {
        return doCreate( propertyIndexConfig, path, value );
    }

    public Set<AbstractIndexDocumentItem> create( final Property property, final PropertyIndexConfig propertyIndexConfig )
    {
        final IndexDocumentItemPath path = IndexDocumentItemPath.from( property );
        final Value propertyValue = property.getValue();

        return doCreate( propertyIndexConfig, path, propertyValue );
    }

    private Set<AbstractIndexDocumentItem> doCreate( final PropertyIndexConfig propertyIndexConfig, final IndexDocumentItemPath path,
                                                     final Value propertyValue )
    {
        final Set<AbstractIndexDocumentItem> indexDocumentItems = Sets.newHashSet();

        if ( !propertyIndexConfig.enabled() )
        {
            return indexDocumentItems;
        }

        addIndexBaseTypeEntries( path, indexDocumentItems, propertyValue );

        if ( decideFulltextByType )
        {
            addFulltextFieldsTypeBased( path, propertyValue, indexDocumentItems );
        }
        else
        {
            addFulltextFields( propertyIndexConfig, path, propertyValue, indexDocumentItems );
        }

        indexDocumentItems.add( createOrderbyItemType( path, propertyValue ) );

        addAllFields( propertyValue, indexDocumentItems );

        return indexDocumentItems;
    }

    private void addAllFields( final Value propertyValue, final Set<AbstractIndexDocumentItem> indexDocumentItems )
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

    private void addFulltextFields( final PropertyIndexConfig propertyIndexConfig, final IndexDocumentItemPath path,
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

    private void addFulltextFieldsTypeBased( final IndexDocumentItemPath path, final Value propertyValue,
                                             final Set<AbstractIndexDocumentItem> indexDocumentItems )
    {
        final ValueType valueType = propertyValue.getType();

        if ( isTextField( valueType ) )
        {
            indexDocumentItems.add( new IndexDocumentAnalyzedItem( path, propertyValue.asString() ) );
            indexDocumentItems.add( new IndexDocumentNGramItem( path, propertyValue.asString() ) );
        }
    }

    private boolean isTextField( final ValueType valueType )
    {
        return valueType instanceof com.enonic.wem.api.data.type.String ||
            valueType instanceof HtmlPart ||
            valueType instanceof Xml;
    }

    private IndexDocumentOrderbyItem createOrderbyItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentOrderbyItem( path, OrderbyValueResolver.getOrderbyValue( propertyValue ) );
    }

    private void addIndexBaseTypeEntries( final IndexDocumentItemPath path, final Set<AbstractIndexDocumentItem> indexDocumentItems,
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

    private IndexDocumentGeoPointItem createGeoPointItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentGeoPointItem( path, (Value.GeoPoint) propertyValue );
    }

    private IndexDocumentStringItem createStringItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentStringItem( path, propertyValue.asString() );
    }

    private IndexDocumentNumberItem createNumericItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentNumberItem( path, propertyValue.asDouble() );
    }

    private IndexDocumentDateItem createDateItemType( final IndexDocumentItemPath path, final Value propertyValue )
    {
        return new IndexDocumentDateItem( path, propertyValue.asDateTime() );
    }

}
