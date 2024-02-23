package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.Content;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;

public final class PermissionsMapper
    implements MapSerializable
{

    private final Content content;

    public PermissionsMapper( final Content content )
    {
        this.content = content;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        if ( !content.getPermissions().isEmpty() )
        {
            gen.array( "permissions" );
            for ( AccessControlEntry accessControlEntry : content.getPermissions() )
            {
                gen.map();
                serialize( gen, accessControlEntry );
                gen.end();
            }
            gen.end();
        }
    }

    private void serialize( final MapGenerator gen, final AccessControlEntry accessControlEntry )
    {
        gen.value( "principal", accessControlEntry.getPrincipal().toString() );

        gen.array( "allow" );
        for ( Permission permission : accessControlEntry.getAllowedPermissions() )
        {
            gen.value( permission.toString() );
        }
        gen.end();

        gen.array( "deny" );
        for ( Permission permission : accessControlEntry.getDeniedPermissions() )
        {
            gen.value( permission.toString() );
        }
        gen.end();
    }
}
