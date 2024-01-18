package com.enonic.xp.lib.common;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.util.JsonHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonToPropertyTreeTranslatorTest
{
    @Test
    public void all_input_types()
        throws Exception
    {
        final JsonNode node = loadJson( "allInputTypes" );
        final PropertyTree data = PropertyTree.fromMap(JsonHelper.toMap( node ) );

        final Property media = data.getProperty( "media" );
        assertNotNull( media );
        assertEquals( ValueTypes.PROPERTY_SET.getName(), media.getType().getName() );
    }

    @Test
    public void map_array_values()
        throws Exception
    {
        final JsonNode node = loadJson( "stringArray" );

        final PropertyTree data = PropertyTree.fromMap( JsonHelper.toMap( node ) );

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

        final PropertyTree data = PropertyTree.fromMap( JsonHelper.toMap( node ) );

        final Property property = data.getProperty( "checkbox" );

        assertTrue( property.getValue().isBoolean());
        assertEquals( true, property.getBoolean());
    }


    private JsonNode loadJson( final String name )
        throws Exception
    {
        return JsonTestHelper.loadJson( getClass(), name );
    }
}

