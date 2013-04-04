package com.enonic.wem.core.content.serializer;


import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.type.DataTypes;

import static junit.framework.Assert.assertEquals;

public class DataJsonSerializerTest
{
    private DataJsonSerializer serializer = new DataJsonSerializer();

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

        Data data = serializer.parse( dataObj );
        assertEquals( "myData", data.getName() );
        assertEquals( DataTypes.TEXT, data.getType() );
        assertEquals( "A value", data.getObject() );
    }

    @Test
    public void parse_BinaryId()
    {
        ObjectNode dataObj = jsonTestHelper.objectMapper().createObjectNode();
        dataObj.put( "name", "myData" );
        dataObj.put( "path", "myData" );
        dataObj.put( "type", "BinaryId" );
        dataObj.put( "value", "217482f4-b89a-4286-9111-5120d11da6c2" );

        Data data = serializer.parse( dataObj );
        assertEquals( "myData", data.getName() );
        assertEquals( DataTypes.BINARY_ID, data.getType() );
        assertEquals( BinaryId.from( "217482f4-b89a-4286-9111-5120d11da6c2" ), data.getObject() );
    }
}
