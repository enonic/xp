package com.enonic.xp.content;

public class ResolveContentsToBePublishedCommandResult
{
    private final CompareContentResults compareContentResults;

    private final Boolean containsOffline;

    public ResolveContentsToBePublishedCommandResult( final Builder builder )
    {
        this.compareContentResults = builder.compareContentResults.build();
        this.containsOffline = builder.containsOffline;
    }

    public CompareContentResults getCompareContentResults()
    {
        return compareContentResults;
    }

    public Boolean getContainsOffline()
    {
        return containsOffline;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder

    {
        private CompareContentResults.Builder compareContentResults = CompareContentResults.create();

        private Boolean containsOffline = false;

        public Builder addCompareContentResult( final CompareContentResult compareContentResult )
        {
            this.compareContentResults.add( compareContentResult );
            return this;
        }

        public Builder addCompareContentResults( final CompareContentResults compareContentResults )
        {
            this.compareContentResults.addAll( compareContentResults );
            return this;
        }

        public Builder setContainsOffline( final Boolean containsOffline )
        {
            this.containsOffline = containsOffline;
            return this;
        }

        public ResolveContentsToBePublishedCommandResult build()
        {
            return new ResolveContentsToBePublishedCommandResult( this );
        }

    }
}
