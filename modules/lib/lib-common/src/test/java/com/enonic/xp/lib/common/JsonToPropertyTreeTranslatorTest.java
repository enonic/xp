package com.enonic.xp.lib.common;

import java.net.URL;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonToPropertyTreeTranslatorTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void all_input_types()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );
        final PropertyTree data = JsonToPropertyTreeTranslator.translate( node );

        final Property media = data.getProperty( "media" );
        assertNotNull( media );
        assertEquals( ValueTypes.PROPERTY_SET.getName(), media.getType().getName() );
    }

    @Test
    public void map_array_values()
        throws Exception
    {
        final JsonNode node = loadJson( "stringArray" );

        final PropertyTree data = JsonToPropertyTreeTranslator.translate( node );

        final Property myArray = data.getProperty( "stringArray" );
        assertNotNull( myArray );
        assertEquals( ValueTypes.STRING.getName(), myArray.getType().getName() );

        final Property myArray0 = data.getProperty( "stringArray[0]" );
        assertNotNull( myArray0 );

        final Property myArray1 = data.getProperty( "stringArray[1]" );
        assertNotNull( myArray1 );

        final Property myArray2 = data.getProperty( "stringArray[2]" );
        assertNotNull( myArray2 );
    }

    @Test
    public void boolean_value()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );

        final PropertyTree data = new FormJsonToPropertyTreeTranslator( null, false ).translate( node );

        final Property property = data.getProperty( "checkbox" );

        assertTrue( property.getValue().isBoolean());
        assertEquals( true, property.getBoolean());
    }


    private JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found");
        return MAPPER.readTree( url );
    }
}

