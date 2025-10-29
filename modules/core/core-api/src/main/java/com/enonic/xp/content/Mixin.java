package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.mixin.MixinName;

@PublicApi
public final class Mixin
{
    private MixinName name;

    private PropertyTree data;

    public Mixin( final MixinName name, final PropertyTree data )
    {
        this.name = Objects.requireNonNull( name, "name cannot be null" );
        this.data = Objects.requireNonNull( data, "data cannot be null" );
    }

    public PropertyTree getData()
    {
        return data;
    }

    public void setData( final PropertyTree data )
    {
        this.data = data;
    }

    public MixinName getName()
    {
        return name;
    }

    public String getApplicationPrefix()
    {
        return name.getApplicationPrefix();
    }

    public static ApplicationKey fromApplicationPrefix( final String applicationPrefixName )
    {
        return ApplicationKey.from( applicationPrefixName, "-" );
    }

    public void setName( final MixinName name )
    {
        this.name = name;
    }

    public Mixin copy()
    {
        return new Mixin( name, data.copy() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Mixin mixin = (Mixin) o;

        if ( !name.equals( mixin.name ) )
        {
            return false;
        }

        return data.equals( mixin.data );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, data );
    }
}
