package com.enonic.xp.dump;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.util.Version;

public final class DumpUpgradeResult
{
    private final String dumpName;

    private final Version initialVersion;

    private final Version upgradedVersion;

    private final List<DumpUpgradeStepResult> stepResults;

    private DumpUpgradeResult( final Builder builder )
    {
        this.dumpName = builder.dumpName;
        this.initialVersion = builder.initialVersion;
        this.upgradedVersion = builder.upgradedVersion;
        this.stepResults = builder.stepResults.build();
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public Version getInitialVersion()
    {
        return initialVersion;
    }

    public Version getUpgradedVersion()
    {
        return upgradedVersion;
    }

    public List<DumpUpgradeStepResult> getStepResults()
    {
        return stepResults;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private Version initialVersion;

        private Version upgradedVersion;

        private final ImmutableList.Builder<DumpUpgradeStepResult> stepResults = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder dumpName( final String dumpName )
        {
            this.dumpName = dumpName;
            return this;
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

        public Builder stepResult( final DumpUpgradeStepResult stepResult )
        {
            this.stepResults.add( stepResult );
            return this;
        }

        public DumpUpgradeResult build()
        {
            return new DumpUpgradeResult( this );
        }
    }
}
