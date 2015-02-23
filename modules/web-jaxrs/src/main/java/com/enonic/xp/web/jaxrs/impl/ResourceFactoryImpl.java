package com.enonic.xp.web.jaxrs.impl;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

import com.enonic.xp.web.jaxrs.JaxRsResourceFactory;

final class ResourceFactoryImpl
    implements ResourceFactory
{
    private final JaxRsResourceFactory factory;

    private final ResourceClass resourceClass;

    private PropertyInjector propertyInjector;

    public ResourceFactoryImpl( final JaxRsResourceFactory factory )
    {
        this.factory = factory;
        this.resourceClass = ResourceBuilder.rootResourceFromAnnotations( this.factory.getType() );
    }

    @Override
    public void registered( final ResteasyProviderFactory factory )
    {
        this.propertyInjector = factory.getInjectorFactory().createPropertyInjector( this.resourceClass, factory );
    }

    @Override
    public Object createResource( final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory factory )
    {
        final Object instance = this.factory.newResource();
        this.propertyInjector.inject( request, response, instance );
        return instance;
    }

    @Override
    public void unregistered()
    {
    }

    @Override
    public Class<?> getScannableClass()
    {
        return this.resourceClass.getClazz();
    }

    @Override
    public void requestFinished( final HttpRequest request, final HttpResponse response, final Object resource )
    {
    }
}
