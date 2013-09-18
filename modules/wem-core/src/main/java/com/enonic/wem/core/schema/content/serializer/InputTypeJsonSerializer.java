package com.enonic.wem.core.schema.content.serializer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.inputtype.InputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeName;
import com.enonic.wem.core.schema.content.form.inputtype.InputTypeResolver;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class InputTypeJsonSerializer
    extends AbstractJsonSerializer<InputType>
{
    public InputTypeJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    @Override
    public JsonNode serialize( final InputType inputType )
    {
        final ObjectNode inputNode = objectMapper().createObjectNode();
        inputNode.put( "name", InputTypeName.from( inputType ).toString() );
        return inputNode;
    }

    public InputType parse( final JsonNode node )
    {
        final String inputTypeName = JsonSerializerUtil.getStringValue( "name", node );
        return InputTypeResolver.get().resolve( inputTypeName );
    }
}
