package com.enonic.wem.guice.binder;

import org.ops4j.peaberry.AttributeFilter;
import org.ops4j.peaberry.Peaberry;
import org.ops4j.peaberry.builders.InjectedServiceBuilder;
import org.ops4j.peaberry.builders.ServiceBuilder;
import org.ops4j.peaberry.util.Filters;
import org.ops4j.peaberry.util.TypeLiterals;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;

final class ImportBuilderImpl<T>
    implements ImportBuilder<T>
{
    private final Binder binder;

    private final Class<T> type;

    private AttributeFilter filter;

    public ImportBuilderImpl( final Binder binder, final Class<T> type )
    {
        this.binder = binder;
        this.type = type;
    }

    @Override
    public ImportBuilder<T> filter( final String filter )
    {
        this.filter = Filters.ldap( filter );
        return this;
    }

    @Override
    public ScopedBindingBuilder toSingle()
    {
        return this.binder.bind( this.type ).toProvider( buildServiceBuilder().single() );
    }

    @Override
    public ScopedBindingBuilder toMultiple()
    {
        final TypeLiteral<Iterable<? extends T>> literal = TypeLiterals.iterable( this.type );
        return this.binder.bind( literal ).toProvider( buildServiceBuilder().multiple() );
    }

    private ServiceBuilder<T> buildServiceBuilder()
    {
        InjectedServiceBuilder<T> builder;

        if ( this.filter != null )
        {
            builder = Peaberry.service( this.type ).filter( this.filter );
        }
        else
        {
            builder = Peaberry.service( this.type );
        }

        return builder;
    }
}
