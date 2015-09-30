package com.enonic.wem.repo.internal.elasticsearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.Test;

import com.enonic.wem.repo.internal.elasticsearch.document.AbstractStoreDocumentItem;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocumentItemFactory;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

public class IndexDocumentItemFactoryTest
{
    private PropertyTree propertyTree = new PropertyTree();

    @Test
    public void double_property()
        throws Exception
    {
        Property property = propertyTree.setDouble( "myDoubleField", 123.0 );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void int_property()
        throws Exception
    {
        Property property = propertyTree.setLong( "myLongField", 123L );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield number, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void date_property()
        throws Exception
    {
        Property property = propertyTree.setLocalDate( "myDateField", LocalDate.now() );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield date, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void dateTime_property()
        throws Exception
    {
        Property property = propertyTree.setLocalDateTime( "myDateTimeField", LocalDateTime.now() );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield date, string, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void geopoint_property()
        throws Exception
    {
        Property property = propertyTree.setGeoPoint( "myGeoPoint", GeoPoint.from( "41.12,-71.34" ) );

        final Set<AbstractStoreDocumentItem> indexDocumentItems = StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE );

        // Should yield string, geo-point, orderby, all*2
        assertEquals( 5, indexDocumentItems.size() );
    }

    @Test
    public void string_by_type()
        throws Exception
    {
        Property property = propertyTree.setString( "myStringProp", "myStringValue" );

        // When by type, should yield string, fulltext, tokenized, orderby, all analyzed, all tokenized
        assertEquals( 6, StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void double_by_type()
        throws Exception
    {
        Property property = propertyTree.setDouble( "myStringProp", 1.0 );

        // When by type, should yield string, number, orderby, all*2
        assertEquals( 5, StoreDocumentItemFactory.create( property, IndexConfig.BY_TYPE ).size() );
    }

    @Test
    public void fulltext_not_in_all()
        throws Exception
    {
        Property property = propertyTree.setString( "myStringProp", "myStringValue" );

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
