package com.enonic.xp.content;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

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

    public Content getResult( final ContentId contentId, final Branch branch )
    {
        final List<BranchResult> results = this.results.get( contentId );
        return results != null ? this.results.get( contentId )
            .stream()
            .filter( br -> br.getBranch().equals( branch ) )
            .map( BranchResult::getContent )
            .filter( Objects::nonNull )
            .findAny()
            .orElse( null ) : null;
    }

    public static final class BranchResult
    {
        private final Branch branch;

        private final Content content;

        public BranchResult( Branch branch, Content content )
        {
            this.branch = branch;
            this.content = content;
        }

        public Branch getBranch()
        {
            return branch;
        }

        public Content getContent()
        {
            return content;
        }

    }

    public static final class Builder
    {
        private final ImmutableListMultimap.Builder<ContentId, BranchResult> results = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        public Builder addResult( ContentId contentId, Branch branch, Content content )
        {
            results.put( contentId, new BranchResult( branch, content ) );
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
