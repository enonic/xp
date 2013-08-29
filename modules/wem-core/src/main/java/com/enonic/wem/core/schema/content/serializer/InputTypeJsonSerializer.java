package com.enonic.wem.core.schema.content.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.inputtype.BaseInputType;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypeName;
import com.enonic.wem.core.schema.content.form.inputtype.InputTypeResolver;
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
        inputNode.put( "name", InputTypeName.from( baseInputType ).toString() );
        return inputNode;
    }

    public BaseInputType parse( final JsonNode node )
    {
        final String inputTypeName = JsonSerializerUtil.getStringValue( "name", node );
        return InputTypeResolver.get().resolve( inputTypeName );
    }
}
