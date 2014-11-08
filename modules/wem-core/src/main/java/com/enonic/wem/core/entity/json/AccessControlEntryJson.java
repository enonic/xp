package com.enonic.wem.core.entity.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.Permission;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@SuppressWarnings("UnusedDeclaration")
public final class AccessControlEntryJson
{
    private final AccessControlEntry entry;

    public AccessControlEntryJson( final AccessControlEntry entry )
    {
        this.entry = entry;
    }

    @JsonCreator
    public AccessControlEntryJson( @JsonProperty("principal") final String principalKey,
                                   @JsonProperty("allow") final List<String> allowPermissions,
                                   @JsonProperty("deny") final List<String> denyPermissions )
    {
        final AccessControlEntry.Builder entryBuilder = AccessControlEntry.create().
            principal( PrincipalKey.from( principalKey ) );

        for ( final String permission : allowPermissions )
        {
            entryBuilder.allow( Permission.valueOf( permission ) );
        }
        for ( final String permission : denyPermissions )
        {
            entryBuilder.deny( Permission.valueOf( permission ) );
        }

        this.entry = entryBuilder.build();
    }

    public String getPrincipal()
    {
        return entry.getPrincipal().toString();
    }

    public List<String> getAllow()
    {
        return toStringList( entry.getAllowedPermissions() );
    }

    public List<String> getDeny()
    {
        return toStringList( entry.getDeniedPermissions() );
    }

    private List<String> toStringList( final Iterable<Permission> permissions )
    {
        return stream( permissions.spliterator(), false ).
            map( Permission::toString ).
            collect( toList() );
    }

    @JsonIgnore
    public AccessControlEntry getAccessControlEntry()
    {
        return entry;
    }
}
