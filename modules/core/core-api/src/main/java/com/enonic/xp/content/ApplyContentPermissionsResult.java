package com.enonic.xp.content;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class ApplyContentPermissionsResult
{
    private final Map<ContentId, List<BranchResult>> results;

    private ApplyContentPermissionsResult( Builder builder )
    {
        this.results = (ImmutableMap) builder.results.build().asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<ContentId, List<BranchResult>> getResults()
    {
        return results;
    }

    public AccessControlList getResult( final ContentId contentId, final Branch branch )
    {
        final List<BranchResult> results = this.results.get( contentId );
        return results != null ? this.results.get( contentId )
            .stream()
            .filter( br -> br.branch().equals( branch ) )
            .map( BranchResult::content )
            .filter( Objects::nonNull )
            .findAny()
            .orElse( null ) : null;
    }

    public record BranchResult(Branch branch, AccessControlList content)
    {
    }

    public static final class Builder
    {
        private final ImmutableListMultimap.Builder<ContentId, BranchResult> results = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        public Builder addResult( ContentId contentId, Branch branch, AccessControlList permissions )
        {
            results.put( contentId, new BranchResult( branch, permissions ) );
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
