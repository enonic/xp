package com.enonic.wem.repo.internal.entity.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

final class AccessControlEntryJson
{
    @JsonProperty("principal")
    private String principalKey;

    @JsonProperty("allow")
    private List<String> allowPermissions;

    @JsonProperty("deny")
    private List<String> denyPermissions;

    public AccessControlEntry fromJson()
    {
        final AccessControlEntry.Builder builder = AccessControlEntry.create().
            principal( PrincipalKey.from( this.principalKey ) );

        for ( final String permission : this.allowPermissions )
        {
            builder.allow( Permission.valueOf( permission ) );
        }

        for ( final String permission : this.denyPermissions )
        {
            builder.deny( Permission.valueOf( permission ) );
        }

        return builder.build();
    }

    public static AccessControlEntryJson toJson( final AccessControlEntry entry )
    {
        final AccessControlEntryJson json = new AccessControlEntryJson();
        json.principalKey = entry.getPrincipal().toString();
        json.allowPermissions = toStringList( entry.getAllowedPermissions() );
        json.denyPermissions = toStringList( entry.getDeniedPermissions() );
        return json;
    }

    private static List<String> toStringList( final Iterable<Permission> permissions )
    {
        return stream( permissions.spliterator(), false ).
            map( Permission::toString ).
            collect( toList() );
    }
}
