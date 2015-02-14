package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.Permission;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public final class AccessControlEntryJson
{
    private final AccessControlEntry entry;

    private final Principal principal;

    public AccessControlEntryJson( final AccessControlEntry entry, final Principal principal )
    {
        this.entry = entry;
        this.principal = principal;
    }

    @JsonCreator
    public AccessControlEntryJson( @JsonProperty("principal") final PrincipalJson principal, //
                                   @JsonProperty("allow") final List<String> allow, //
                                   @JsonProperty("deny") final List<String> deny )
    {
        final AccessControlEntry.Builder builder = AccessControlEntry.create().
            principal( PrincipalKey.from( principal.getKey() ) );

        for ( final String permission : allow )
        {
            builder.allow( Permission.valueOf( permission ) );
        }

        for ( final String permission : deny )
        {
            builder.deny( Permission.valueOf( permission ) );
        }

        this.entry = builder.build();
        this.principal = null;
    }

    public PrincipalJson getPrincipal()
    {
        return new PrincipalJson( principal );
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
    public AccessControlEntry getSourceEntry()
    {
        return entry;
    }
}
