package com.enonic.wem.core.elasticsearch;

import java.time.LocalDate;
import java.util.Set;

import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.core.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.core.elasticsearch.document.StoreDocumentItemFactory;

import static org.junit.Assert.*;

public class StoreDocumentItemFactoryTest
{
    @Test
    public void double_property()
        throws Exception
    {
        Property property = Property.newDouble( "myDoubleField", 123.0 );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = Property.newLong( "myDoubleField", 123L );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = Property.newLocalDate( "myDateField", LocalDate.now() );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield date, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = new Property( "myGeoPoint", Value.newGeoPoint( "41.12,-71.34" ) );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield string, geo-point, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void string_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        // When by type, should yield string, fulltext, tokenized, orderby, all analyzed, all tokenized
        assertEquals( 6, StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void double_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newDouble( 1.0 ) );

        // When by type, should yield string, number, orderby, all*2
        assertEquals( 5, StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void fulltext_not_in_all()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        // should yield string, fulltext, !tokenized, orderby, !all analyzed, !all tokenized
        assertEquals( 3, StoreDocumentItemFactory.create( property, IndexConfig.create().
            decideByType( false ).
            enabled( true ).
            fulltext( true ).
            nGram( false ).
            includeInAllText( false ).
            build() ).size() );
    }


}
