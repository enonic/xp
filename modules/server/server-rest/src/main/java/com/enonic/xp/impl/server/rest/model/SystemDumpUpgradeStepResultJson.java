package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.DumpUpgradeStepResult;

public class SystemDumpUpgradeStepResultJson
{
    private final String initialVersion;

    private final String upgradedVersion;

    private final String stepName;

    private final long processed;

    private final long errors;

    private final long warnings;

    private SystemDumpUpgradeStepResultJson( final Builder builder )
    {
        initialVersion = builder.initialVersion;
        upgradedVersion = builder.upgradedVersion;
        stepName = builder.stepName;
        processed = builder.processed;
        errors = builder.errors;
        warnings = builder.warnings;
    }

    public String getInitialVersion()
    {
        return initialVersion;
    }

    public String getUpgradedVersion()
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

    public static SystemDumpUpgradeStepResultJson from( final DumpUpgradeStepResult result )
    {
        return SystemDumpUpgradeStepResultJson.create().
            stepName( result.getStepName() ).
            initialVersion( result.getInitialVersion().toShortestString() ).
            upgradedVersion( result.getUpgradedVersion().toShortestString() ).
            processed( result.getProcessed() ).
            errors( result.getErrors() ).
            warnings( result.getWarnings() ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String initialVersion;

        private String upgradedVersion;

        private String stepName;

        private long processed;

        private long errors;

        private long warnings;

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

        public Builder errors( final long errors )
        {
            this.errors = errors;
            return this;
        }

        public Builder warnings( final long warnings )
        {
            this.warnings = warnings;
            return this;
        }

        public SystemDumpUpgradeStepResultJson build()
        {
            return new SystemDumpUpgradeStepResultJson( this );
        }
    }
}
