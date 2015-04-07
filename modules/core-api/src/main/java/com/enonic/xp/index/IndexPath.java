package com.enonic.xp.index;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.Collections2;

import com.enonic.xp.data.Property;

@Beta
public class IndexPath
{
    private final String path;

    private IndexPath( final String path )
    {
        this.path = IndexFieldNameNormalizer.normalize( path );
    }

    public static IndexPath from( final String path )
    {
        return new IndexPath( path );
    }

    public static IndexPath from( final Property property )
    {
        return IndexPath.from( property.getPath().resetAllIndexesTo( 0 ).toString() );
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return this.path;
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

        final IndexPath indexPath = (IndexPath) o;

        if ( path != null ? !path.equalsIgnoreCase( indexPath.path ) : indexPath.path != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return path != null ? path.hashCode() : 0;
    }

    private static class IndexFieldNameNormalizer
    {
        private static final String FIELD_PATH_SEPARATOR = ".";

        private static final String INDEX_PATH_SEPARATOR = "_";

        public static String normalize( final String path )
        {
            return doNormalize( path );
        }

        private static String doNormalize( final String path )
        {
            String normalized = path;

            normalized = normalized.toLowerCase().trim();
            //normalized = normalized.replace( FIELD_PATH_SEPARATOR, INDEX_PATH_SEPARATOR );

            return normalized;
        }

        public static String[] normalize( final Collection<String> paths )
        {
            return Collections2.transform( paths, str -> doNormalize( str ) ).toArray( new String[paths.size()] );
        }
    }
}
