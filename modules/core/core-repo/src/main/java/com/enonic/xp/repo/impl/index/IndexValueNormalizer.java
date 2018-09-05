package com.enonic.xp.repo.impl.index;

public class IndexValueNormalizer
{
    public static String normalize( final String value )
    {
        return value == null ? null : value.trim().toLowerCase();
    }

}
