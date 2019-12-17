package com.enonic.xp.dump;

public class VersionsLoadResult
    extends AbstractLoadResult
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
        extends AbstractLoadResult.Builder<VersionsLoadResult, Builder>
    {
        private Builder()
        {
            super();
        }

        @Override
        public VersionsLoadResult build()
        {
            super.build();
            return new VersionsLoadResult( this );
        }
    }
}
