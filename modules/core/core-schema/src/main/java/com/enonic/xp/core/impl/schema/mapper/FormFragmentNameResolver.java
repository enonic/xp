package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.formfragment.FormFragmentName;

final class FormFragmentNameResolver
{
    private FormFragmentNameResolver()
    {
    }

    static FormFragmentName resolve( final String value, final ApplicationKey applicationKey )
    {
        if ( value.contains( ":" ) )
        {
            throw new IllegalArgumentException( "Form fragment name must not contain ':'" );
        }
        return FormFragmentName.from( applicationKey, value );
    }
}
