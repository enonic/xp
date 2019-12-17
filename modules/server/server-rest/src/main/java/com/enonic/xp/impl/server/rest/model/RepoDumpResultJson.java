package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.RepoDumpResult;

public class RepoDumpResultJson
{
    private final List<BranchDumpResultJson> branches;

    private final String repository;

    private final Long versions;

    private final List<DumpErrorJson> versionsErrors;

    private RepoDumpResultJson( final Builder builder )
    {
        this.branches = builder.branches;
        this.repository = builder.repository;
        this.versions = builder.versions;
        this.versionsErrors = builder.versionsErrors;
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        final Builder builder = RepoDumpResultJson.create().
            repository( repoDumpResult.getRepositoryId().toString() ).
            versions( repoDumpResult.getVersions() ).
            versionsErrors(repoDumpResult.getVersionsErrors().stream().map( DumpErrorJson::from ).collect( Collectors.toList() ) );

        for ( final BranchDumpResult result : repoDumpResult )
        {
            builder.add( BranchDumpResultJson.from( result ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public List<BranchDumpResultJson> getBranches()
    {
        return branches;
    }

    @SuppressWarnings("unused")
    public String getRepositoryId()
    {
        return repository;
    }

    @SuppressWarnings("unused")
    public Long getVersions()
    {
        return versions;
    }

    @SuppressWarnings("unused")
    public List<DumpErrorJson> getVersionsErrors()
    {
        return versionsErrors;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<BranchDumpResultJson> branches = new ArrayList<>();

        private List<DumpErrorJson> versionsErrors = new ArrayList<>();

        private String repository;

        private Long versions = 0L;

        private Builder()
        {
        }

        public Builder add( final BranchDumpResultJson val )
        {
            branches.add( val );
            return this;
        }

        public Builder repository( final String val )
        {
            repository = val;
            return this;
        }

        public Builder versions( final Long versions )
        {
            this.versions = versions;
            return this;
        }

        public Builder versionsErrors( final List<DumpErrorJson> versionsErrors )
        {
            this.versionsErrors = versionsErrors;
            return this;
        }

        public RepoDumpResultJson build()
        {
            return new RepoDumpResultJson( this );
        }
    }
}
