package com.enonic.xp.lib.common;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;

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
        serializeIdProviderConfig( gen, idProvider.getIdProviderConfig() );
    }

    private void serializeIdProviderConfig( final MapGenerator gen, final IdProviderConfig config )
    {
        if ( config == null )
        {
            return;
        }
        gen.map( "idProviderConfig" );
        gen.value( "applicationKey", config.getApplicationKey() );
        gen.map( "config" );
        new PropertyTreeMapper( config.getConfig() ).serialize( gen );
        gen.end();
        gen.end();
    }
}
