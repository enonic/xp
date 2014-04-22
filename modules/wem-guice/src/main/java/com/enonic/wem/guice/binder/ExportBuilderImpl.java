package com.enonic.wem.guice.binder;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Peaberry;
import org.ops4j.peaberry.builders.ExportProvider;
import org.ops4j.peaberry.util.TypeLiterals;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;

final class ExportBuilderImpl<T>
    implements ExportBuilder<T>
{
    private final Binder binder;

    private final Map<String, Object> properties;

    private final TypeLiteral<Export<? extends T>> type;

    public ExportBuilderImpl( final Binder binder, final Class<T> type )
    {
        this.binder = binder;
        this.properties = new HashMap<>();
        this.type = TypeLiterals.export( type );
    }

    @Override
    public ExportBuilder<T> property( final String key, final Object value )
    {
        this.properties.put( key, value );
        return this;
    }

    @Override
    public ExportBuilder<T> properties( final Map<String, Object> properties )
    {
        this.properties.putAll( properties );
        return this;
    }

    @Override
    public ScopedBindingBuilder to( final Class<? extends T> type )
    {
        final ExportProvider<? extends T> provider = Peaberry.service( type ).attributes( this.properties ).export();
        return this.binder.bind( this.type ).toProvider( provider );
    }

    @Override
    public void toInstance( final T instance )
    {
        final ExportProvider<? extends T> provider = Peaberry.service( instance ).attributes( this.properties ).export();
        this.binder.bind( this.type ).toProvider( provider );
    }
}
