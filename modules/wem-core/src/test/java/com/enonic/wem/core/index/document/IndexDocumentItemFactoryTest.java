package com.enonic.wem.core.index.document;

import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.PropertyIndexConfig;

import static org.junit.Assert.*;

public class IndexDocumentItemFactoryTest
{
    @Test
    public void double_property()
        throws Exception
    {
        Property property = Property.newDouble( "myDoubleField", 123.0 );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).nGramEnabled( false ).build();

        final IndexDocumentItemFactory factory = new IndexDocumentItemFactory( false );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = factory.create( property, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = Property.newLong( "myDoubleField", 123L );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).nGramEnabled( false ).build();

        final IndexDocumentItemFactory factory = new IndexDocumentItemFactory( false );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = factory.create( property, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = Property.newLocalDate( "myDateField", LocalDate.now() );

        final IndexDocumentItemFactory factory = new IndexDocumentItemFactory( false );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).nGramEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = factory.create( property, propertyIndexConfig );

        // Should yield date, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }


    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = new Property( "myGeoPoint", Value.newGeoPoint( "41.12,-71.34" ) );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).nGramEnabled( false ).build();

        final IndexDocumentItemFactory factory = new IndexDocumentItemFactory( false );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = factory.create( property, propertyIndexConfig );

        // Should yield string, geo-point, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void fulltext_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).nGramEnabled( false ).build();

        // When not by type, no analyzed or tokenized
        assertEquals( 4, new IndexDocumentItemFactory( false ).create( property, propertyIndexConfig ).size() );

        // When by type, should yield string, fulltext, tokenized, orderby, all analyzed, all tokenized
        assertEquals( 6, new IndexDocumentItemFactory( true ).create( property, propertyIndexConfig ).size() );
    }
}
