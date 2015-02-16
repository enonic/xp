package com.enonic.wem.repo.internal.index;

import java.util.Collection;

import com.google.common.collect.Collections2;

public class IndexFieldNameNormalizer
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
       // normalized = normalized.replace( FIELD_PATH_SEPARATOR, INDEX_PATH_SEPARATOR );

        return normalized;
    }

    public static String[] normalize( final Collection<String> paths )
    {
        return Collections2.transform( paths, str -> doNormalize( str ) ).toArray( new String[paths.size()] );
    }

}
