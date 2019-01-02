package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pre2VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private Collection<Pre2VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public Pre2VersionsDumpEntryJson()
    {
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public Collection<Pre2VersionDumpEntryJson> getVersions()
    {
        return versions;
    }
}
