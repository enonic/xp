package com.enonic.wem.core.content.type.form;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.type.form.MixinReference;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonSerializerUtil;

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
