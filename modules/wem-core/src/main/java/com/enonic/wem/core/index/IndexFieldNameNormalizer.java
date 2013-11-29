package com.enonic.wem.core.index;

public class IndexFieldNameNormalizer
{
    public static final String FIELD_PATH_SEPARATOR = ".";

    public static final String INDEX_PATH_SEPARATOR = "_";

    public static String normalize( final String path )
    {
        String normalized = path;

        normalized = normalized.toLowerCase();
        normalized = normalized.replace( FIELD_PATH_SEPARATOR, INDEX_PATH_SEPARATOR );

        return normalized;
    }
}
