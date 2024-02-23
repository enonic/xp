package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.lib.node.NodePropertyConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;


final class PermissionsMapper
    implements MapSerializable
{
    private final Node node;

    PermissionsMapper( final Node node )
    {
        this.node = node;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        if ( !node.getPermissions().isEmpty() )
        {
            gen.array( NodePropertyConstants.PERMISSIONS );
            for ( AccessControlEntry accessControlEntry : node.getPermissions() )
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
