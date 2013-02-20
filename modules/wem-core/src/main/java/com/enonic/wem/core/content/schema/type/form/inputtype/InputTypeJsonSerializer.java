package com.enonic.wem.core.content.schema.type.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.type.form.inputtype.BaseInputType;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class InputTypeJsonSerializer
    extends AbstractJsonSerializer<BaseInputType>
{
    public InputTypeJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    @Override
    public JsonNode serialize( final BaseInputType baseInputType )
    {
        final ObjectNode inputNode = objectMapper().createObjectNode();
        inputNode.put( "name", baseInputType.getName() );
        inputNode.put( "builtIn", baseInputType.isBuiltIn() );
        return inputNode;
    }

    public BaseInputType parse( final JsonNode node )
    {
        final String className = JsonSerializerUtil.getStringValue( "name", node );
        final boolean builtIn = JsonSerializerUtil.getBooleanValue( "builtIn", node );
        return InputTypeFactory.instantiate( className, builtIn );
    }
}
