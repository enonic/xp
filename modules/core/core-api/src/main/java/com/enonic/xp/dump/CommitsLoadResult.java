package com.enonic.xp.dump;

public final class CommitsLoadResult
    extends AbstractLoadResult
{
    private CommitsLoadResult( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractLoadResult.Builder<CommitsLoadResult, Builder>
    {
        private Builder()
        {
            super();
        }

        @Override
        public CommitsLoadResult build()
        {
            return new CommitsLoadResult( this );
        }
    }
}
