package com.enonic.wem.api.data.serializer;


import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class PropertyJsonSerializerTest
{
    private PropertyJsonSerializer serializer = new PropertyJsonSerializer();

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
        dataObj.put( "type", "String" );
        dataObj.put( "value", "A value" );

        Property property = serializer.parse( dataObj );
        assertEquals( "myData", property.getName() );
        assertEquals( ValueTypes.STRING, property.getValueType() );
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
