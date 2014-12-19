package com.enonic.wem.api.form;

import com.enonic.wem.api.util.CamelCaseConverter;

public class FormItemName
{
    public static String safeName( final String name )
    {
        return CamelCaseConverter.defaultConvert( name );
    }
}
