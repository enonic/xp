package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4;

import java.util.Collection;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre4VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private Collection<Pre4VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public Pre4VersionsDumpEntryJson()
    {
    }

    private Pre4VersionsDumpEntryJson( final Builder builder )
    {
        nodeId = builder.nodeId;
        versions = builder.versions;
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<Pre4VersionDumpEntryJson> getVersions()
    {
        return versions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String nodeId;

        private Collection<Pre4VersionDumpEntryJson> versions = new LinkedList<>();

        private Builder()
        {
        }

        public Builder nodeId( final String nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder version( final Pre4VersionDumpEntryJson version )
        {
            this.versions.add( version );
            return this;
        }

        public Pre4VersionsDumpEntryJson build()
        {
            return new Pre4VersionsDumpEntryJson( this );
        }
    }
}
