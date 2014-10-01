package com.enonic.wem.portal.internal;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

final class ResourceFactoryImpl
    implements ResourceFactory
{
    private final ResourceProvider provider;

    private final ResourceClass resourceClass;

    private PropertyInjector propertyInjector;

    public ResourceFactoryImpl( final ResourceProvider provider )
    {
        this.provider = provider;
        this.resourceClass = ResourceBuilder.rootResourceFromAnnotations( this.provider.getType() );
    }

    @Override
    public void registered( final ResteasyProviderFactory factory )
    {
        this.propertyInjector = factory.getInjectorFactory().createPropertyInjector( this.resourceClass, factory );
    }

    public Object createResource( final HttpRequest request, final HttpResponse response, final ResteasyProviderFactory factory )
    {
        final Object instance = this.provider.newResource();
        this.propertyInjector.inject( request, response, instance );
        return instance;
    }

    public void unregistered()
    {
        // Do nothing
    }

    public Class<?> getScannableClass()
    {
        return this.resourceClass.getClazz();
    }

    public void requestFinished( final HttpRequest request, final HttpResponse response, final Object resource )
    {
        // Do nothing
    }
}