package com.enonic.xp.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.util.CamelCaseConverter;

@Beta
public class FormItemName
{
    public static String safeName( final String name )
    {
        return CamelCaseConverter.defaultConvert( name );
    }
}
