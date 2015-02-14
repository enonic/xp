package com.enonic.xp.admin.impl.rest.resource.schema;

import java.awt.image.BufferedImage;

import com.enonic.xp.admin.impl.rest.resource.BaseImageHelper;

public final class SchemaImageHelper
    extends BaseImageHelper
{
    private final BufferedImage defaultMixinImage;

    private final BufferedImage defaultRelationshipTypeImage;

    public SchemaImageHelper()
    {
        defaultMixinImage = loadDefaultImage( "mixin" );
        defaultRelationshipTypeImage = loadDefaultImage( "relationshiptype" );
    }

    public BufferedImage getDefaultMixinImage( final int size )
    {
        return resizeImage( defaultMixinImage, size );
    }

    public BufferedImage getDefaultRelationshipTypeImage( final int size )
    {
        return resizeImage( defaultRelationshipTypeImage, size );
    }

}
