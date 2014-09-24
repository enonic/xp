package com.enonic.wem.core.index.document;

import java.time.LocalDate;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;

import static org.junit.Assert.*;

public class IndexDocumentItemFactoryTest
{
    @Test
    public void double_property()
        throws Exception
    {
        Property property = Property.newDouble( "myDoubleField", 123.0 );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = Property.newLong( "myDoubleField", 123L );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = Property.newLocalDate( "myDateField", LocalDate.now() );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield date, string, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = new Property( "myGeoPoint", Value.newGeoPoint( "41.12,-71.34" ) );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield string, geo-point, orderby
        assertEquals( 3, indexDocumentItems.size() );
    }

    @Ignore
    @Test
    public void fulltext_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        // When not by type, no analyzed or tokenized
        assertEquals( 4, IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );

        // When by type, should yield string, fulltext, tokenized, orderby, all analyzed, all tokenized
        assertEquals( 6, IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }
}
