package com.enonic.wem.admin.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SnapshotRequestJson
{
    private String snapshotName;

    private boolean overwrite;

    @JsonCreator
    public SnapshotRequestJson( @JsonProperty("snapshotName") final String snapshotName, //
                                @JsonProperty("overwrite") final boolean overwrite )
    {
        this.snapshotName = snapshotName;
        this.overwrite = overwrite;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }
}
