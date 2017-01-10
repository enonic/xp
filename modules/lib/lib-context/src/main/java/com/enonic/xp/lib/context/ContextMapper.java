package com.enonic.xp.lib.context;

import com.enonic.xp.context.Context;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class ContextMapper
    implements MapSerializable
{
    private final Context context;

    public ContextMapper( final Context context )
    {
        this.context = context;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "branch", this.context.getBranch().toString() );
        gen.value( "repository", this.context.getRepositoryId().toString() );
        serializeAuthInfo( gen, this.context.getAuthInfo() );
    }

    private void serializeAuthInfo( final MapGenerator gen, final AuthenticationInfo info )
    {
        if ( info == null )
        {
            return;
        }

        gen.map( "authInfo" );
        serializeUser( gen, info.getUser() );
        serializePrincipals( gen, info.getPrincipals() );
        gen.end();
    }

    private void serializeUser( final MapGenerator gen, final User user )
    {
        if ( user == null )
        {
            return;
        }

        gen.map( "user" );
        new PrincipalMapper( user ).serialize( gen );
        gen.end();
    }

    private void serializePrincipals( final MapGenerator gen, final PrincipalKeys keys )
    {
        if ( keys == null )
        {
            return;
        }

        gen.array( "principals" );
        for ( final PrincipalKey key : keys )
        {
            gen.value( key.toString() );
        }
        gen.end();
    }
}
