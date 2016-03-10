package com.enonic.xp.admin.impl.rest.resource.schema;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

public final class SchemaImageHelper
    extends BaseImageHelper
{
    private final byte[] defaultMixinImage;

    private final byte[] defaultRelationshipTypeImage;

    public SchemaImageHelper()
    {
        defaultMixinImage = loadDefaultImage( "mixin" );
        defaultRelationshipTypeImage = loadDefaultImage( "relationshiptype" );
    }

    public byte[] getDefaultMixinImage()
    {
        return defaultMixinImage;
    }

    public byte[] getDefaultRelationshipTypeImage()
    {
        return defaultRelationshipTypeImage;
    }
}
