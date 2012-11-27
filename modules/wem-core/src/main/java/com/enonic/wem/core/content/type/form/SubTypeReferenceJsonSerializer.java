package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.form.SubTypeReference;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;

class SubTypeReferenceJsonSerializer
    extends AbstractJsonSerializer<SubTypeReference>
{
    private static final String NAME = "name";

    private static final String REFERENCE = "reference";

    public static final String TYPE = "type";

    @Override
    protected JsonNode serialize( final SubTypeReference subTypeReference, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( NAME, subTypeReference.getName() );
        jsonObject.put( REFERENCE, subTypeReference.getSubTypeQualifiedName().toString() );
        jsonObject.put( TYPE, subTypeReference.getSubTypeClass().getSimpleName() );
        return jsonObject;
    }

    public SubTypeReference parse( final JsonNode subTypeReferenceObj )
    {
        final SubTypeReference.Builder builder = SubTypeReference.newSubTypeReference();
        builder.name( JsonParserUtil.getStringValue( NAME, subTypeReferenceObj ) );
        builder.subType( new SubTypeQualifiedName( JsonParserUtil.getStringValue( REFERENCE, subTypeReferenceObj ) ) );
        builder.type( JsonParserUtil.getStringValue( TYPE, subTypeReferenceObj ) );
        return builder.build();
    }
}
