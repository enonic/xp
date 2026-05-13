package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

/**
 * v8 on-disk shape of a branch entry. Used only by {@link DumpUpgrader8to9} when reading
 * the input dump. The v9 form lives in {@code BranchDumpEntryJson} and carries only the
 * versionId reference.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model8BranchDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("meta")
    private VersionDumpEntryJson meta;

    @SuppressWarnings("unused")
    public Model8BranchDumpEntryJson()
    {
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public VersionDumpEntryJson getMeta()
    {
        return meta;
    }

    public void setMeta( final VersionDumpEntryJson meta )
    {
        this.meta = meta;
    }
}
