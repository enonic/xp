package com.enonic.xp.dump;

public class VersionsLoadResult
    extends LoadResult<VersionsLoadResult>
{
    private VersionsLoadResult( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends LoadResult.Builder<VersionsLoadResult, Builder>
    {
        private Builder()
        {
            super();
        }

        public VersionsLoadResult build()
        {
            super.build();
            return new VersionsLoadResult( this );
        }
    }
}
