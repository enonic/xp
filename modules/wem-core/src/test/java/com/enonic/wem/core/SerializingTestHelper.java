package com.enonic.wem.core;


import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.XmlTestHelper;

public class SerializingTestHelper
{
    private JsonTestHelper jsonTestHelper;

    private XmlTestHelper xmlTestHelper;

    public SerializingTestHelper( final Object testInstance, final boolean prettyPrint )
    {
        jsonTestHelper = new JsonTestHelper( testInstance, prettyPrint );
        xmlTestHelper = new XmlTestHelper( testInstance );
    }

    public JsonNode loadTestJson( final String fileName )
    {
        return jsonTestHelper.loadTestJson( fileName );
    }

    public String loadJsonAsString( String fileName )
    {
        return jsonToString( jsonTestHelper.loadTestJson( fileName ) );
    }

    public String jsonToString( final JsonNode value )
    {
        return jsonTestHelper.jsonToString( value );
    }

    public JsonNode stringToJson( final String jsonString )
    {
        return jsonTestHelper.stringToJson( jsonString );
    }

    public String loadTextXml( final String fileName )
    {
        return xmlTestHelper.loadTestXml( fileName );
    }
}
