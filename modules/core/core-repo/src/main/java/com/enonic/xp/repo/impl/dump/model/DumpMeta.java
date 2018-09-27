package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.Version;
import com.enonic.xp.repo.impl.dump.DumpConstants;

public class DumpMeta
{
    private final String xpVersion;

    private final Instant timestamp;
    
    private final SystemDumpResult systemDumpResult;

    private final Version modelVersion;

    public DumpMeta( final Builder builder )
    {
        this.timestamp = builder.timestamp;
        this.xpVersion = builder.xpVersion;
        this.systemDumpResult = builder.systemDumpResult;
        this.modelVersion = builder.modelVersion;
    }

    public String getXpVersion()
    {
        return xpVersion;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public SystemDumpResult getSystemDumpResult()
    {
        return systemDumpResult;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Version getModelVersion()
    {
        return modelVersion;
    }

    public static class Builder
    {
        private String xpVersion;

        private Instant timestamp;

        private SystemDumpResult systemDumpResult;

        private Version modelVersion = DumpConstants.MODEL_VERSION;

        public Builder xpVersion( final String xpVersion )
        {
            this.xpVersion = xpVersion;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder systemDumpResult( final SystemDumpResult systemDumpResult )
        {
            this.systemDumpResult = systemDumpResult;
            return this;
        }

        public Builder modelVersion( final Version modelVersion )
        {
            this.modelVersion = modelVersion;
            return this;
        }

        public DumpMeta build()
        {
            return new DumpMeta( this );
        }
    }
}
