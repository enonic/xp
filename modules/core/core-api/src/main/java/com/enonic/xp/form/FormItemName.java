package com.enonic.xp.form;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.CamelCaseConverter;

@PublicApi
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
