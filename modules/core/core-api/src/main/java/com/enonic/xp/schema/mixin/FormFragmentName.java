package com.enonic.xp.schema.mixin;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class FormFragmentName
    extends BaseSchemaName
    implements Comparable<FormFragmentName>
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

    public static FormFragmentName from( final String mixinName )
    {
        return new FormFragmentName( mixinName );
    }

    public String getApplicationPrefix()
    {
        return this.getApplicationKey() == null ? "" : this.getApplicationKey().toString().replace( '.', '-' );
    }

    @Override
    public int compareTo( final FormFragmentName that )
    {
        return this.toString().compareTo( that.toString() );
    }
}
