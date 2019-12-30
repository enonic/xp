package com.enonic.xp.inputtype;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.LinkedHashMultimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.convert.Converters;

@PublicApi
public final class InputTypeConfig
    implements Iterable<InputTypeProperty>
{
    private final LinkedHashMultimap<String, InputTypeProperty> map;

    private InputTypeConfig( final Builder builder )
    {
        this.map = builder.map;
    }

    public Set<String> getNames()
    {
        return this.map.keySet();
    }

    public InputTypeProperty getProperty( final String name )
    {
        final Set<InputTypeProperty> properties = getProperties( name );
        return properties.isEmpty() ? null : properties.iterator().next();
    }

    public Set<InputTypeProperty> getProperties( final String name )
    {
        return this.map.get( name );
    }

    private Stream<InputTypeProperty> findProperties( final String name, final Predicate<InputTypeProperty> filter )
    {
        return getProperties( name ).stream().filter( filter );
    }

    public String getValue( final String name )
    {
        final InputTypeProperty property = getProperty( name );
        return property != null ? property.getValue() : null;
    }

    public <T> T getValue( final String name, final Class<T> type )
    {
        return getValue( name, type, null );
    }

    public <T> T getValue( final String name, final Class<T> type, final T defValue )
    {
        final String value = getValue( name );
        if ( value == null )
        {
            return defValue;
        }

        final T converted = Converters.convert( value, type );
        return converted != null ? converted : defValue;
    }

    public boolean hasValue( final String name, final String value )
    {
        return findProperties( name, property -> Objects.equals( value, property.getValue() ) ).count() > 0;
    }

    public boolean hasAttributeValue( final String name, final String attr, final String attrValue )
    {
        return findProperties( name, property -> Objects.equals( attrValue, property.getAttribute( attr ) ) ).count() > 0;
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

    public static class Builder
    {
        private final LinkedHashMultimap<String, InputTypeProperty> map;

        private Builder()
        {
            this.map = LinkedHashMultimap.create();
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
