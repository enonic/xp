package com.enonic.xp.impl.server.rest.model;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class ReprocessContentRequestJson
{
    private final Branch branch;

    private final ContentPath contentPath;

    private final boolean skipChildren;

    @JsonCreator
    public ReprocessContentRequestJson( @JsonProperty("sourceBranchPath") final String sourceBranchPath,
                                        @JsonProperty("skipChildren") final boolean skipChildren )
    {
        Preconditions.checkNotNull( sourceBranchPath, "sourceBranchPath not specified" );

        final String[] elements = sourceBranchPath.split( Pattern.quote( ":" ), -1 );

        Preconditions.checkArgument( elements.length == 2, "Not a valid branch content path" );
        Preconditions.checkArgument( !isNullOrEmpty( elements[0] ), "Branch cannot be empty" );
        Preconditions.checkArgument( !isNullOrEmpty( elements[1] ), "ContentPath cannot be empty" );

        this.branch = Branch.from( elements[0] );
        this.contentPath = ContentPath.from( elements[1] );
        this.skipChildren = skipChildren;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public boolean isSkipChildren()
    {
        return skipChildren;
    }
}
