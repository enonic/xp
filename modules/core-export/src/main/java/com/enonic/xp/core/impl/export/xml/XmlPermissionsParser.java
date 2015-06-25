package com.enonic.xp.core.impl.export.xml;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.xml.DomElement;

public class XmlPermissionsParser
{
    public static AccessControlList parse( final DomElement root )
    {
        final AccessControlList.Builder builder = AccessControlList.create();

        final List<DomElement> aclEntries = root.getChildren( "principal" );

        for ( final DomElement aclEntry : aclEntries )
        {
            builder.add( parseACLEntry( aclEntry ) );
        }

        return builder.build();
    }

    private static AccessControlEntry parseACLEntry( final DomElement element )
    {
        final AccessControlEntry.Builder builder = AccessControlEntry.create();

        builder.principal( PrincipalKey.from( element.getAttribute( "key" ) ) );
        builder.allow( parsePermissions( element.getChild( "allow" ) ) );
        builder.deny( parsePermissions( element.getChild( "deny" ) ) );

        return builder.build();
    }

    private static Permission[] parsePermissions( final DomElement element )
    {
        final List<Permission> permissions = Lists.newArrayList();

        final List<DomElement> values = element.getChildren( "value" );

        for ( final DomElement value : values )
        {
            permissions.add( Permission.valueOf( value.getValue() ) );
        }

        return permissions.toArray( new Permission[permissions.size()] );
    }
}
