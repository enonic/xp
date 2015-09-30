package com.enonic.xp.lib.auth;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.User;

final class UserMapper
    implements MapSerializable
{
    private final User value;

    public UserMapper( final User value )
    {
        this.value = value;
    }

    private void serialize( final MapGenerator gen, final User value )
    {
        gen.value( "disabled", value.isDisabled() );
        gen.value( "displayName", value.getDisplayName() );
        gen.value( "email", value.getEmail() );
        gen.value( "key", value.getKey() );
        gen.value( "login", value.getLogin() );
        gen.value( "userStore", value.getKey() != null ? value.getKey().getUserStore() : null );
        gen.value( "modifiedTime", value.getModifiedTime() );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

