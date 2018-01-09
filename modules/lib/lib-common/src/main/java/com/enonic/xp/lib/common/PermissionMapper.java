package com.enonic.xp.lib.common;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.acl.UserStoreAccess;

public final class PermissionMapper
    implements MapSerializable
{
    private final Principal principal;

    private final UserStoreAccess access;

    public PermissionMapper( final Principal principal, final UserStoreAccess access )
    {
        this.principal = principal;
        this.access = access;
    }


    private void serialize( final MapGenerator gen, final Principal principal, final UserStoreAccess access )
    {
        serializePrincipal( gen, principal );
        gen.value( "access", access.toString() );
    }

    private void serializePrincipal( final MapGenerator gen, final Principal principal )
    {
        gen.map( "principal" );
        new PrincipalMapper( principal ).serialize( gen );
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.principal, this.access );
    }
}

