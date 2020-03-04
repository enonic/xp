package com.enonic.xp.impl.task;

import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public final class OsgiSupport
{
    private OsgiSupport()
    {
    }

    public static <T, R> R withService( Class<T> serviceClass, Function<T, R> function, R noServiceValue )
    {
        final Bundle bundle = FrameworkUtil.getBundle( serviceClass );
        if ( bundle == null )
        {
            throw new RuntimeException( serviceClass + " class is not defined by a bundle class loader" );
        }
        final BundleContext bundleContext = bundle.getBundleContext();
        if ( bundleContext == null )
        {
            throw new RuntimeException( bundle + " bundle has no valid BundleContext" );
        }
        final ServiceReference<T> serviceReference = bundleContext.getServiceReference( serviceClass );
        if ( serviceReference != null )
        {
            final T service = bundleContext.getService( serviceReference );
            try
            {
                if ( service != null )
                {
                    return function.apply( service );
                }
                else
                {
                    return noServiceValue;
                }
            }
            finally
            {
                bundleContext.ungetService( serviceReference );
            }
        }
        else
        {
            return noServiceValue;
        }
    }
}
