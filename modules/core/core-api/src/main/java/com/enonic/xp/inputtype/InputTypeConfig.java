package com.enonic.xp.inputtype;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InputTypeConfig
    implements Iterable<InputTypeProperty>
{
    private final ImmutableSetMultimap<String, InputTypeProperty> map;

    private InputTypeConfig( final Builder builder )
    {
        this.map = builder.map.build();
    }

    public Set<String> getNames()
    {
        return this.map.keySet();
    }

    public Set<InputTypeProperty> getProperties( final String name )
    {
        return this.map.get( name );
    }

    public Optional<InputTypeProperty> getProperty( final String name )
    {
        return getProperties( name ).stream().findFirst();
    }

    public int getSize()
    {
        return this.map.size();
    }

    @Override
    public Iterator<InputTypeProperty> iterator()
    {
        return this.map.values().iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InputTypeConfig ) )
        {
            return false;
        }
        final InputTypeConfig that = (InputTypeConfig) o;
        return map.equals( that.map );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( map );
    }

    public static InputTypeConfig empty()
    {
        return create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSetMultimap.Builder<String, InputTypeProperty> map = ImmutableSetMultimap.builder();

        private Builder()
        {
        }

        public Builder config( final InputTypeConfig config )
        {
            if ( config != null )
            {
                this.map.putAll( config.map );
            }

            return this;
        }

        public Builder property( final InputTypeProperty property )
        {
            this.map.put( property.getName(), property );
            return this;
        }

        public InputTypeConfig build()
        {
            return new InputTypeConfig( this );
        }
    }
}
