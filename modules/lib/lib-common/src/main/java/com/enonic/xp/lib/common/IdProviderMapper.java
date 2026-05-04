package com.enonic.xp.lib.common;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.IdProvider;

public final class IdProviderMapper
    implements MapSerializable
{
    private final IdProvider idProvider;

    public IdProviderMapper( final IdProvider idProvider )
    {
        this.idProvider = idProvider;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", idProvider.getKey() );
        gen.value( "displayName", idProvider.getDisplayName() );
        gen.value( "description", idProvider.getDescription() );
    }
}
