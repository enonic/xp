package com.enonic.wem.core.item.index;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemIndexConfig;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.item.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.indexdocument.IndexBaseType;
import com.enonic.wem.core.index.indexdocument.IndexDocument2;

import static org.junit.Assert.*;

public class ItemIndexDocumentFactoryTest
{

    private static final int NUMBER_OF_METADATA_ITEMS = 5;

    ItemIndexDocumentFactory factory = new ItemIndexDocumentFactory();

    @Test
    public void index_document_meta_data()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

        final String myAnalyzerName = "myAnalyzer";

        DateTime modifiedDateTime = new DateTime( 2013, 01, 02, 03, 04, 05 );

        Item item = Item.newItem().
            id( new ItemId( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( ItemPath.ROOT ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            itemIndexConfig( ItemIndexConfig.newItemIndexConfig().
                analyzer( myAnalyzerName ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocument = factory.create( item );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstAndOnlyDoc = indexDocument.iterator().next();

        assertEquals( myAnalyzerName, firstAndOnlyDoc.getAnalyzer() );
        assertEquals( IndexConstants.NODB_INDEX, firstAndOnlyDoc.getIndex() );
        assertEquals( IndexType.NODE, firstAndOnlyDoc.getIndexType() );

        final AbstractIndexDocumentItem createdTimeItem =
            firstAndOnlyDoc.getItemWithName( ItemIndexDocumentFactory.CREATED_TIME_PROPERTY_NAME, IndexBaseType.DATETIME );

        assertEquals( item.getCreatedTime(), createdTimeItem.getValue() );

        final AbstractIndexDocumentItem creator =
            firstAndOnlyDoc.getItemWithName( ItemIndexDocumentFactory.CREATOR_PROPERTY_NAME, IndexBaseType.STRING );

        assertEquals( "test:creator", creator.getValue() );

        final AbstractIndexDocumentItem modifier =
            firstAndOnlyDoc.getItemWithName( ItemIndexDocumentFactory.MODIFIER_PROPERTY_NAME, IndexBaseType.STRING );

        assertEquals( "test:modifier", modifier.getValue() );

    }

    @Test
    public void string_array_value_document()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue1" ) );
        rootDataSet.addProperty( "a", new Value.String( "myValue2" ) );
        rootDataSet.addProperty( "a", new Value.String( "myValue3" ) );

        Item item = Item.newItem().
            id( new ItemId( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( ItemPath.ROOT ).
            itemIndexConfig( ItemIndexConfig.newItemIndexConfig().
                analyzer( "default" ).
                addPropertyIndexConfig( "a", PropertyIndexConfig.
                    newPropertyIndexConfig().
                    enabled( true ).
                    autocompleteEnabled( true ).
                    fulltextEnabled( true ).
                    build() ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocument = factory.create( item );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstAndOnlyDocument = indexDocument.iterator().next();

        final Set<AbstractIndexDocumentItem> indexDocumentEntries = firstAndOnlyDocument.getIndexDocumentItems();

        // All three values should have an entry for: string, analyzed, tokenized, orderby, thus 12 elements, plus the number of meta-data
        assertEquals( 12 + NUMBER_OF_METADATA_ITEMS, indexDocumentEntries.size() );
    }

    @Test
    public void create_document_without_property_index_settings()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.addProperty( "a/b", new Value.Double( 3.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

        Item item = Item.newItem().
            id( new ItemId( "myId" ) ).
            rootDataSet( rootDataSet ).
            parent( ItemPath.ROOT ).
            itemIndexConfig( ItemIndexConfig.newItemIndexConfig().
                analyzer( "default" ).
                build() ).
            build();

        ItemIndexDocumentFactory factory = new ItemIndexDocumentFactory();

        final Collection<IndexDocument2> indexDocument = factory.create( item );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstDoc = indexDocument.iterator().next();

    }


}
