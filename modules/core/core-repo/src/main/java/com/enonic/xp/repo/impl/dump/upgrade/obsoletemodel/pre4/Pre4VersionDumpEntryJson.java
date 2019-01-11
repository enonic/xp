package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre4VersionDumpEntryJson
{
    @JsonProperty("nodePath")
    private String nodePath;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("blobKey")
    private String blobKey;

    @JsonProperty("nodeState")
    private String nodeState;

    public Pre4VersionDumpEntryJson()
    {
    }

    private Pre4VersionDumpEntryJson( final Builder builder )
    {
        nodePath = builder.nodePath;
        timestamp = builder.timestamp;
        version = builder.version;
        blobKey = builder.blobKey;
        nodeState = builder.nodeState;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getNodePath()
    {
        return nodePath;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getVersion()
    {
        return version;
    }

    public String getBlobKey()
    {
        return blobKey;
    }

    public String getNodeState()
    {
        return nodeState;
    }

    public static final class Builder
    {
        private String nodePath;

        private String timestamp;

        private String version;

        private String blobKey;

        private String nodeState;

        private Builder()
        {
        }

        public Builder nodePath( final String nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder timestamp( final String timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder version( final String version )
        {
            this.version = version;
            return this;
        }

        public Builder blobKey( final String blobKey )
        {
            this.blobKey = blobKey;
            return this;
        }

        public Builder nodeState( final String nodeState )
        {
            this.nodeState = nodeState;
            return this;
        }

        public Pre4VersionDumpEntryJson build()
        {
            return new Pre4VersionDumpEntryJson( this );
        }
    }
}
