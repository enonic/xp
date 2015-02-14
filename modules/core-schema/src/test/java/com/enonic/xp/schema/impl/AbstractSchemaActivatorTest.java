package com.enonic.xp.schema.impl;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import com.google.common.collect.Lists;

public abstract class AbstractSchemaActivatorTest
    extends AbstractBundleTest
{
    @Test
    public final void testProviders()
        throws Exception
    {
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        final SchemaActivator activator = new SchemaActivator();
        activator.activate( componentContext );

        startBundles( newBundle( "module1" ), newBundle( "module2" ), newBundle( "not-module" ) );
        validateProviders();

        activator.deactivate();
        validateNoProviders();
    }

    protected abstract void validateProviders()
        throws Exception;

    protected abstract void validateNoProviders()
        throws Exception;

    protected final <T> List<T> getServices( final String bundle, final Class<T> type )
        throws Exception
    {
        final ServiceReference[] refs = this.serviceRegistry.getServiceReferences( type.getName(), null );
        if ( refs == null )
        {
            return Lists.newArrayList();
        }

        final List<T> list = Lists.newArrayList();
        for ( final ServiceReference ref : refs )
        {

            final T value = getService( bundle, ref );
            if ( value != null )
            {
                list.add( value );
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    private <T> T getService( final String bundle, final ServiceReference ref )
        throws Exception
    {
        if ( bundle != null )
        {
            if ( !ref.getBundle().getSymbolicName().equals( bundle ) )
            {
                return null;
            }
        }

        return (T) this.serviceRegistry.getService( ref );
    }
}
