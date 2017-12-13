package com.enonic.xp.repo.impl.dump.serializer.json;


import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.repository.RepositoryId;

public class RepoDumpResultJson
{
    @JsonProperty
    private Long versions;

    @SuppressWarnings("unused")
    public RepoDumpResultJson()
    {
    }

    private RepoDumpResultJson( final Long versions )
    {
        this.versions = versions;
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        return new RepoDumpResultJson( repoDumpResult.getVersions() );
    }

    public static RepoDumpResult fromJson( final String repositoryId, RepoDumpResultJson json )
    {
        return RepoDumpResult.create( RepositoryId.from( repositoryId ) ).
            versions( json.versions ).
            build();
    }
}
