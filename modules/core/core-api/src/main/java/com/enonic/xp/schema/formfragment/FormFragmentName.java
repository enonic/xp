package com.enonic.xp.schema.formfragment;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class FormFragmentName
    extends BaseSchemaName
{
    private FormFragmentName( final String name )
    {
        super( name );
    }

    private FormFragmentName( final ApplicationKey applicationKey, final String localName )
    {
        super( applicationKey, localName );
    }

    public static FormFragmentName from( final ApplicationKey applicationKey, final String localName )
    {
        return new FormFragmentName( applicationKey, localName );
    }

    public static FormFragmentName from( final String formFragmentName )
    {
        return new FormFragmentName( formFragmentName );
    }
}
