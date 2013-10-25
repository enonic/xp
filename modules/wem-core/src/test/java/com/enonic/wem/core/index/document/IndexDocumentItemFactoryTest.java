package com.enonic.wem.core.index.document;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.PropertyIndexConfig;

import static org.junit.Assert.*;

public class IndexDocumentItemFactoryTest
{

    @Test
    public void double_property()
        throws Exception
    {
        Property dateProperty = Property.newProperty().type( ValueTypes.DOUBLE ).value( 123.0 ).name( "myDoubleField" ).build();

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( dateProperty, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property dateProperty = Property.newProperty().type( ValueTypes.LONG ).value( 123L ).name( "myLongField" ).build();

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( dateProperty, propertyIndexConfig );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property dateProperty =
            Property.newProperty().type( ValueTypes.DATE_MIDNIGHT ).value( DateTime.now().toDateMidnight() ).name( "myDateField" ).build();

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( dateProperty, propertyIndexConfig );

        // Should yield date, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }


    @Test
    public void geopoint_property()
        throws Exception
    {
        Property arrayProperty =
            Property.newProperty().type( ValueTypes.GEO_POINT ).name( "myGeoPoint" ).value( new Value.GeoPoint( "41.12,-71.34" ) ).build();

        PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).fulltextEnabled( false ).autocompleteEnabled( false ).build();

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( arrayProperty, propertyIndexConfig );

        // Should yield string, geo-point, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

}
