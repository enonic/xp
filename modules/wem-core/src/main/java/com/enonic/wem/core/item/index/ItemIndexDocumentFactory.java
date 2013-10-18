package com.enonic.wem.core.item.index;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemIndexConfig;
import com.enonic.wem.api.item.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.IndexDocument2;
import com.enonic.wem.core.index.indexdocument.IndexDocumentItemFactory;


public class ItemIndexDocumentFactory
{
    public Collection<IndexDocument2> create( final Item item )
    {
        Set<IndexDocument2> indexDocuments = Sets.newHashSet();

        indexDocuments.add( createDataDocument( item ) );

        return indexDocuments;
    }

    private IndexDocument2 createDataDocument( final Item item )
    {

        final ItemIndexConfig itemIndexConfig = item.getItemIndexConfig();

        final IndexDocument2.Builder builder = IndexDocument2.newIndexDocument().
            setId( item.id() ).
            setIndex( "WEM" ).
            setIndexType( IndexType.ITEM );

        addItemMetaData( item, builder );
        addItemData( item, builder );

        return builder.build();
    }

    private void addItemMetaData( final Item item, final IndexDocument2.Builder builder )
    {

    }

    private void addItemData( final Item item, final IndexDocument2.Builder builder )
    {
        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                PropertyIndexConfig propertyIndexConfig = item.getItemIndexConfig().getPropertyIndexConfig( property.getPath() );

                if ( propertyIndexConfig == null )
                {
                    propertyIndexConfig = createItemDefaultPropertyIndexConfig();
                }

                builder.addEntries( IndexDocumentItemFactory.create( property, propertyIndexConfig ) );
            }
        };

        visitor.traverse( item.rootDataSet() );
    }

    private PropertyIndexConfig createItemDefaultPropertyIndexConfig()
    {
        return PropertyIndexConfig.newPropertyIndexConfig().
            autocompleteEnabled( true ).
            fulltextEnabled( true ).
            enabled( true ).
            build();
    }

}