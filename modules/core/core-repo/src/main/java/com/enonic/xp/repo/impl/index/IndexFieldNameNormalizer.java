package com.enonic.xp.repo.impl.index;

import java.util.Collection;

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
        return paths.stream().map( IndexFieldNameNormalizer::doNormalize ).toArray( String[]::new );
    }

}
