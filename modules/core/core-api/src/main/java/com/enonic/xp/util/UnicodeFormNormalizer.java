package com.enonic.xp.util;

import java.text.Normalizer;

import com.google.common.annotations.Beta;

@Beta
public class UnicodeFormNormalizer
{
    private final static Normalizer.Form FORM = Normalizer.Form.NFC;

    public static String normalize( final String value )
    {
        if ( !Normalizer.isNormalized( value, FORM ) )
        {
            return Normalizer.normalize( value, FORM );
        }
        return value;
    }
}
