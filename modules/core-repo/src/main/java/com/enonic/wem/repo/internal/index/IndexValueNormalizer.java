package com.enonic.wem.repo.internal.index;

import com.google.common.base.Strings;

public class IndexValueNormalizer
{
    public static String normalize( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        return value.trim().toLowerCase();
    }

}
