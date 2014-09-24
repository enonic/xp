package com.enonic.wem.api.index;

import com.enonic.wem.api.data.DataPath;

public class PatternConfig
    implements Comparable<PatternConfig>
{
    private final DataPath path;

    private final IndexConfig indexConfig;

    private PatternConfig( Builder builder )
    {
        path = builder.path;
        indexConfig = builder.indexConfig;
    }

    public boolean matches( final DataPath dataPath )
    {
        if ( dataPath.startsWith( path ) )
        {
            return true;
        }

        return false;
    }

    public DataPath getPath()
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
        private DataPath path;

        private IndexConfig indexConfig;

        private Builder()
        {
        }

        public Builder path( DataPath path )
        {
            this.path = path;
            return this;
        }

        public Builder indexConfig( IndexConfig indexConfig )
        {
            this.indexConfig = indexConfig;
            return this;
        }

        public PatternConfig build()
        {
            return new PatternConfig( this );
        }
    }

    @Override
    public int compareTo( final PatternConfig o )
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
        if ( !( o instanceof PatternConfig ) )
        {
            return false;
        }

        final PatternConfig that = (PatternConfig) o;

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
