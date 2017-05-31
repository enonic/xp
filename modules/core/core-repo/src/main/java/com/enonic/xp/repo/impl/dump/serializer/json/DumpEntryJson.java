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

    @JsonProperty("versions")
    private Collection<MetaJson> versions;

    public DumpEntryJson()
    {
    }

    private DumpEntryJson( final Collection<MetaJson> versions, final String nodeId )
    {
        this.versions = versions;
        this.nodeId = nodeId;
    }

    public static DumpEntry fromJson( final DumpEntryJson json )
    {
        return DumpEntry.create().
            nodeId( NodeId.from( json.getNodeId() ) ).
            setVersions( json.getVersions().stream().map( MetaJson::fromJson ).collect( Collectors.toList() ) ).
            build();
    }

    public static DumpEntryJson from( final DumpEntry dumpEntry )
    {
        String nodeId = dumpEntry.getNodeId().toString();
        Collection<MetaJson> otherVersions = dumpEntry.getVersions().stream().map( MetaJson::from ).collect( Collectors.toList() );

        return new DumpEntryJson( otherVersions, nodeId );
    }

    private String getNodeId()
    {
        return nodeId;
    }


    private Collection<MetaJson> getVersions()
    {
        return versions;
    }


}
