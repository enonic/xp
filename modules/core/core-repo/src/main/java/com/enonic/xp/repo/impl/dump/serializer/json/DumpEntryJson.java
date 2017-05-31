package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;

public class DumpEntryJson
{
    @JsonProperty("nodeId")
    private String nodeId;

    @JsonProperty("currentVersion")
    private MetaJson currentVersion;

    @JsonProperty("otherVersions")
    private Collection<MetaJson> otherVersions;

    public DumpEntryJson()
    {
    }

    public DumpEntryJson( final Collection<MetaJson> otherVersions, final MetaJson currentVersion, final String nodeId )
    {
        this.otherVersions = otherVersions;
        this.currentVersion = currentVersion;
        this.nodeId = nodeId;
    }

    public static DumpEntry fromJson( final DumpEntryJson json )
    {
        return DumpEntry.create().
            nodeId( NodeId.from( json.getNodeId() ) ).
            currentVersion( MetaJson.fromJson( json.currentVersion ) ).
            setVersions( json.getOtherVersions().stream().map( MetaJson::fromJson ).collect( Collectors.toList() ) ).
            build();
    }

    public static DumpEntryJson from( final DumpEntry dumpEntry )
    {
        String nodeId = dumpEntry.getNodeId().toString();
        MetaJson currentVersion = MetaJson.from( dumpEntry.getCurrentVersion() );
        Collection<MetaJson> otherVersions = dumpEntry.getOtherVersions().stream().map( MetaJson::from ).collect( Collectors.toList() );

        return new DumpEntryJson( otherVersions, currentVersion, nodeId );
    }

    public String getNodeId()
    {
        return nodeId;
    }

    public MetaJson getCurrentVersion()
    {
        return currentVersion;
    }

    public Collection<MetaJson> getOtherVersions()
    {
        return otherVersions;
    }


}
