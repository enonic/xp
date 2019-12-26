package com.enonic.xp.form;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.CamelCaseConverter;

@PublicApi
public class FormItemName
{
    public static String safeName( final String name )
    {
        return CamelCaseConverter.defaultConvert( name );
    }
}
