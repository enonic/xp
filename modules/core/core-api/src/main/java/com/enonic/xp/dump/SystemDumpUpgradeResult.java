package com.enonic.xp.dump;

import com.enonic.xp.util.Version;

public class SystemDumpUpgradeResult
{
    private Version initialVersion;

    private Version upgradedVersion;

    public SystemDumpUpgradeResult( final Builder builder )
    {
        this.initialVersion = builder.initialVersion;
        this.upgradedVersion = builder.upgradedVersion;
    }

    public Version getInitialVersion()
    {
        return initialVersion;
    }

    public Version getUpgradedVersion()
    {
        return upgradedVersion;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Version initialVersion;

        private Version upgradedVersion;

        private Builder()
        {
        }

        public Builder initialVersion( final Version initialVersion )
        {
            this.initialVersion = initialVersion;
            return this;
        }

        public Builder upgradedVersion( final Version upgradedVersion )
        {
            this.upgradedVersion = upgradedVersion;
            return this;
        }

        public SystemDumpUpgradeResult build()
        {
            return new SystemDumpUpgradeResult( this );
        }
    }
}
