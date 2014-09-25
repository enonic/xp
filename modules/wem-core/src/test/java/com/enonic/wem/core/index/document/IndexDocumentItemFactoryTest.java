package com.enonic.wem.core.index.document;

import java.time.LocalDate;
import java.util.Set;

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

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = Property.newLong( "myDoubleField", 123L );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = Property.newLocalDate( "myDateField", LocalDate.now() );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield date, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = new Property( "myGeoPoint", Value.newGeoPoint( "41.12,-71.34" ) );

        final Set<AbstractIndexDocumentItem> indexDocumentItems = IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield string, geo-point, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void string_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        // When by type, should yield string, fulltext, tokenized, orderby, all analyzed, all tokenized
        assertEquals( 6, IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void double_by_type()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newDouble( 1.0 ) );

        // When by type, should yield string, number, orderby, all*2
        assertEquals( 5, IndexDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void fulltext_not_in_all()
        throws Exception
    {
        Property property = new Property( "myStringProp", Value.newString( "myStringValue" ) );

        // should yield string, fulltext, !tokenized, orderby, !all analyzed, !all tokenized
        assertEquals( 3, IndexDocumentItemFactory.create( property, IndexConfig.create().
            decideByType( false ).
            enabled( true ).
            fulltext( true ).
            nGram( false ).
            includeInAllText( false ).
            build() ).size() );
    }


}
