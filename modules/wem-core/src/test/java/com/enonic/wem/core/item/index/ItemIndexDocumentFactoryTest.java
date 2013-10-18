package com.enonic.wem.core.item.index;

import java.util.Collection;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemIndexConfig;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.item.PropertyIndexConfig;
import com.enonic.wem.core.index.indexdocument.AbstractIndexDocumentItem;
import com.enonic.wem.core.index.indexdocument.IndexDocument2;

import static org.junit.Assert.*;

public class ItemIndexDocumentFactoryTest
{

    ItemIndexDocumentFactory factory = new ItemIndexDocumentFactory();

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

        final Set<AbstractIndexDocumentItem> indexDocumentEntries = firstAndOnlyDocument.getIndexDocumentEntries();

        // All three values should have an entry for: string, analyzed, tokenized, orderby, thus 12 elements
        assertEquals( 12, indexDocumentEntries.size() );

    }

    @Test
    public void create_simple_document()
        throws Exception
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "a", new Value.String( "myValue" ) );
        rootDataSet.setProperty( "a/b", new Value.Double( 2.0 ) );
        rootDataSet.setProperty( "a/b/c", new Value.DateMidnight( DateMidnight.now() ) );

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
                addPropertyIndexConfig( "a/b", PropertyIndexConfig.
                    newPropertyIndexConfig().
                    enabled( true ).
                    autocompleteEnabled( false ).
                    fulltextEnabled( false ).
                    build() ).
                addPropertyIndexConfig( "a/b/c", PropertyIndexConfig.
                    newPropertyIndexConfig().
                    enabled( true ).
                    autocompleteEnabled( false ).
                    fulltextEnabled( false ).
                    build() ).
                build() ).
            build();

        final Collection<IndexDocument2> indexDocument = factory.create( item );

        assertFalse( indexDocument.isEmpty() );
        assertEquals( indexDocument.size(), 1 );

        final IndexDocument2 firstDoc = indexDocument.iterator().next();

        final Set<AbstractIndexDocumentItem> indexDocumentEntries = firstDoc.getIndexDocumentEntries();

        // a should have string, analyzed, tokenized and orderby
        // a/b should have number, string, orderby
        // a/b/c should have date, string, orderby
        // Total 10 entries
        assertEquals( 10, indexDocumentEntries.size() );
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
