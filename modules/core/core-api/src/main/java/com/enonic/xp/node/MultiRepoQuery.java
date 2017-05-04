package com.enonic.xp.node;

public class MultiRepoQuery
    extends AbstractQuery
{
    private final SearchTargets searchTargets;

    private MultiRepoQuery( final Builder builder )
    {
        super( builder );
        searchTargets = builder.searchTargets;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private SearchTargets searchTargets;

        private Builder()
        {
        }

        public Builder searchTargets( final SearchTargets val )
        {
            searchTargets = val;
            return this;
        }

        public MultiRepoQuery build()
        {
            return new MultiRepoQuery( this );
        }
    }
}


