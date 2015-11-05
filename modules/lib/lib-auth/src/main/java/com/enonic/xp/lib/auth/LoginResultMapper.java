package com.enonic.xp.lib.auth;


import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class LoginResultMapper
    implements MapSerializable
{
    private final AuthenticationInfo value;

    private final String message;

    public LoginResultMapper( final AuthenticationInfo value )
    {
        this.value = value;
        this.message = null;
    }

    public LoginResultMapper( final AuthenticationInfo value, final String message )
    {
        this.value = value;
        this.message = message;
    }

    private void serialize( final MapGenerator gen, final AuthenticationInfo value )
    {
        gen.value( "authenticated", value.isAuthenticated() );
        gen.value( "message", message );
        serializeUser( gen, value.getUser() );
    }

    private void serializeUser( final MapGenerator gen, final User value )
    {
        if ( value == null )
        {
            return;
        }
        gen.map( "user" );
        new PrincipalMapper( value ).serialize( gen );
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}

