package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class PermissionsFactory
{
    private static final AccessControlList DEFAULT_PERMISSIONS = AccessControlList.create().
        add( AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.ADMIN ).
            build() ).
        add( AccessControlEntry.create().
            principal( RoleKeys.EVERYONE ).
            allow( Permission.READ ).
            build() ).
        build();

    private final Iterable<PropertySet> permissions;

    public PermissionsFactory( final Iterable<PropertySet> permissions )
    {
        this.permissions = permissions;
    }

    public AccessControlList create()
    {
        if ( this.permissions == null || !this.permissions.iterator().hasNext() )
        {
            return DEFAULT_PERMISSIONS;
        }

        final AccessControlList.Builder builder = AccessControlList.create();

        permissions.forEach( ( permission ) -> {
            builder.add( createEntry( permission ) );
        } );

        return builder.build();
    }

    private AccessControlEntry createEntry( final PropertySet entry )
    {
        final AccessControlEntry.Builder builder = AccessControlEntry.create();

        builder.principal( PrincipalKey.from( entry.getString( "principal" ) ) );

        final Iterable<String> allow = entry.getStrings( "allow" );
        final Iterable<String> deny = entry.getStrings( "deny" );

        if ( !allow.iterator().hasNext() && deny.iterator().hasNext() )
        {
            builder.allowAll();
        }
        else
        {
            allow.forEach( ( permission ) -> builder.allow( Permission.valueOf( permission ) ) );
        }

        deny.forEach( ( permission ) -> builder.deny( Permission.valueOf( permission ) ) );

        return builder.build();
    }
}
