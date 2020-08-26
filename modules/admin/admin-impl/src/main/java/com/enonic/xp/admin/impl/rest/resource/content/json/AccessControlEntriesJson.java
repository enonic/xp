package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.security.Principals;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.support.AbstractImmutableEntityList;

public final class AccessControlEntriesJson
    extends AbstractImmutableEntityList<AccessControlEntryJson>
{
    private AccessControlEntriesJson( final ImmutableList<AccessControlEntryJson> list )
    {
        super( list );
    }

    public static AccessControlEntriesJson empty()
    {
        return new AccessControlEntriesJson( ImmutableList.of() );
    }

    public static AccessControlEntriesJson from( final AccessControlList acl, final Principals principals )
    {
        final ImmutableList.Builder<AccessControlEntryJson> jsonList = ImmutableList.builder();
        for ( AccessControlEntry entry : acl )
        {
            if ( principals.getPrincipal( entry.getPrincipal() ) != null )
            {
                jsonList.add( new AccessControlEntryJson( entry, principals.getPrincipal( entry.getPrincipal() ) ) );
            }
        }
        return new AccessControlEntriesJson( jsonList.build() );
    }
}
