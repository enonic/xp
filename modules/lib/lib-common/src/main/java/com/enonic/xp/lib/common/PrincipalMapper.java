package com.enonic.xp.lib.common;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.User;

public final class PrincipalMapper
    implements MapSerializable
{
    private final Principal value;

    private final boolean detailed;

    public PrincipalMapper( final Principal value )
    {
        this( value, false );
    }

    public PrincipalMapper( final Principal value, final boolean detailed )
    {
        this.value = value;
        this.detailed = detailed;
    }


    private void serialize( final MapGenerator gen, final Principal value )
    {
        gen.value( "type", value.getClass().getSimpleName().toLowerCase() );
        gen.value( "key", value.getKey() );
        gen.value( "displayName", value.getDisplayName() );
        gen.value( "modifiedTime", value.getModifiedTime() );
        if ( value instanceof User )
        {
            final User user = (User) value;
            gen.value( "disabled", user.isDisabled() );
            gen.value( "email", user.getEmail() );
            gen.value( "login", user.getLogin() );
            gen.value( "idProvider", value.getKey() != null ? value.getKey().getIdProviderKey() : null );
            gen.value( "hasPassword", user.getAuthenticationHash() != null );
            serializeProfile( gen, user.getProfile() );
        }
        else
        {
            gen.value( "description", value.getDescription() );
        }
    }

    private void serializeProfile( final MapGenerator gen, final PropertyTree value )
    {
        if ( this.detailed )
        {
            gen.map( "profile" );
            new PropertyTreeMapper( value ).serialize( gen );
            gen.end();
        }
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

