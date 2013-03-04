package com.enonic.wem.core.lifecycle;

import com.google.inject.Binding;
import com.google.inject.ProvisionException;
import com.google.inject.spi.ProvisionListener;

final class InitializingHandler
    implements ProvisionListener
{
    @Override
    public <T> void onProvision( final ProvisionInvocation<T> provision )
    {
        final Binding<?> binding = provision.getBinding();
        final Class<?> clz = binding.getKey().getTypeLiteral().getRawType();

        if ( InitializingBean.class.isAssignableFrom( clz ) )
        {
            initialize( (InitializingBean) provision.provision() );
        }
    }

    private void initialize( final InitializingBean bean )
    {
        try
        {
            bean.afterPropertiesSet();
        }
        catch ( final Exception e )
        {
            throw new ProvisionException( "An error occurred while initializing " + bean.getClass().getName(), e );
        }
    }
}
