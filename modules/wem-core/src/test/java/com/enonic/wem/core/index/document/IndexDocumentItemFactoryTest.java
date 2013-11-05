package com.enonic.wem.core.index.document;

import java.util.Set;

import org.joda.time.DateTime;
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
        Property property = new Property.Double( "myDoubleField", 123.0 );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = new Property.Long( "myDoubleField", 123L );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = new Property.Date( "myDateField", DateTime.now().toDateMidnight() );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, propertyIndexConfig );

        // Should yield date, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }


    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = new Property( "myGeoPoint", new Value.GeoPoint( "41.12,-71.34" ) );

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, propertyIndexConfig );

        // Should yield string, geo-point, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

}
