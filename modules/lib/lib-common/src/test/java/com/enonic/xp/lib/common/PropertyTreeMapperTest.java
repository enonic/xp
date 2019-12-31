package com.enonic.xp.lib.common;

import java.net.URL;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PropertyTreeMapperTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT ).
        enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );

    @Test
    public void numbers()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        properties.addDouble( "myDouble", 2.0 );
        properties.addLong( "myLong", 2L );

        serializeAndAssert( "mapper-numbers", properties );
    }

    @Test
    public void array()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        properties.addStrings( "myValues", "value1", "value2", "value3" );

        serializeAndAssert( "mapper-array", properties );
    }

    @Test
    public void map()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet mySet = properties.addSet( "mySet" );
        mySet.setString( "mySetValue", "value" );
        mySet.setString( "mySetValue2", "value2" );

        serializeAndAssert( "mapper-map", properties );
    }

    @Test
    public void map_in_map()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet mySet = properties.addSet( "mySet" );
        mySet.setString( "mySetValue", "value" );
        final PropertySet mySetInSet = mySet.addSet( "mySetInSet" );
        mySetInSet.setString( "mySetValue", "value" );

        serializeAndAssert( "mapper-map-in-map", properties );
    }

    @Test
    public void list_of_maps()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        final PropertySet subSet1 = new PropertySet();
        subSet1.setString( "subSet1Value1", "fisk1" );
        subSet1.setString( "subSet2Value2", "ost1" );
        final PropertySet subSet2 = new PropertySet();
        subSet2.setString( "subSet2Value1", "fisk2" );
        subSet2.setString( "subSet2Value2", "ost2" );
        properties.addSets( "subSets", subSet1, subSet2 );

        serializeAndAssert( "mapper-list-of-maps", properties );
    }

    @Test
    public void raw_values()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        final GeoPoint geoPointValue = GeoPoint.from( "80,80" );
        properties.addGeoPoint( "myGeoPoint", geoPointValue );

        MapGenerator generator = Mockito.mock( MapGenerator.class );
        new PropertyTreeMapper( true, properties ).serialize( generator );

        Mockito.verify( generator ).rawValue( "myGeoPoint", geoPointValue );
    }

    @Test
    public void string()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        properties.setString( "displayName", "This is brand new node" );
        serializeAndAssert( "mapper-string", properties );
    }

    private void serializeAndAssert( final String name, final PropertyTree value )
        throws Exception
    {
        final String resource = name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        final JsonNode expectedJson = MAPPER.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        new PropertyTreeMapper( value ).serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = MAPPER.writeValueAsString( expectedJson );
        final String actualStr = MAPPER.writeValueAsString( actualJson );

        assertEquals( expectedStr, actualStr );
    }


}
