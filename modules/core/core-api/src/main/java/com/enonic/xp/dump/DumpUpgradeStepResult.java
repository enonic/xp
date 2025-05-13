package com.enonic.xp.dump;

import com.google.common.base.Preconditions;

import com.enonic.xp.util.Version;

public final class DumpUpgradeStepResult
{
    private final Version initialVersion;

    private final Version upgradedVersion;

    private final String stepName;

    private final long processed;

    private final long errors;

    private final long warnings;

    private DumpUpgradeStepResult( final Builder builder )
    {
        Preconditions.checkNotNull( builder.initialVersion, "initialVersion cannot be null" );
        Preconditions.checkNotNull( builder.upgradedVersion, "upgradedVersion cannot be null" );
        Preconditions.checkNotNull( builder.stepName, "stepName cannot be null" );
        initialVersion = builder.initialVersion;
        upgradedVersion = builder.upgradedVersion;
        stepName = builder.stepName;
        processed = builder.processed;
        errors = builder.errors;
        warnings = builder.warnings;
    }

    public Version getInitialVersion()
    {
        return initialVersion;
    }

    public Version getUpgradedVersion()
    {
        return upgradedVersion;
    }

    public String getStepName()
    {
        return stepName;
    }

    public long getProcessed()
    {
        return processed;
    }

    public long getErrors()
    {
        return errors;
    }

    public long getWarnings()
    {
        return warnings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Version initialVersion;

        private Version upgradedVersion;

        private String stepName;

        private long processed = 0L;

        private long errors = 0L;

        private long warnings = 0L;

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

        public Builder stepName( final String stepName )
        {
            this.stepName = stepName;
            return this;
        }

        public Builder processed( final long processed )
        {
            this.processed = processed;
            return this;
        }

        public Builder processed()
        {
            this.processed++;
            return this;
        }

        public Builder errors( final long errors )
        {
            this.errors = errors;
            return this;
        }

        public Builder error()
        {
            this.errors++;
            return this;
        }

        public Builder warnings( final long warnings )
        {
            this.warnings = warnings;
            return this;
        }

        public Builder warning()
        {
            this.warnings++;
            return this;
        }

        public DumpUpgradeStepResult build()
        {
            return new DumpUpgradeStepResult( this );
        }
    }
}
