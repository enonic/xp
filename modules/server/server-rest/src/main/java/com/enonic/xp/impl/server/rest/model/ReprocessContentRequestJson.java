package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public final class ReprocessContentRequestJson
{
    private final BranchContentPath sourceBranchPath;

    private final boolean skipChildren;

    @JsonCreator
    public ReprocessContentRequestJson( @JsonProperty("sourceBranchPath") final String sourceBranchPath,
                                        @JsonProperty("skipChildren") final boolean skipChildren )
    {
        Preconditions.checkNotNull( sourceBranchPath, "sourceBranchPath not specified" );

        this.sourceBranchPath = BranchContentPath.from( sourceBranchPath );
        this.skipChildren = skipChildren;
    }

    public BranchContentPath getSourceBranchPath()
    {
        return sourceBranchPath;
    }

    public boolean isSkipChildren()
    {
        return skipChildren;
    }
}
