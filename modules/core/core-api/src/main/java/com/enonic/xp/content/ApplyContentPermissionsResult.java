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
    private final Map<ContentId, List<BranchResult>> branchResults;


    private ApplyContentPermissionsResult( Builder builder )
    {
        this.branchResults = (ImmutableMap) builder.branchResults.build().asMap();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
    public ContentPaths getSucceedContents()
    {
        return branchResults.values()
            .stream()
            .filter( l -> l.get( 0 ).content != null )
            .map( l -> l.get( 0 ).content.getPath() )
            .collect( ContentPaths.collecting() );

    }

    @Deprecated
    public ContentPaths getSkippedContents()
    {
        return ContentPaths.empty();
    }

    public Map<ContentId, List<BranchResult>> getResults()
    {
        return branchResults;
    }

    public Content getResult( final ContentId contentId, final Branch branch )
    {
        final List<BranchResult> results = branchResults.get( contentId );
        return results != null ? branchResults.get( contentId )
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
        private final ImmutableListMultimap.Builder<ContentId, BranchResult> branchResults = ImmutableListMultimap.builder();

        private Builder()
        {
        }

        @Deprecated
        public Builder setSucceedContents( final ContentPaths succeedContents )
        {
            return this;
        }

        @Deprecated
        public Builder setSkippedContents( final ContentPaths skippedContents )
        {
            return this;
        }

        public Builder addBranchResult( ContentId contentId, Branch branch, Content content )
        {
            branchResults.put( contentId, new BranchResult( branch, content ) );
            return this;
        }

        public ApplyContentPermissionsResult build()
        {
            return new ApplyContentPermissionsResult( this );
        }
    }
}
