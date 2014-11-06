package com.enonic.wem.core.entity.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

public final class AccessControlListJson
{
    protected final List<AccessControlEntryJson> entryList;

    private final AccessControlList acl;

    @JsonCreator
    public AccessControlListJson( @JsonProperty("accessControlList") final List<AccessControlEntryJson> accessControlListJson )
    {
        this.entryList = accessControlListJson;

        if ( accessControlListJson != null )
        {
            this.acl = buildAccessControlList( accessControlListJson );
        }
        else
        {
            acl = null;
        }
    }

    public AccessControlListJson( final AccessControlList acl )
    {
        this.acl = acl;
        this.entryList = createAccessControlEntryJsonList( acl );
    }

    private AccessControlList buildAccessControlList( final List<AccessControlEntryJson> accessControlEntryList )
    {
        final AccessControlList.Builder aclBuilder = AccessControlList.newACL();
        for ( final AccessControlEntryJson entryJson : accessControlEntryList )
        {
            aclBuilder.add( entryJson.getAccessControlEntry() );
        }
        return aclBuilder.build();
    }

    private List<AccessControlEntryJson> createAccessControlEntryJsonList( final AccessControlList acl )
    {
        if ( acl == null )
        {
            return null;
        }

        final List<AccessControlEntryJson> entryJsonList = Lists.newArrayList();
        for ( final AccessControlEntry entry : acl )
        {
            entryJsonList.add( new AccessControlEntryJson( entry ) );
        }
        return entryJsonList;
    }

    public List<AccessControlEntryJson> getAccessControlList()
    {
        return entryList;
    }

    @JsonIgnore
    public AccessControlList getAcl()
    {
        return acl;
    }
}



