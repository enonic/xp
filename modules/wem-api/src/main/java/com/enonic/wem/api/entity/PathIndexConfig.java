package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.DataPath;

public class PathIndexConfig
    implements Comparable<PathIndexConfig>
{
    private final PropertyIndexConfig propertyIndexConfig;

    private final DataPath path;

    private PathIndexConfig( final Builder builder )
    {
        this.propertyIndexConfig = builder.propertyIndexConfig;
        this.path = builder.path;
    }

    public PropertyIndexConfig getPropertyIndexConfig()
    {
        return propertyIndexConfig;
    }

    public DataPath getPath()
    {
        return path;
    }

    public static Builder newConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PropertyIndexConfig propertyIndexConfig;

        private DataPath path;

        public Builder propertyIndexConfig( final PropertyIndexConfig propertyIndexConfig )
        {
            this.propertyIndexConfig = propertyIndexConfig;
            return this;
        }

        public Builder path( final DataPath path )
        {
            this.path = path;
            return this;
        }

        public PathIndexConfig build()
        {
            return new PathIndexConfig( this );
        }
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

        final PathIndexConfig config = (PathIndexConfig) o;

        if ( !path.equals( config.path ) )
        {
            return false;
        }
        if ( !propertyIndexConfig.equals( config.propertyIndexConfig ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = propertyIndexConfig.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public int compareTo( final PathIndexConfig o )
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if ( this == o )
        {
            return EQUAL;
        }

        final int thisElementCount = this.path.elementCount();
        final int thatElementCount = o.path.elementCount();

        if ( thisElementCount < thatElementCount )
        {
            return AFTER;
        }
        if ( thisElementCount > thatElementCount )
        {
            return BEFORE;
        }

        return this.hashCode() - o.hashCode();
    }
}

