package com.enonic.wem.admin.rest.resource.content.json;

import java.util.List;

import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.Permission;

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

}
