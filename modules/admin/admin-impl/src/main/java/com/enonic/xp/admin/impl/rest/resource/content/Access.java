package com.enonic.xp.admin.impl.rest.resource.content;


import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import com.enonic.xp.security.acl.Permission;

public enum Access
{
    READ( Permission.READ ),
    WRITE( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ),
    PUBLISH( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.PUBLISH ),
    FULL( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.PUBLISH, Permission.READ_PERMISSIONS,
          Permission.WRITE_PERMISSIONS ),
    CUSTOM();

    private final EnumSet<Permission> permissions;

    Access( final Permission... permissions )
    {
        this.permissions = permissions.length == 0 ? EnumSet.noneOf( Permission.class ) : EnumSet.copyOf( Arrays.asList( permissions ) );
    }

    public static Access fromPermissions( final Iterable<Permission> permissions )
    {
        final HashSet<Permission> perms = Sets.newHashSet( permissions );
        return Stream.of( READ, WRITE, PUBLISH, FULL ).
            filter( ( a ) -> a.hasPermissions( perms ) ).
            findFirst().
            orElse( CUSTOM );
    }

    private boolean hasPermissions( final Set<Permission> permissions )
    {
        return permissions.size() == this.permissions.size() && this.permissions.containsAll( permissions );
    }
}
