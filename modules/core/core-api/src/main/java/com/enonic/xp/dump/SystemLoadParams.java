package com.enonic.xp.dump;

import java.util.Objects;

public class SystemLoadParams
{
    private final String dumpName;

    private final boolean includeVersions;

    private final boolean upgrade;

    private final boolean zip;

    private final SystemLoadListener listener;

    private SystemLoadParams( final Builder builder )
    {
        this.dumpName = builder.dumpName;
        this.includeVersions = builder.includeVersions;
        this.listener = builder.listener;
        this.upgrade = builder.upgrade;
        this.zip = builder.zip;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public SystemLoadListener getListener()
    {
        return listener;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }

    public boolean isZip()
    {
        return zip;
    }

    public static Builder create()
    {
        return new Builder();
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
        final SystemLoadParams that = (SystemLoadParams) o;
        return includeVersions == that.includeVersions && upgrade == that.upgrade && zip == that.zip &&
            Objects.equals( dumpName, that.dumpName ) && Objects.equals( listener, that.listener );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( dumpName, includeVersions, upgrade, listener, zip );
    }

    public static final class Builder
    {
        private String dumpName;

        private boolean includeVersions = false;

        private boolean upgrade = false;

        private boolean zip;

        private SystemLoadListener listener;

        private Builder()
        {
        }

        public Builder listener( final SystemLoadListener listener )
        {
            this.listener = listener;
            return this;
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

        public Builder upgrade( final boolean upgrade )
        {
            this.upgrade = upgrade;
            return this;
        }

        public Builder zip( final boolean zip )
        {
            this.zip = zip;
            return this;
        }

        public SystemLoadParams build()
        {
            return new SystemLoadParams( this );
        }
    }
}
