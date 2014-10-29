package com.enonic.wem.admin.rest.resource.content.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.wem.api.content.ContentPermissions;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.acl.AccessControlEntry;

import static java.util.stream.StreamSupport.stream;

public final class ContentPermissionsJson
{
    private final ContentPermissions permissions;

    private final Principals principals;

    public ContentPermissionsJson( final ContentPermissions contentPermissions, final Principals principals )
    {
        this.permissions = contentPermissions;
        this.principals = principals;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return toJsonList( permissions.getPermissions() );
    }

    public List<AccessControlEntryJson> getInheritedPermissions()
    {
        return toJsonList( permissions.getInheritedPermissions() );
    }

    private List<AccessControlEntryJson> toJsonList( final Iterable<AccessControlEntry> aces )
    {
        return stream( aces.spliterator(), false ).
            map( ( ace ) -> new AccessControlEntryJson( ace, principals.getPrincipal( ace.getPrincipal() ) ) ).
            collect( Collectors.toList() );
    }
}
