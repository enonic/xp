package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ResolvePublishDependenciesResult
{
    private CompareContentResults compareContents;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.compareContents = builder.results.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds contentIds()
    {
        return ContentIds.from( compareContents.getCompareContentResultsMap().keySet() );
    }

    public CompareContentResults getCompareContents()
    {
        return compareContents;
    }

    public static final class Builder
    {
        private CompareContentResults.Builder results = CompareContentResults.create();

        private Builder()
        {
        }

        public Builder add( final CompareContentResult result )
        {
            this.results.add( result );
            return this;
        }

        public Builder addAll( final CompareContentResults compareContentResults )
        {
            this.results.addAll( compareContentResults );
            return this;
        }

        public ResolvePublishDependenciesResult build()
        {
            return new ResolvePublishDependenciesResult( this );
        }
    }

}
