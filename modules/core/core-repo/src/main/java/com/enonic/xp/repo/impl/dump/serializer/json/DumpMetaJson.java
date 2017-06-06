package com.enonic.xp.repo.impl.dump.serializer.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repo.impl.dump.model.DumpMeta;

public class DumpMetaJson
{
    @JsonProperty("xpVersion")
    private String xpVersion;

    @JsonProperty("timestamp")
    private String timestamp;

    public DumpMetaJson()
    {
    }

    public DumpMetaJson( final String xpVersion, final String timestamp )
    {
        this.xpVersion = xpVersion;
        this.timestamp = timestamp;
    }

    public static DumpMetaJson from( final DumpMeta dumpMeta )
    {
        return new DumpMetaJson( dumpMeta.getXpVersion(), dumpMeta.getTimestamp().toString() );
    }

    public static DumpMeta fromJson( final DumpMetaJson dumpMetaJson )
    {
        return new DumpMeta( dumpMetaJson.getXpVersion(), Instant.parse( dumpMetaJson.getTimestamp() ) );
    }

    private String getXpVersion()
    {
        return xpVersion;
    }

    public void setXpVersion( final String xpVersion )
    {
        this.xpVersion = xpVersion;
    }

    private String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( final String timestamp )
    {
        this.timestamp = timestamp;
    }
}
