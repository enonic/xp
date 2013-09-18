package com.enonic.wem.core.content.serializer;


import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueTypes;

import static junit.framework.Assert.assertEquals;

public class PopertyJsonSerializerTest
{
    private PopertyJsonSerializer serializer = new PopertyJsonSerializer();

    private JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Before
    public void before()
    {
        serializer.prettyPrint();
    }

    @Test
    public void parse_Text()
    {
        ObjectNode dataObj = jsonTestHelper.objectMapper().createObjectNode();
        dataObj.put( "name", "myData" );
        dataObj.put( "path", "myData" );
        dataObj.put( "type", "Text" );
        dataObj.put( "value", "A value" );

        Property property = serializer.parse( dataObj );
        assertEquals( "myData", property.getName() );
        assertEquals( ValueTypes.TEXT, property.getValueType() );
        assertEquals( "A value", property.getObject() );
    }

    @Test
    public void parse_BinaryId()
    {
        ObjectNode dataObj = jsonTestHelper.objectMapper().createObjectNode();
        dataObj.put( "name", "myData" );
        dataObj.put( "path", "myData" );
        dataObj.put( "type", "BinaryId" );
        dataObj.put( "value", "217482f4-b89a-4286-9111-5120d11da6c2" );

        Property property = serializer.parse( dataObj );
        assertEquals( "myData", property.getName() );
        assertEquals( ValueTypes.BINARY_ID, property.getValueType() );
        assertEquals( BinaryId.from( "217482f4-b89a-4286-9111-5120d11da6c2" ), property.getObject() );
    }
}
