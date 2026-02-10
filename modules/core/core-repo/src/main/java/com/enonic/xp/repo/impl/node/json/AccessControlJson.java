package com.enonic.xp.repo.impl.node.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AccessControlJson
{
    @JsonProperty("permissions")
    private List<AccessControlEntryJson> permissions;

    private AccessControlJson()
    {
    }

    private AccessControlJson( final Builder builder )
    {
        permissions = builder.permissions;
    }

    public static AccessControlJson toJson( final NodeStoreVersion nodeVersion )
    {
        return create().permissions( toJson( nodeVersion.permissions() ) ).build();
    }

    private static List<AccessControlEntryJson> toJson( final AccessControlList acl )
    {
        if ( acl == null )
        {
            return null;
        }

        final List<AccessControlEntryJson> entryJsonList = new ArrayList<>();
        for ( final AccessControlEntry entry : acl )
        {
            entryJsonList.add( AccessControlEntryJson.toJson( entry ) );
        }
        return entryJsonList;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static AccessControlList fromJson( final AccessControlJson json )
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( final AccessControlEntryJson entryJson : json.permissions )
        {
            builder.add( entryJson.fromJson() );
        }

        return builder.build();
    }

    public static final class Builder
    {
        private List<AccessControlEntryJson> permissions;

        private Builder()
        {
        }

        public Builder permissions( final List<AccessControlEntryJson> permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public AccessControlJson build()
        {
            return new AccessControlJson( this );
        }
    }
}
