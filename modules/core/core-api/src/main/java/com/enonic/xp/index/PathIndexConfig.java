package com.enonic.xp.index;

import java.util.Comparator;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyPath;

@PublicApi
public final class PathIndexConfig
    implements Comparable<PathIndexConfig>
{
    public static final Comparator<PathIndexConfig> COMPARATOR =
        Comparator.comparing( ( PathIndexConfig pathIndexConfig ) -> pathIndexConfig.path.toString() )
            .thenComparing( ( PathIndexConfig pathIndexConfig ) -> pathIndexConfig.indexConfig )
            .reversed();

    private final IndexPath path;

    private final IndexConfig indexConfig;

    private PathIndexConfig( Builder builder )
    {
        path = builder.path;
        indexConfig = builder.indexConfig;
    }

    public boolean matches( final String testPath )
    {
        return testPath.startsWith( path.toString() );
    }

    public IndexPath getIndexPath()
    {
        return path;
    }

    @Deprecated
    public PropertyPath getPath()
    {
        return PropertyPath.from( path.getPath() );
    }

    public IndexConfig getIndexConfig()
    {
        return indexConfig;
    }

    public static Builder create()
    {
        return new Builder();
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

        final int thisElementCount = countPathElements( this.path.getPath() );
        final int thatElementCount = countPathElements( o.path.getPath() );

        if ( thisElementCount < thatElementCount )
        {
            return AFTER;
        }
        if ( thisElementCount > thatElementCount )
        {
            return BEFORE;
        }

        return COMPARATOR.compare( this, o );
    }

    private static int countPathElements( final String path )
    {
        if ( path.isEmpty() )
        {
            return 1; // empty string split by "\\." returns array of length 1
        }

        int count = 1;
        for ( int i = 0; i < path.length(); i++ )
        {
            if ( path.charAt( i ) == '.' )
            {
                count++;
            }
        }
        return count;
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

        if ( !Objects.equals( indexConfig, that.indexConfig ) )
        {
            return false;
        }
        return Objects.equals( path, that.path );
    }

    public static final class Builder
    {
        private IndexPath path;

        private IndexConfig indexConfig;

        private Builder()
        {
        }

        public Builder path( IndexPath path )
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
    public int hashCode()
    {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + ( indexConfig != null ? indexConfig.hashCode() : 0 );
        return result;
    }
}
