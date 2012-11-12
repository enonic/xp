package com.enonic.wem.core.content.type.component.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.api.content.type.component.inputtype.InputType;
import com.enonic.wem.core.content.JsonParserUtil;

public class InputTypeSerializerJson
{
    public JsonNode generate( final InputType inputType, final ObjectMapper objectMapper )
    {
        final BaseInputType baseInputType = (BaseInputType) inputType;

        final ObjectNode inputNode = objectMapper.createObjectNode();
        inputNode.put( "name", baseInputType.getName() );
        inputNode.put( "builtIn", baseInputType.isBuiltIn() );
        return inputNode;
    }

    public BaseInputType parse( final JsonNode node )
    {
        final String className = JsonParserUtil.getStringValue( "name", node );
        final boolean builtIn = JsonParserUtil.getBooleanValue( "builtIn", node );
        return InputTypeFactory.instantiate( className, builtIn );
    }
}
