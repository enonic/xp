package com.enonic.xp.form;

import com.enonic.xp.util.CamelCaseConverter;


public final class FormItemName
{
    private FormItemName()
    {
    }

    public static String safeName( final String name )
    {
        return CamelCaseConverter.defaultConvert( name );
    }
}
