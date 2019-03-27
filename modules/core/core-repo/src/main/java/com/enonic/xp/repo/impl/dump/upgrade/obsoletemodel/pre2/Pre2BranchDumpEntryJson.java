package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre2BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private Pre2VersionDumpEntryJson meta;

    @JsonProperty("binaries")
    private Collection<String> binaries;

    @SuppressWarnings("unused")
    public Pre2BranchDumpEntryJson()
    {
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<String> getBinaries()
    {
        return binaries;
    }

    public Pre2VersionDumpEntryJson getMeta()
    {
        return meta;
    }
}
