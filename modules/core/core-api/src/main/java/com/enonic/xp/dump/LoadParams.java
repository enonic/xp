package com.enonic.xp.dump;

public class LoadParams
{
    private final String dumpName;

    private final boolean includeVersions;

    private LoadParams( final Builder builder )
    {
        dumpName = builder.dumpName;
        includeVersions = builder.includeVersions;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private boolean includeVersions = false;

        private Builder()
        {
        }

        public Builder dumpName( final String val )
        {
            dumpName = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public LoadParams build()
        {
            return new LoadParams( this );
        }
    }
}
