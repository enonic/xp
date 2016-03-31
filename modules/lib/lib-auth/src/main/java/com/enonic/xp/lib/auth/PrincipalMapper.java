package com.enonic.xp.lib.auth;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.User;

public final class PrincipalMapper
    implements MapSerializable
{
    private final Principal value;

    public PrincipalMapper( final Principal value )
    {
        this.value = value;
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
            gen.value( "userStore", value.getKey() != null ? value.getKey().getUserStore() : null );
        }
        else
        {
            gen.value( "description", value.getDescription() );
        }
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

