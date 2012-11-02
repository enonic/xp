package com.enonic.wem.core.content.type.component.inputtype;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.api.content.type.component.inputtype.InputType;
import com.enonic.wem.core.content.JsonParserUtil;

public class InputTypeSerializerJson
{
    public void generate( final InputType inputType, final JsonGenerator g )
        throws IOException
    {
        final BaseInputType baseInputType = (BaseInputType) inputType;

        g.writeFieldName( "inputType" );
        g.writeStartObject();
        g.writeStringField( "name", baseInputType.getName() );
        g.writeBooleanField( "builtIn", baseInputType.isBuiltIn() );
        g.writeEndObject();
    }

    public BaseInputType parse( final JsonNode node )
    {
        final String className = JsonParserUtil.getStringValue( "name", node );
        final boolean builtIn = JsonParserUtil.getBooleanValue( "builtIn", node );
        return InputTypeFactory.instantiate( className, builtIn );
    }
}
