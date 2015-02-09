package com.enonic.wem.admin.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class RestoreRequestJson
{
    private String snapshotName;

    @JsonCreator
    public RestoreRequestJson( @JsonProperty("snapshotName") final String snapshotName )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );

        this.snapshotName = snapshotName.toLowerCase();
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }


}
