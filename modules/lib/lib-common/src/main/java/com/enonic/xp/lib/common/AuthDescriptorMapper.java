package com.enonic.xp.lib.common;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.form.Form;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class AuthDescriptorMapper
    implements MapSerializable
{
    private final AuthDescriptor value;

    public AuthDescriptorMapper( final AuthDescriptor value )
    {
        this.value = value;
    }


    private void serialize( final MapGenerator gen, final AuthDescriptor value )
    {
        gen.value( "key", value.getKey() );
        if ( value.getMode() != null )
        {
            gen.value( "mode", value.getMode().toString() );
        }
        serializeConfig( gen, value.getConfig() );
    }

    private void serializeConfig( final MapGenerator gen, final Form value )
    {
        gen.map( "config" );
        // TODO: Implement form mapper
        // new FormMapper( value ).serialize( gen );
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

