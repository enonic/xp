package com.enonic.xp.lib.common;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.testing.helper.JsonAssert;
import com.enonic.xp.util.GeoPoint;

class PropertyTreeMapperTest
{
    @Test
    void numbers()
    {
        final PropertyTree properties = new PropertyTree();
        properties.addDouble( "myDouble", 2.0 );
        properties.addLong( "myLong", 2L );

        serializeAndAssert( "mapper-numbers", properties );
    }

    @Test
    void array()
    {
        final PropertyTree properties = new PropertyTree();
        properties.addStrings( "myValues", "value1", "value2", "value3" );

        serializeAndAssert( "mapper-array", properties );
    }

    @Test
    void map()
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet mySet = properties.addSet( "mySet" );
        mySet.setString( "mySetValue", "value" );
        mySet.setString( "mySetValue2", "value2" );

        serializeAndAssert( "mapper-map", properties );
    }

    @Test
    void map_in_map()
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet mySet = properties.addSet( "mySet" );
        mySet.setString( "mySetValue", "value" );
        final PropertySet mySetInSet = mySet.addSet( "mySetInSet" );
        mySetInSet.setString( "mySetValue", "value" );

        serializeAndAssert( "mapper-map-in-map", properties );
    }

    @Test
    void list_of_maps()
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet subSet1 = properties.newSet();
        subSet1.setString( "subSet1Value1", "fisk1" );
        subSet1.setString( "subSet2Value2", "ost1" );
        final PropertySet subSet2 = properties.newSet();
        subSet2.setString( "subSet2Value1", "fisk2" );
        subSet2.setString( "subSet2Value2", "ost2" );
        properties.addSets( "subSets", subSet1, subSet2 );

        serializeAndAssert( "mapper-list-of-maps", properties );
    }

    @Test
    void raw_values()
    {
        final PropertyTree properties = new PropertyTree();
        final GeoPoint geoPointValue = GeoPoint.from( "80,80" );
        properties.addGeoPoint( "myGeoPoint", geoPointValue );

        MapGenerator generator = Mockito.mock( MapGenerator.class );
        new PropertyTreeMapper( true, properties ).serialize( generator );

        Mockito.verify( generator ).rawValue( "myGeoPoint", geoPointValue );
    }

    @Test
    void string()
    {
        final PropertyTree properties = new PropertyTree();
        properties.setString( "displayName", "This is brand new node" );
        serializeAndAssert( "mapper-string", properties );
    }

    private void serializeAndAssert( final String name, final PropertyTree value )
    {
        JsonAssert.assertMapper( getClass(), name + ".json", new PropertyTreeMapper( value ) );
    }
}
