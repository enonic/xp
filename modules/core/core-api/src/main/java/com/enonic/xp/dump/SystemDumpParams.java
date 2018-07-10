package com.enonic.xp.dump;

import java.util.Objects;

public class SystemDumpParams
{
    private final String dumpName;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final SystemDumpListener listener;

    private SystemDumpParams( final Builder builder )
    {
        dumpName = builder.dumpName;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        maxAge = builder.maxAge;
        maxVersions = builder.maxVersions;
        this.listener = builder.listener;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public boolean isIncludeBinaries()
    {
        return includeBinaries;
    }

    public Integer getMaxAge()
    {
        return maxAge;
    }

    public Integer getMaxVersions()
    {
        return maxVersions;
    }

    public SystemDumpListener getListener()
    {
        return listener;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final SystemDumpParams that = (SystemDumpParams) o;
        return includeVersions == that.includeVersions && includeBinaries == that.includeBinaries &&
            Objects.equals( dumpName, that.dumpName ) && Objects.equals( maxAge, that.maxAge ) &&
            Objects.equals( maxVersions, that.maxVersions ) && Objects.equals( listener, that.listener );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( dumpName, includeVersions, includeBinaries, maxAge, maxVersions, listener );
    }

    public static final class Builder
    {
        private String dumpName;

        private boolean includeVersions = true;

        private boolean includeBinaries = true;

        private Integer maxAge;

        private Integer maxVersions;

        private SystemDumpListener listener;

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

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public Builder maxAge( final Integer val )
        {
            maxAge = val;
            return this;
        }

        public Builder maxVersions( final Integer val )
        {
            maxVersions = val;
            return this;
        }

        public Builder listener( final SystemDumpListener listener )
        {
            this.listener = listener;
            return this;
        }

        public SystemDumpParams build()
        {
            return new SystemDumpParams( this );
        }
    }
}
