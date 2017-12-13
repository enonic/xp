package com.enonic.xp.repo.impl.dump.serializer.json;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;

public class DumpMetaJson
{
    @JsonProperty("xpVersion")
    private String xpVersion;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("result")
    private Map<String, RepoDumpResultJson> result;

    @SuppressWarnings("unused")
    public DumpMetaJson()
    {
    }

    private DumpMetaJson( final String xpVersion, final String timestamp, final Map<String, RepoDumpResultJson> result )
    {
        this.xpVersion = xpVersion;
        this.timestamp = timestamp;
        this.result = result;
    }

    public static DumpMetaJson from( final DumpMeta dumpMeta )
    {
        Map<String, RepoDumpResultJson> result = new HashMap<>();
        dumpMeta.getSystemDumpResult().forEach( repoDumpResult -> {
            final RepoDumpResultJson repoDumpResultJson = RepoDumpResultJson.from( repoDumpResult );
            result.put( repoDumpResult.getRepositoryId().toString(), repoDumpResultJson);
        } );

        return new DumpMetaJson( dumpMeta.getXpVersion(), dumpMeta.getTimestamp().toString(), result );
    }

    public static DumpMeta fromJson( final DumpMetaJson dumpMetaJson )
    {
        SystemDumpResult result = null;
        if ( dumpMetaJson.getResult() != null )
        {
            SystemDumpResult.Builder resultBuilder = SystemDumpResult.create();
            dumpMetaJson.getResult().entrySet().forEach(
                resultEntry -> resultBuilder.add( RepoDumpResultJson.fromJson( resultEntry.getKey(), resultEntry.getValue() ) ) );
            result = resultBuilder.build();
        }
        return new DumpMeta( dumpMetaJson.getXpVersion(), Instant.parse( dumpMetaJson.getTimestamp() ), result );
    }

    private String getXpVersion()
    {
        return xpVersion;
    }

    @SuppressWarnings("unused")
    public void setXpVersion( final String xpVersion )
    {
        this.xpVersion = xpVersion;
    }

    private String getTimestamp()
    {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public void setTimestamp( final String timestamp )
    {
        this.timestamp = timestamp;
    }

    public Map<String, RepoDumpResultJson> getResult()
    {
        return result;
    }

    public void setResult( final Map<String, RepoDumpResultJson> result )
    {
        this.result = result;
    }
}
