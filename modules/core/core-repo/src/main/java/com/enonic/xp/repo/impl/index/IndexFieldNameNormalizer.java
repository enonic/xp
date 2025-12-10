package com.enonic.xp.repo.impl.index;

import com.enonic.xp.index.IndexPath;

public class IndexFieldNameNormalizer
{
    public static String normalize( final String path )
    {
        return IndexPath.from( path ).getPath();
    }
}
