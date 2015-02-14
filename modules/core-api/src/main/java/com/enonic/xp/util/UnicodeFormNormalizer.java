package com.enonic.xp.util;

import java.text.Normalizer;

public class UnicodeFormNormalizer
{
    private final static Normalizer.Form form = Normalizer.Form.NFC;

    public static final String normalize( final String value )
    {
        if ( !Normalizer.isNormalized( value, form ) )
        {
            return Normalizer.normalize( value, form );
        }
        return value;
    }
}
