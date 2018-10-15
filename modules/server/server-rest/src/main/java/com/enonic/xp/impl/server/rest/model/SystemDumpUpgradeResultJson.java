package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.SystemDumpUpgradeResult;

public class SystemDumpUpgradeResultJson
{
    private final String initialVersion;

    private final String upgradedVersion;

    private SystemDumpUpgradeResultJson( final Builder builder )
    {
        this.initialVersion = builder.initialVersion;
        this.upgradedVersion = builder.upgradedVersion;
    }

    public static SystemDumpUpgradeResultJson from( final SystemDumpUpgradeResult result )
    {
        return SystemDumpUpgradeResultJson.create().
            initialVersion( result.getInitialVersion().toString() ).
            upgradedVersion( result.getUpgradedVersion().toString() ).
            build();
    }

    @SuppressWarnings("unused")
    public String getInitialVersion()
    {
        return initialVersion;
    }

    @SuppressWarnings("unused")
    public String getUpgradedVersion()
    {
        return upgradedVersion;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String initialVersion;

        private String upgradedVersion;

        private Builder()
        {
        }

        public Builder initialVersion( final String initialVersion )
        {
            this.initialVersion = initialVersion;
            return this;
        }

        public Builder upgradedVersion( final String upgradedVersion )
        {
            this.upgradedVersion = upgradedVersion;
            return this;
        }

        public SystemDumpUpgradeResultJson build()
        {
            return new SystemDumpUpgradeResultJson( this );
        }
    }
}
