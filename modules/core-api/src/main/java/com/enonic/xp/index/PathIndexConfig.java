package com.enonic.xp.index;

import com.enonic.xp.data.PropertyPath;

public class PathIndexConfig
    implements Comparable<PathIndexConfig>
{
    private final PropertyPath path;

    private final IndexConfig indexConfig;

    private PathIndexConfig( Builder builder )
    {
        path = builder.path;
        indexConfig = builder.indexConfig;
    }

    public boolean matches( final PropertyPath dataPath )
    {
        final String testPath = dataPath.resetAllIndexesTo( 0 ).toString();

        return testPath.startsWith( path.toString() );
    }

    public PropertyPath getPath()
    {
        return path;
    }

    public IndexConfig getIndexConfig()
    {
        return indexConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private PropertyPath path;

        private IndexConfig indexConfig;

        private Builder()
        {
        }

        public Builder path( PropertyPath path )
        {
            this.path = path;
            return this;
        }

        public Builder indexConfig( IndexConfig indexConfig )
        {
            this.indexConfig = indexConfig;
            return this;
        }

        public PathIndexConfig build()
        {
            return new PathIndexConfig( this );
        }
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PathIndexConfig ) )
        {
            return false;
        }

        final PathIndexConfig that = (PathIndexConfig) o;

        if ( indexConfig != null ? !indexConfig.equals( that.indexConfig ) : that.indexConfig != null )
        {
            return false;
        }
        if ( path != null ? !path.equals( that.path ) : that.path != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + ( indexConfig != null ? indexConfig.hashCode() : 0 );
        return result;
    }
}
