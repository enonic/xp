package com.enonic.xp.content;

public class ResolveContentsToBePublishedCommandResult
{
    private final CompareContentResults compareContentResults;

    private final ContentIds requiredIds;

    private ResolveContentsToBePublishedCommandResult( final Builder builder )
    {
        this.compareContentResults = builder.compareContentResults.build();
        this.requiredIds = builder.requiredIds.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public CompareContentResults getCompareContentResults()
    {
        return compareContentResults;
    }

    public ContentIds getRequiredIds()
    {
        return requiredIds;
    }

    public static class Builder

    {
        private final CompareContentResults.Builder compareContentResults = CompareContentResults.create();

        private final ContentIds.Builder requiredIds = ContentIds.create();

        public Builder addCompareContentResult( final CompareContentResult result )
        {
            this.compareContentResults.add( result );
            return this;
        }

        public Builder addCompareContentResults( final CompareContentResults results )
        {
            this.compareContentResults.addAll( results );
            return this;
        }

        public Builder addRequiredContentId( final ContentId contentId )
        {
            this.requiredIds.add( contentId );
            return this;
        }

        public Builder addRequiredContentIds( final ContentIds contentIds )
        {
            this.requiredIds.addAll( contentIds );
            return this;
        }

        public ResolveContentsToBePublishedCommandResult build()
        {
            return new ResolveContentsToBePublishedCommandResult( this );
        }

    }
}
