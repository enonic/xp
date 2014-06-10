package com.enonic.wem.guice.internal.service;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;

import com.google.inject.Binding;
import com.google.inject.Injector;

import com.enonic.wem.guice.internal.util.InjectorUtils;

public final class ServiceManager
{
    private final Injector injector;

    private final List<Export<?>> exports;

    public ServiceManager( final Injector injector )
    {
        this.injector = injector;
        this.exports = new ArrayList<>();
    }

    public void exportAll()
    {
        InjectorUtils.findBindings( this.injector, Export.class ).forEach( this::export );
    }

    private void export( final Binding<?> binding )
    {
        this.exports.add( (Export<?>) binding.getProvider().get() );
    }

    public void unexportAll()
    {
        this.exports.forEach( Import::unget );
    }
}
