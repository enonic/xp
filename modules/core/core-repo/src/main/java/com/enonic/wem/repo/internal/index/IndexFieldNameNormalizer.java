package com.enonic.wem.repo.internal.index;

import java.util.Collection;

import com.google.common.collect.Collections2;

public class IndexFieldNameNormalizer
{
    public static String normalize( final String path )
    {
        return doNormalize( path );
    }

    private static String doNormalize( final String path )
    {
        String normalized = path;

        normalized = normalized.toLowerCase().trim();

        return normalized;
    }

    public static String[] normalize( final Collection<String> paths )
    {
        return Collections2.transform( paths, str -> doNormalize( str ) ).toArray( new String[paths.size()] );
    }

}
