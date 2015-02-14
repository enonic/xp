package com.enonic.xp.core.form;

import com.enonic.xp.core.util.CamelCaseConverter;

public class FormItemName
{
    public static String safeName( final String name )
    {
        return CamelCaseConverter.defaultConvert( name );
    }
}
