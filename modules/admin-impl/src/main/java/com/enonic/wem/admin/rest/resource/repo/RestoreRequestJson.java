package com.enonic.wem.admin.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RestoreRequestJson
{

    private String snapshotName;

    @JsonCreator
    public RestoreRequestJson( @JsonProperty("snapshotName") final String snapshotName )
    {
        this.snapshotName = snapshotName;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }


}
