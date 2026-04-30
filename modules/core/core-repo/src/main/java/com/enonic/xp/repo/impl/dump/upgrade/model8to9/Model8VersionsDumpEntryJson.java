package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

/**
 * v8 on-disk shape of a versions entry — one file per node containing all of the node's versions
 * wrapped in {@code {nodeId, versions:[...]}}. Used only by {@link DumpUpgrader8to9} when reading
 * the input dump. The v9 form is a stream of NDJSON lines, each line being a self-contained
 * {@link VersionDumpEntryJson} (with its own {@code nodeId}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model8VersionsDumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("versions")
    private List<VersionDumpEntryJson> versions;

    @SuppressWarnings("unused")
    public Model8VersionsDumpEntryJson()
    {
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public List<VersionDumpEntryJson> getVersions()
    {
        return versions;
    }
}
