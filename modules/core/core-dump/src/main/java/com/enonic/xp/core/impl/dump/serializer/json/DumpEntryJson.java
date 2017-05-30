package com.enonic.xp.core.impl.dump.serializer.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.core.impl.dump.model.DumpEntry;

class DumpEntryJson
{
    private final String nodeId;

    private final MetaJson currentVersion;

    private final List<MetaJson> otherVersions;

    public DumpEntryJson( final DumpEntry dumpEntry )
    {
        this.nodeId = dumpEntry.getNodeId().toString();
        this.currentVersion = new MetaJson( dumpEntry.getCurrentVersion() );
        this.otherVersions = dumpEntry.getOtherVersions().stream().map( MetaJson::new ).collect( Collectors.toList() );
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public MetaJson getCurrentVersion()
    {
        return currentVersion;
    }

    public List<MetaJson> getOtherVersions()
    {
        return otherVersions;
    }
}
