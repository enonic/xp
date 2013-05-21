package com.enonic.wem.core.schema.content.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.content.form.MixinReference;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

class MixinReferenceJsonSerializer
    extends AbstractJsonSerializer<MixinReference>
{
    private static final String NAME = "name";

    private static final String REFERENCE = "reference";

    public static final String TYPE = "type";

    MixinReferenceJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    @Override
    protected JsonNode serialize( final MixinReference mixinReference )
    {
        final ObjectNode jsonObject = objectMapper().createObjectNode();
        jsonObject.put( NAME, mixinReference.getName() );
        jsonObject.put( REFERENCE, mixinReference.getQualifiedMixinName().toString() );
        jsonObject.put( TYPE, mixinReference.getMixinClass().getSimpleName() );
        return jsonObject;
    }

    public MixinReference parse( final JsonNode mixinReferenceObj )
    {
        final MixinReference.Builder builder = MixinReference.newMixinReference();
        builder.name( JsonSerializerUtil.getStringValue( NAME, mixinReferenceObj ) );
        builder.mixin( new QualifiedMixinName( JsonSerializerUtil.getStringValue( REFERENCE, mixinReferenceObj ) ) );
        builder.type( JsonSerializerUtil.getStringValue( TYPE, mixinReferenceObj ) );
        return builder.build();
    }
}
