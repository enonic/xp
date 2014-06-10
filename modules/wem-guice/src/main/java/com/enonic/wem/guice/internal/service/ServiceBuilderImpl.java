package com.enonic.wem.guice.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.peaberry.AttributeFilter;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Peaberry;
import org.ops4j.peaberry.builders.ExportProvider;
import org.ops4j.peaberry.builders.InjectedServiceBuilder;
import org.ops4j.peaberry.util.Filters;
import org.ops4j.peaberry.util.TypeLiterals;
import org.osgi.framework.Constants;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import com.enonic.wem.guice.ServiceBuilder;

final class ServiceBuilderImpl<T>
    implements ServiceBuilder<T>
{
    private final Binder binder;

    private final Map<String, Object> attributes;

    private final Class<T> type;

    private AttributeFilter filter;

    public ServiceBuilderImpl( final Binder binder, final Class<T> type )
    {
        this.binder = binder;
        this.attributes = new HashMap<>();
        this.type = type;
    }

    @Override
    public ServiceBuilder<T> attribute( final String key, final Object value )
    {
        this.attributes.put( key, value );
        return this;
    }

    @Override
    public ServiceBuilder<T> filter( final String filter )
    {
        this.filter = Filters.ldap( filter );
        return this;
    }

    @Override
    public void importSingle()
    {
        this.binder.bind( this.type ).toProvider( buildInjectedServiceBuilder().single() );
    }

    @Override
    public void importMultiple()
    {
        final TypeLiteral<Iterable<? extends T>> literal = TypeLiterals.iterable( this.type );
        this.binder.bind( literal ).toProvider( buildInjectedServiceBuilder().multiple() );
    }

    @Override
    public void export()
    {
        final ExportProvider<? extends T> provider = Peaberry.service( this.type ).attributes( this.attributes ).export();
        final TypeLiteral<Export<? extends T>> exportType = TypeLiterals.export( this.type );
        this.binder.bind( exportType ).annotatedWith( Names.named( this.type.getName() ) ).toProvider( provider );
    }

    @Override
    public final void exportAs( final Class<? super T> iface )
    {
        attribute( Constants.OBJECTCLASS, iface.getName() );
        export();
    }

    private InjectedServiceBuilder<T> buildInjectedServiceBuilder()
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
