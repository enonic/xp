package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;

public class SystemDumpUpgradeResultJson
{
    private final String initialVersion;

    private final String upgradedVersion;

    private List<SystemDumpUpgradeStepResultJson> stepResults;

    private SystemDumpUpgradeResultJson( final Builder builder )
    {
        this.initialVersion = builder.initialVersion;
        this.upgradedVersion = builder.upgradedVersion;
        this.stepResults = builder.stepResults.build();
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

    @SuppressWarnings("unused")
    public List<SystemDumpUpgradeStepResultJson> getStepResults()
    {
        return stepResults;
    }

    public static SystemDumpUpgradeResultJson from( final DumpUpgradeResult result )
    {
        final Builder json = SystemDumpUpgradeResultJson.create().
            initialVersion( result.getInitialVersion().toString() ).
            upgradedVersion( result.getUpgradedVersion().toString() );
        result.getStepResults().stream().map( SystemDumpUpgradeStepResultJson::from ).
            forEach( json::stepResult );
        return json.build();
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String initialVersion;

        private String upgradedVersion;

        private ImmutableList.Builder<SystemDumpUpgradeStepResultJson> stepResults = ImmutableList.builder();

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

        public Builder stepResult( final SystemDumpUpgradeStepResultJson stepResult )
        {
            this.stepResults.add( stepResult );
            return this;
        }

        public SystemDumpUpgradeResultJson build()
        {
            return new SystemDumpUpgradeResultJson( this );
        }
    }

    @Override
    public String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }
}
