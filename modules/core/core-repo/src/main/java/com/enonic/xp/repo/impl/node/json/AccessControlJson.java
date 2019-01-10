package com.enonic.xp.repo.impl.node.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@JsonIgnoreProperties(ignoreUnknown = true)
final class AccessControlJson
{
    @JsonProperty("permissions")
    private List<AccessControlEntryJson> permissions;

    @JsonProperty("inheritPermissions")
    private boolean inheritPermissions;

    public static AccessControlJson toJson( final NodeVersion nodeVersion )
    {
        final AccessControlJson json = new AccessControlJson();
        json.permissions = toJson( nodeVersion.getPermissions() );
        json.inheritPermissions = nodeVersion.isInheritPermissions();
        return json;
    }

    private static List<AccessControlEntryJson> toJson( final AccessControlList acl )
    {
        if ( acl == null )
        {
            return null;
        }

        final List<AccessControlEntryJson> entryJsonList = Lists.newArrayList();
        for ( final AccessControlEntry entry : acl )
        {
            entryJsonList.add( AccessControlEntryJson.toJson( entry ) );
        }
        return entryJsonList;
    }

    @JsonIgnore
    public AccessControlList getAccessControlList()
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( final AccessControlEntryJson entryJson : permissions )
        {
            builder.add( entryJson.fromJson() );
        }

        return builder.build();
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }
}
