package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre2VersionDumpEntryJson
{
    @JsonProperty("nodePath")
    private String nodePath;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("version")
    private String version;

    @JsonProperty("nodeState")
    private String nodeState;

    public Pre2VersionDumpEntryJson()
    {
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

    public String getNodeState()
    {
        return nodeState;
    }
}
