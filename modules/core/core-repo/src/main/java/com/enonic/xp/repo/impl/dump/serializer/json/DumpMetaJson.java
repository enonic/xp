package com.enonic.xp.repo.impl.dump.serializer.json;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.util.Version;

public class DumpMetaJson
{
    @JsonProperty("xpVersion")
    private String xpVersion;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("result")
    private Map<String, RepoDumpResultJson> result;

    @JsonProperty("modelVersion")
    private String modelVersion;

    @SuppressWarnings("unused")
    public DumpMetaJson()
    {

    }

    private DumpMetaJson( final Builder builder )
    {
        this.xpVersion = builder.xpVersion;
        this.timestamp = builder.timestamp;
        this.result = builder.result;
        this.modelVersion = builder.modelVersion.toShortestString();
    }

    public static DumpMetaJson from( final DumpMeta dumpMeta )
    {
        Map<String, RepoDumpResultJson> result = new HashMap<>();
        dumpMeta.getSystemDumpResult().forEach( repoDumpResult -> {
            final RepoDumpResultJson repoDumpResultJson = RepoDumpResultJson.from( repoDumpResult );
            result.put( repoDumpResult.getRepositoryId().toString(), repoDumpResultJson );
        } );

        return DumpMetaJson.create().
            xpVersion( dumpMeta.getXpVersion() ).
            modelVersion(dumpMeta.getModelVersion()).
            timestamp( dumpMeta.getTimestamp().toString() ).
            result(result ).
            build();
    }

    public static DumpMeta fromJson( final DumpMetaJson dumpMetaJson )
    {
        final DumpMeta.Builder dumpMeta = DumpMeta.create().
            xpVersion( dumpMetaJson.getXpVersion() ).
            timestamp( Instant.parse( dumpMetaJson.getTimestamp() ) );

        if ( dumpMetaJson.getResult() != null )
        {
            SystemDumpResult.Builder systemDumpResult = SystemDumpResult.create();
            dumpMetaJson.getResult().
                entrySet().
                forEach(
                    resultEntry -> systemDumpResult.add( RepoDumpResultJson.fromJson( resultEntry.getKey(), resultEntry.getValue() ) ) );
            dumpMeta.systemDumpResult( systemDumpResult.build() );
        }
        if ( !Strings.isNullOrEmpty( dumpMetaJson.getModelVersion() ) )
        {
            final Version modelVersion = Version.valueOf( dumpMetaJson.getModelVersion() );
            dumpMeta.modelVersion( modelVersion );
        }
        return dumpMeta.build();
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

    public static Builder create()
    {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public void setResult( final Map<String, RepoDumpResultJson> result )
    {
        this.result = result;
    }

    public String getModelVersion()
    {
        return modelVersion;
    }

    @SuppressWarnings("unused")
    public DumpMetaJson setModelVersion( final String modelVersion )
    {
        this.modelVersion = modelVersion;
        return this;
    }

    public static class Builder
    {
        private String xpVersion;

        private String timestamp;

        private Map<String, RepoDumpResultJson> result;

        private Version modelVersion = DumpConstants.MODEL_VERSION;

        private Builder()
        {
        }

        public Builder xpVersion( final String xpVersion )
        {
            this.xpVersion = xpVersion;
            return this;
        }

        public Builder timestamp( final String timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder result( final Map<String, RepoDumpResultJson> result )
        {
            this.result = result;
            return this;
        }

        public Builder modelVersion( final Version modelVersion )
        {
            this.modelVersion = modelVersion;
            return this;
        }

        public DumpMetaJson build()
        {
            return new DumpMetaJson( this );
        }
    }
}
