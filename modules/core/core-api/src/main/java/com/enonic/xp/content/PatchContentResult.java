package com.enonic.xp.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class PatchContentResult
{
    private final ContentId contentId;

    private final List<BranchResult> results;

    private PatchContentResult( Builder builder )
    {
        this.contentId = builder.contentId;
        this.results = builder.results.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public List<BranchResult> getResults()
    {
        return results;
    }

    public Content getResult( final Branch branch )
    {
        return results.stream().filter( br -> br.branch.equals( branch ) ).findAny().map( BranchResult::content ).orElse( null );
    }

    public record BranchResult(Branch branch, Content content)
    {

    }

    public static final class Builder
    {
        private final ImmutableList.Builder<BranchResult> results = ImmutableList.builder();

        private ContentId contentId;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder addResult( Branch branch, Content content )
        {
            results.add( new BranchResult( branch, content ) );
            return this;
        }

        public PatchContentResult build()
        {
            return new PatchContentResult( this );
        }
    }
}
