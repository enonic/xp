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
public final class AccessControlJson
{
    @JsonProperty("permissions")
    private List<AccessControlEntryJson> permissions;

    @JsonProperty("inheritPermissions")
    private boolean inheritPermissions;

    private AccessControlJson()
    {
    }

    private AccessControlJson( final Builder builder )
    {
        permissions = builder.permissions;
        inheritPermissions = builder.inheritPermissions;
    }

    public static AccessControlJson toJson( final NodeVersion nodeVersion )
    {
        return create().
            inheritPermissions( nodeVersion.isInheritPermissions() ).
            permissions( toJson( nodeVersion.getPermissions() ) ).
            build();
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

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private List<AccessControlEntryJson> permissions;

        private boolean inheritPermissions;

        private Builder()
        {
        }

        public Builder permissions( final List<AccessControlEntryJson> permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public AccessControlJson build()
        {
            return new AccessControlJson( this );
        }
    }
}
